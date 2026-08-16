/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Uddanda Technologies
 */
public class Encryption {

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String getEncryptedString(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		if (text != null)
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}

	public static String getEncyptedPassword(String pass) {
		String decrypted = null;
		try {
			/* BASE64Encoder decoder = new BASE64Encoder(); */
			// encrypt the text
			decrypted = AES.symmetricEncrypt(pass, TpoUtil.getKeyInfo());
			System.err.println(decrypted);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return decrypted;

	}

	public static String getDecryptPassword(String password) {
		/* BASE64Decoder decoder = new BASE64Decoder(); */
		String decodedPassword = null;
		try {
			decodedPassword = AES.symmetricDecrypt(password, TpoUtil.getKeyInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decodedPassword;
	}

	public static void main(String[] args) {
		String encrypted = Encryption.getEncyptedPassword("nG/NkQjAgmaix9R2fLUrPA==");
		System.out.println(encrypted);
		System.out.println(Encryption.getDecryptPassword(encrypted));
		System.out.println(Encryption.getDecryptPassword("fQ+ZtxTGIPzKy1oJXCZANt2w6ETRKMb28Cl8/RhXBFg="));
	}

}
