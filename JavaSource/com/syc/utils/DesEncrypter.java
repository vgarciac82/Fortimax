package com.syc.utils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;


import org.apache.log4j.Logger;

public class DesEncrypter {

	private static final Logger log = Logger.getLogger(DesEncrypter.class);
	
	public static final String PASSWORD = "FORTIMAXSYC";
	private Cipher ecipher;

	private Cipher dcipher;

	// 8-byte Salt
	private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
			(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

	// Iteration count
	private int iterationCount = 19;

	public DesEncrypter(String passPhrase) {
		try {
			// Create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt,
					iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());

			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iterationCount);

			// Create the ciphers
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (java.security.InvalidAlgorithmParameterException e) {
			log.error(e,e);
		} catch (java.security.spec.InvalidKeySpecException e) {
			log.error(e,e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			log.error(e,e);
		} catch (java.security.NoSuchAlgorithmException e) {
			log.error(e,e);
		} catch (java.security.InvalidKeyException e) {
			log.error(e,e);
		}
	}

//	@SuppressWarnings("hiding")
	public synchronized String encrypt(String str) {
		System.out.println("ENCRIPTA:  "+ str);
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return new String(Base64.encodeBase64(enc));
		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {	log.error(e,e);

		} catch (UnsupportedEncodingException e) {	log.error(e,e);

		} catch (IOException e) {	log.error(e,e);

		}
		return null;
	}

	public synchronized String decrypt(String str) {
		try {
			// Decode base64 to get bytes
			
			byte[] dec = Base64.decodeBase64(str.getBytes());

			// Decrypt
			byte[] utf8 = dcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {	log.error(e,e);

		} catch (UnsupportedEncodingException e) {	log.error(e,e);

		} catch (java.io.IOException e) {
		}
		return null;
	}
	
	
	
	public static Map<String, String> decripth(String strBase64)
	throws IOException {
		
						if (strBase64 == null) {
							
							return null;
						}
						
						// El formato esperado es: encrypt-key:=data-encrypted
						if (strBase64.indexOf(":=") == -1)
							strBase64 = URLDecoder.decode(strBase64, "UTF-8");
						
						String[] b64Array = strBase64.split(":=");
						if (b64Array.length < 2) {
							
							return null;
						}
						
						byte[] bKey = DatatypeConverter.parseBase64Binary(b64Array[0]);
						byte[] bData = DatatypeConverter.parseBase64Binary(b64Array[1]);
						
						Map<String, String> param = new HashMap<String, String>();
						try {
							// Convierte el String encrypt-key a SecretKey
							DESedeKeySpec keyspec = new DESedeKeySpec(bKey);
							SecretKeyFactory keyfactory = SecretKeyFactory
									.getInstance("DESede");
							SecretKey key = keyfactory.generateSecret(keyspec);
						
							// Des-encripta los datos enviados
							Cipher cipher = Cipher.getInstance("DESede");
							cipher.init(Cipher.DECRYPT_MODE, key);
							byte[] clearData = cipher.doFinal(bData);
						
							// Formato esperado de data-encrypted: name=value[&[name=value...]]
							// name/value: Deben ser escapados segun la especificacion HTML 4.01
							// Ver: http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
							String[] valueNamePairs = new String(clearData).split("&");
							for (int i = 0; i < valueNamePairs.length; i++) {
								String[] pair = valueNamePairs[i].split("=");
								param.put(pair[0], pair[1]);
							}
						} catch (Exception exc) {	log.error(exc,exc);
						
							
							param = null;
							return null;
						}
						
						return param;

}
	
}