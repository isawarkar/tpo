package com.ut.fbn.admin.util;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

    public class AES {
        public static String symmetricDecrypt(String text, String secretKey) {
            Cipher cipher;
            String encryptedString;
            byte[] encryptText = null;
            byte[] raw;
            SecretKeySpec skeySpec;
            try {
                raw = Base64.decodeBase64(secretKey.getBytes());
                skeySpec = new SecretKeySpec(raw, "AES");
                encryptText = Base64.decodeBase64(text.getBytes());
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                encryptedString = new String(cipher.doFinal(encryptText));
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
            return encryptedString;
        }
    }