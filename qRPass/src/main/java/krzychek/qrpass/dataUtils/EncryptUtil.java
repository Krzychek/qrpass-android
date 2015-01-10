package krzychek.qrpass.dataUtils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptUtil {
    private SecretKey secKey;
    private Cipher cipher;

    public EncryptUtil(String base64key) {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        byte[] keyArray = Base64.decode(base64key,Base64.DEFAULT);
        secKey = new SecretKeySpec(keyArray, "AES");
    }

    public String encrypt(String str) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secKey);
            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));

            return Base64.encodeToString(iv, Base64.NO_WRAP)
                    + "|" + Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}