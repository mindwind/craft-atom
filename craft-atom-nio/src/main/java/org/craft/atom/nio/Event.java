package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.Handler;

/**
 * I/O event.
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-15
 */
public class Event {
	
	private static final Log LOG = LogFactory.getLog(Event.class);
	
	private final EventType type;
    private final Session session;
    private final Object parameter;
    private Handler handler;
    
    public Event(EventType type, Session session, Object parameter, Handler handler) {
        if (type == null) {
            throw new IllegalArgumentException("type == null");
        }
        if (session == null) {
            throw new IllegalArgumentException("session == null");
        }
        if (handler == null) {
        	throw new IllegalArgumentException("handler == null");
        }
        
        this.type = type;
        this.session = session;
        this.parameter = parameter;
        this.handler = handler;
    }
    
    // ~ ---------------------------------------------------------------------------------------------------------------

	public void fire() {
		try {
			fire0();
		} catch (Throwable t1) {
			try {
				handler.exceptionCaught(session, t1);
			} catch (Throwable t2) {
				LOG.warn("Catch hanlder.exceptionCaught() thrown exception, log warn and drop it", t2);
			}
		}
	}

	private void fire0() {
		switch (type) {
		case MESSAGE_RECEIVED:
			handler.messageReceived(session, (byte[]) parameter);
			break;
		case MESSAGE_SENT:
			handler.messageSent(session, (byte[]) parameter);
			break;
		case EXCEPTION_CAUGHT:
			handler.exceptionCaught(session, (Throwable) parameter);
			break;
		case SESSION_IDLE:
			handler.sessionIdle(session);
			break;
		case SESSION_OPENED:
			handler.sessionOpened(session);
			break;
		case SESSION_CLOSED:
			handler.sessionClosed(session);
			break;
		default:
			throw new IllegalArgumentException("Unknown event type: " + type);
		}
	}
	
	// ~ ---------------------------------------------------------------------------------------------------------------

	public EventType getType() {
		return type;
	}

	public Session getSession() {
		return session;
	}

	public Object getParameter() {
		return parameter;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

}
