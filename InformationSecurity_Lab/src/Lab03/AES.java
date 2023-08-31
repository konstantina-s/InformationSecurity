package Lab03;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

//AES class used in previous Lab exercises
public class AES{
    private SecretKeySpec secretKeySpec;

    public byte[] decrypt(byte[] msg){
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(msg);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encrypt(byte[] msg){
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(msg);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setKey(byte[] key){
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, "AES");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
