package net.artux.columba;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        try {
            String key = Security.generatePrivateKey();
            Security security = new Security(key);
            System.out.println(key);
            String s = security.encrypt("ss");
            System.out.println(s);
            assertEquals(security.decrypt(s), "ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}