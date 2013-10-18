package org.craft.atom.protocol.ssl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.ws.ProtocolException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.util.buffer.AdaptiveByteBuffer;


/**
 * A easy use class using the SSLEngine API to decrypt/encrypt data.
 * 
 * @author mindwind
 * @version 1.0, Oct 17, 2013
 */
@ToString
public class SslCodec {
	
	/** Set true if the engine will <em>request</em> client authentication.This option is only useful to engines in the server mode.*/
	@Getter @Setter private boolean                         wantClientAuth;   
	
	/** Set true if the engine will <em>require</em> client authentication.This option is only useful to engines in the server mode.*/
	@Getter @Setter private boolean                         needClientAuth;    
	
	/** Set true if the engine is set to use client mode when handshaking */
	@Getter @Setter private boolean                         clientMode;
	
	/** The cipher suites to be enabled when {@link SSLEngine} is initialized. <tt>null</tt> means use {@link SSLEngine}'s default. */
	@Getter @Setter private String[]                        enabledCipherSuites;
	
	/** The protocols to be enabled when {@link SSLEngine} is initialized.<tt>null</tt> means use {@link SSLEngine}'s default.*/
	@Getter @Setter private String[]                        enabledProtocols;
	
	/** Encrypted data from the net */
    private AdaptiveByteBuffer                              inNetBuffer;
    
    /** Encrypted data to be written to the net */
    private AdaptiveByteBuffer                              outNetBuffer;
    
    /** Application cleartext data to be read by application */
    private AdaptiveByteBuffer                              appBuffer;
    
    /** Empty buffer used during initial handshake and close operations */
    private final AdaptiveByteBuffer                        emptyBuffer         = AdaptiveByteBuffer.allocate(0);
    
    /** A flag set to true when a SSL Handshake has been completed */
    private boolean                                         handshakeComplete;
    
    private SslHandshakeHandler                             handshakeHandler;
	
    
	@Getter @Setter private InetSocketAddress               peer;
	@Getter @Setter private SSLContext                      sslContext;
	@Getter @Setter private SSLEngine                       sslEngine;
	@Getter         private SSLEngineResult.HandshakeStatus handshakeStatus;

	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
	public SslCodec(SSLContext sslContext, SslHandshakeHandler sslHandler) {
		this.sslContext       = sslContext;
		this.handshakeHandler = sslHandler;
	}
	
	public void init() {
		if (peer == null) {
            sslEngine = sslContext.createSSLEngine();
        } else {
            sslEngine = sslContext.createSSLEngine(peer.getHostName(), peer.getPort());
        }
		
		sslEngine.setUseClientMode(clientMode);
		
		// these parameters are only valid when in server mode
		if (!clientMode) {
			sslEngine.setWantClientAuth(wantClientAuth);
			sslEngine.setNeedClientAuth(needClientAuth);
		}
		
		if (enabledCipherSuites != null) {
			sslEngine.setEnabledCipherSuites(enabledCipherSuites);
		}
		if (enabledProtocols != null) {
			sslEngine.setEnabledProtocols(enabledProtocols);
		}
		
		try {
			sslEngine.beginHandshake();
		} catch (SSLException e) {
			throw new ProtocolException(e);
		}
		handshakeStatus   = sslEngine.getHandshakeStatus();
		handshakeComplete = false;
	}
	
	
	// ~ -----------------------------------------------------------------------------------------------------------

	
	/**
	 * Decode for ssl encrypt data
	 * 
	 * @param data
	 * @return
	 */
	public byte[] decode(byte[] data) {
		if (data == null) {
			return null;
		}
		
		try {
			byte[] out = null;
			int len = data.length;
			if (inNetBuffer == null) {
	            inNetBuffer = AdaptiveByteBuffer.allocate(len).setAutoExpand(true);
	        }
			
			inNetBuffer.put(data);
			if (!handshakeComplete) {
	            handshake0();
	        } else {
	        	// Prepare the net data for reading.
	            inNetBuffer.flip();
	            
	            if (!inNetBuffer.hasRemaining()) {
	                return null;
	            }
	            
	            SSLEngineResult res = unwrap();

	            // prepare to be written again
	            if (inNetBuffer.hasRemaining()) {
	                inNetBuffer.compact();
	            } else {
	                inNetBuffer = null;
	            }

	            checkStatus(res);
	            renegotiateIfNeeded(res);
	            out = getBytes(fetchAppBuffer());
	        }
		
			if (isInboundDone()) {
				inNetBuffer = null;
			}
			return out;
		} catch (Exception e) {
			throw new ProtocolException(e);
		}
	}
	
