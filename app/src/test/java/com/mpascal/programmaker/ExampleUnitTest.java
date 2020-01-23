package com.mpascal.programmaker;

import org.junit.Test;
import com.mpascal.programmaker.AESHelper;

import java.security.Key;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void encryption() {
        String admin = "Admin";

        SecretKey secretKey = AESHelper.setKey();

        String stringKey = ConvertKeyToString(secretKey);
        SecretKey decodedKey = ConvertStringToKey(stringKey);

        String adminEnc = AESHelper.encrypt(admin, secretKey);
        String adminDec = AESHelper.decrypt(adminEnc, decodedKey);

        System.out.println("Encrypted " + adminEnc);

        assertEquals("Admin", adminDec);
    }

    public static String ConvertKeyToString(SecretKey key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

    public static SecretKey ConvertStringToKey(String encodedKey) {
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }
}