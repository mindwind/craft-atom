package org.craft.atom.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA util class.
 * 
 * @author Hu Feng
 * @version 1.0, 2011-10-12
 */
@SuppressWarnings("deprecation")
public class RSAUtil {

	/**
	 * Generate key pair
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair() throws GeneralSecurityException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		final int KEY_SIZE = 1024;
		keyPairGen.initialize(KEY_SIZE, new SecureRandom());
		KeyPair keyPair = keyPairGen.genKeyPair();
		return keyPair;
	}

	/**
	 * Convert to {@link PublicKey} object
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static Key toPublicKey(String key) throws IOException, GeneralSecurityException, InvalidKeySpecException {
		byte[] keyBytes = BASE64Util.decode(key);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		return publicKey;
	}

	/**
	 * Convert {@link Key} object to string
	 * 
	 * @param key
	 * @return
	 */
	public static String keyToStr(Key key) {
		return BASE64Util.encode(key.getEncoded());
	}

	/**
	 * Convert to {@link PrivateKey} object
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static Key toPrivateKey(String key) throws IOException, GeneralSecurityException {
		byte[] keyBytes = BASE64Util.decode(key);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		return privateKey;
	}

	/**
	 * Encrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] encrypt(Key key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(data);
	}

	/**
	 * Encrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static String encrypt(String key, String data) throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, toPublicKey(key));

		return BASE64Util.encode(cipher.doFinal(data.getBytes()));
	}

	/**
	 * Decrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static String decrypt(String key, String data) throws IOException, GeneralSecurityException {
		byte[] bytes = BASE64Util.decode(data);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, toPrivateKey(key));

		return new String(cipher.doFinal(bytes));
	}

	/**
	 * Decrypt
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] decrypt(Key key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);

		return cipher.doFinal(data);
	}

	private RSAUtil() {}

}