	private void handshake0() throws SSLException {
		for (;;) {
			switch (handshakeStatus) {
			case FINISHED:
			case NOT_HANDSHAKING:
				handshakeComplete = true;
				return;
			case NEED_TASK:
				handshakeStatus = doTasks();
                break;
			case NEED_UNWRAP:
				SSLEngineResult.Status status = unwrapHandshake();
				if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW && handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED || isInboundDone()) {
					// Need more data
					return;
				}
				break;
			case NEED_WRAP:
				// First make sure that the out buffer is completely empty.Since we cannot call wrap with data left on the buffer
                if (outNetBuffer != null && outNetBuffer.hasRemaining()) {
                	return;
                }
                SSLEngineResult result;
                createOutNetBuffer(0);

                for (;;) {
                    result = sslEngine.wrap(emptyBuffer.buf(), outNetBuffer.buf());
                    if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                        outNetBuffer.capacity(outNetBuffer.capacity() << 1);
                        outNetBuffer.limit(outNetBuffer.capacity());
                    } else {
                        break;
                    }
                }

                outNetBuffer.flip();
                handshakeStatus = result.getHandshakeStatus();
                writeNetBuffer();
                break;
			default:
				String msg = "Invalid handshaking state" + handshakeStatus + " while processing the Handshake for session.";
				throw new IllegalStateException(msg);
			}
		}
	}
	
	private void writeNetBuffer() throws SSLException {
        // Check if any net data needed to be writen
        if (outNetBuffer == null || !outNetBuffer.hasRemaining()) {
            return;
        }
        
        AdaptiveByteBuffer writeBuffer = fetchOutNetBuffer();
        handshakeHandler.needWrite(getBytes(writeBuffer));
       
        // loop while more writes required to complete handshake
        while (needToCompleteHandshake()) {
            try {
                handshake0();
            } catch (SSLException ssle) {
                SSLException newSsle = new SSLHandshakeException("SSL handshake failed.");
                newSsle.initCause(ssle);
                throw newSsle;
            }

            AdaptiveByteBuffer outNetBuffer = fetchOutNetBuffer();
            if (outNetBuffer != null && outNetBuffer.hasRemaining()) {
            	handshakeHandler.needWrite(getBytes(writeBuffer));
            }
        }
    }
	
	private void createOutNetBuffer(int expectedRemaining) {
        // SSLEngine requires us to allocate unnecessarily big buffer even for small data. *Shrug*
        int capacity = Math.max(expectedRemaining, sslEngine.getSession().getPacketBufferSize());
        if (outNetBuffer != null) {
            outNetBuffer.capacity(capacity);
        } else {
            outNetBuffer = AdaptiveByteBuffer.allocate(capacity).minimumCapacity(0);
        }
    }
	
	private byte[] getBytes(AdaptiveByteBuffer buf) {
		int len = buf.remaining();
		if (len == 0) {
			return null;
		}
		byte[] bytes = new byte[len];
		buf.get(bytes);
		return bytes;
	}
	
	/**
     * Get encrypted data to be sent.
     * 
     * @return buffer with data
     */
    private AdaptiveByteBuffer fetchOutNetBuffer() {
    	AdaptiveByteBuffer answer = outNetBuffer;
        if (answer == null) {
            return emptyBuffer;
        }

        outNetBuffer = null;
        return answer.shrink();
    }
    
    /**
     * Get decrypted application data.
     * 
     * @return buffer with data
     */
    private AdaptiveByteBuffer fetchAppBuffer() {
    	AdaptiveByteBuffer appBuffer = this.appBuffer.flip();
        this.appBuffer = null;
        return appBuffer;
    }
	
	private boolean isInboundDone() {
        return sslEngine == null || sslEngine.isInboundDone();
    }
	
	/**
     * Check if there is any need to complete handshake.
     */
    private boolean needToCompleteHandshake() {
        return handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP && !isInboundDone();
    }
	
	private SSLEngineResult.Status unwrapHandshake() throws SSLException {
		// Prepare the net data for reading.
		if (inNetBuffer != null) {
			inNetBuffer.flip();
		}

		if (inNetBuffer == null || !inNetBuffer.hasRemaining()) {
			// Need more data.
			return SSLEngineResult.Status.BUFFER_UNDERFLOW;
		}

		SSLEngineResult res = unwrap();
		handshakeStatus = res.getHandshakeStatus();

		checkStatus(res);

		// If handshake finished, no data was produced, and the status is still ok, try to unwrap more
		if (handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED
				&& res.getStatus() == SSLEngineResult.Status.OK
				&& inNetBuffer.hasRemaining()) {
			res = unwrap();

			// prepare to be written again
			if (inNetBuffer.hasRemaining()) {
				inNetBuffer.compact();
			} else {
				inNetBuffer = null;
			}

			renegotiateIfNeeded(res);
		} else {
			// prepare to be written again
			if (inNetBuffer.hasRemaining()) {
				inNetBuffer.compact();
			} else {
				inNetBuffer = null;
			}
		}

		return res.getStatus();
	}
	
	private void renegotiateIfNeeded(SSLEngineResult res) throws SSLException {
		if (   (res.getStatus()           != SSLEngineResult.Status.CLOSED)           
		    && (res.getStatus()           != SSLEngineResult.Status.BUFFER_UNDERFLOW) 
			&& (res.getHandshakeStatus()  != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)) {
			// Renegotiation required.
			handshakeComplete = false;
			handshakeStatus = res.getHandshakeStatus();
			handshake0();
		}
	}
	
	/** 
	 * Decrypt the incoming buffer and move the decrypted data to an application buffer. 
	 */
    private SSLEngineResult unwrap() throws SSLException {
        // We first have to create the application buffer if it does not exist
        if (appBuffer == null) {
            appBuffer = AdaptiveByteBuffer.allocate(inNetBuffer.remaining());
        } else {
            // We already have one, just add the new data into it
            appBuffer.expand(inNetBuffer.remaining());
        }

        SSLEngineResult res;

        Status status = null;
        HandshakeStatus handshakeStatus = null;

        do {
            // Unwrap the incoming data
            res = sslEngine.unwrap(inNetBuffer.buf(), appBuffer.buf());
            status = res.getStatus();

            // We can be processing the Handshake
            handshakeStatus = res.getHandshakeStatus();
            if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                // We have to grow the target buffer, it's too small.Then we can call the unwrap method again
                appBuffer.capacity(appBuffer.capacity() << 1);
                appBuffer.limit(appBuffer.capacity());
                continue;
            }
        } while (((status == SSLEngineResult.Status.OK) || (status == SSLEngineResult.Status.BUFFER_OVERFLOW)) && ((handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) || (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)));

        return res;
    }
    
    private void checkStatus(SSLEngineResult res) throws SSLException {
        SSLEngineResult.Status status = res.getStatus();

        /*
         * The status may be:
         * OK          - Normal operation
         * OVERFLOW    - Should never happen since the application buffer is sized to hold the maximum packet size.
         * UNDERFLOW   - Need to read more data from the socket. It's normal.
         * CLOSED      - The other peer closed the socket. Also normal.
         */
        if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
            throw new SSLException("SSLEngine error during decrypt: " + status + " inNetBuffer: " + inNetBuffer + "appBuffer: " + appBuffer);
        }
    }
	
	private SSLEngineResult.HandshakeStatus doTasks() {
		Runnable runnable;
		while ((runnable = sslEngine.getDelegatedTask()) != null) {
			runnable.run();
		}
		return sslEngine.getHandshakeStatus();
	}
	
	/**
	 * Initiate ssl handshake
	 */
	public void handshake() {
		try {
			handshake0();
		} catch (Exception e) {
			throw new ProtocolException(e);
		}
	}
	

	/**
	 * Encode data to ssl encrypt data
	 * 
	 * @param data
	 * @return
	 */
	public byte[] encode(byte[] data) {
		if (data == null) {
			return null;
		}
		
		if (!handshakeComplete) {
			throw new IllegalStateException();
		}
		
		ByteBuffer src = ByteBuffer.wrap(data);
		createOutNetBuffer(src.remaining());
		
		try {
	        // Loop until there is no more data in src
	        while (src.hasRemaining()) {
	            SSLEngineResult result = sslEngine.wrap(src, outNetBuffer.buf());
	            if (result.getStatus() == SSLEngineResult.Status.OK) {
	                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
	                    doTasks();
	                }
	            } else if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
	                outNetBuffer.capacity(outNetBuffer.capacity() << 1);
	                outNetBuffer.limit(outNetBuffer.capacity());
	            } else {
	                throw new SSLException("SSLEngine error during encrypt: " + result.getStatus() + " src: " + src + "outNetBuffer: " + outNetBuffer);
	            }
	        }
	        outNetBuffer.flip();
	        return getBytes(fetchOutNetBuffer());
		} catch (Exception e) {
			throw new ProtocolException(e);
		}
	}

}
