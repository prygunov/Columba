package net.artux.columba;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    public Security(String privateKey){
        secretKey = new SecretKeySpec(privateKey.getBytes(), ALGORITHM);
    }

    public static String generatePrivateKey(){
        RandomString randomString = new RandomString(16);
        return randomString.nextString();
    }

    public String encrypt(String message)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return toBase64(cipher.doFinal(message.getBytes("UTF-8")));
    }

    public String decrypt(String cipherText)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        byte[] bytes = fromBase64(cipherText);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(bytes), "UTF-8");
    }


    public String toBase64(byte[] arr) {
        return new String(
                android.util.Base64.encode(arr, android.util.Base64.DEFAULT),
                StandardCharsets.UTF_8);
    }


    public byte[] fromBase64(String base) {
        return android.util.Base64.decode(base, android.util.Base64.DEFAULT);
    }
}
