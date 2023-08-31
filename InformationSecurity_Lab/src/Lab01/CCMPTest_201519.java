package Lab01;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CCMPTest_201519{
    public static void main(String[] args) {

        //Random generated addresses:
        String sourceMAC = "A5-8C-A1-92-99-A4";
        String destinationMAC = "5B-79-9F-5B-F5-E1";
        String message = "I have been struggling with the decryption, and as it seems, my encryption might be the problem.";
        ClearTextFrame clearTextFrame = null;
        try {
            clearTextFrame = new ClearTextFrame(message,sourceMAC,destinationMAC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Encrypted frame:");
        EncryptedFrame encryptedFrame = null;
        try {
            encryptedFrame = CCMP.ECBModeEncryptPayload(clearTextFrame);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println(encryptedFrame);
        System.out.println('\n');

        System.out.println("Derypted frame:");
        ClearTextFrame decryptedFrame = null;
        try {
            assert encryptedFrame != null;
            decryptedFrame = CCMP.decryptFrame(encryptedFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(decryptedFrame);

        System.out.println("Is the message authentic?");
        try {
            CCMP.checkAuthenticity(encryptedFrame.getMIC(), decryptedFrame);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }
}
