package pl.polsl.workinghours.data.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    private static String TAG = "Encryptor";
    private static byte[] KEY = "5492720CAB66C8258671992848527BD9".getBytes();

    private SecretKeySpec skeySpec;

    public Encryptor() {
        this.skeySpec = new SecretKeySpec(KEY, "AES");
    }

    public byte[] encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, this.skeySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return encrypted;
    }

    public String decrypt(byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, this.skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
}
