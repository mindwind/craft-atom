package org.craft.atom.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES util class
 * 
 * @author Hu Feng
 * @version 1.0, 2011-10-12
 */
@Deprecated
public class DESUtil {

	private static final String KEY_ALGORITHM = "DES";
	private static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

	/**
	 * Generate key by seed.
	 * 
	 * @param seed
	 * @return
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static String generateKey(String seed) throws IOException, GeneralSecurityException {
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(BASE64Util.decode(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		kg.init(secureRandom);
		SecretKey secretKey = kg.generateKey();
		
		return BASE64Util.encode(secretKey.getEncoded());
	}

	/**
	 * Convert to {@link Key} object
	 * 
	 * @param key
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static Key toKey(byte[] key) throws GeneralSecurityException {
		DESKeySpec des = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(des);
		return secretKey;
	}

	/**
	 * encrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] encrypt(byte[] key, byte[] data) throws GeneralSecurityException {
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}
	
	/**
	 * encrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static String encrypt(String key, String data) throws GeneralSecurityException {
		Key k = toKey(key.getBytes());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return BASE64Util.encode(cipher.doFinal(data.getBytes()));
	}
	
	/**
	 * decrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] decrypt(byte[] key, byte[] data) throws GeneralSecurityException {
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}
	
	/**
	 * decrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException 
	 */
	public static String decrypt(String key, String data) throws IOException, GeneralSecurityException { 
		byte[] bytes = BASE64Util.decode(data);
		Key k = toKey(key.getBytes());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return new String(cipher.doFinal(bytes));
	}

	private DESUtil() {}

}
