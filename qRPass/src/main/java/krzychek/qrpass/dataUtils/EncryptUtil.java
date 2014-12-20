package krzychek.qrpass.dataUtils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptUtil {
    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_SIZE = 256;
    private byte[] iv;
    private SecretKey secKey;
    private Cipher cipher;

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public EncryptUtil(String passPhrase, String hexSalt, String hexIV) {
        this.iv = hexStringToByteArray(hexIV);
        try {
            // get cipher instance
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // generate key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), hexStringToByteArray(hexSalt),
                    ITERATION_COUNT, KEY_SIZE);
            this.secKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public EncryptUtil() {
        String hexSalt = "616be707c95cc08deb05cc27a809189d6bf0db8fa4ca8b8994a2c70b6d542239";
        String hexIV = "e24589a9fda0999fb8b6bfdebdade8b6";
        String secKey = "DuIhstWPxjTcmS8j";
        this.iv = hexStringToByteArray(hexIV);

        try {
            // get cipher instance
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // generate key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(secKey.toCharArray(), hexStringToByteArray(hexSalt),
                    ITERATION_COUNT, KEY_SIZE);
            this.secKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
	
	public String encrypt(String str) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secKey, new IvParameterSpec(iv));
            byte[] result = cipher.doFinal(str.getBytes("UTF-8"));

            return Base64.encodeToString(result, Base64.DEFAULT);
        }
        catch (InvalidKeyException | InvalidAlgorithmParameterException |
                BadPaddingException | IllegalBlockSizeException |
                UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}