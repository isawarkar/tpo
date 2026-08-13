package com.util;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

    public class AES {
        public static String symmetricEncrypt(String text, String secretKey) {
            byte[] raw;
            String encryptedString;
            SecretKeySpec skeySpec;
            byte[] encryptText = text.getBytes();
            Cipher cipher;
            try {
                raw = Base64.decodeBase64(secretKey.getBytes());
                skeySpec = new SecretKeySpec(raw, "AES");
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                encryptedString = new String(Base64.encodeBase64(cipher.doFinal(encryptText)));
            } 
            catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
            return encryptedString;
        }

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

        public static void main(String[] args) {
            String secretKey = TpoUtil.geyKeyInfo();
            //String value1= "He**o@19**";
            String value1= "whar qnvh tfqg mnjc";
            String decryptedValue1 = symmetricEncrypt(value1, secretKey);
            System.out.println(decryptedValue1);

            /**  Decryption of  enctypedValue1 **/
            String decryptedValue2 = symmetricDecrypt("2yoPe/0S3eRO4aTR/x5D6pdfnu0ja5JrzXwA3HE0SDZT+z6Onh846tWG3mFdkIJGlH+LO3wTX3WZsVFzGu0mPb5yQy1JIbyZVv4FYdoiGyk=", secretKey);
            System.out.println(decryptedValue2);
            
            System.out.println("hhhh" + Base64.encodeBase64("I am smart then you".getBytes()));

        }
    }