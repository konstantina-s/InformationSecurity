package Lab01;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;


//Represents the two modes used in CCMP for encrypting a frame.
//One for encrypting the payload, and one for calculating the MIC.
public class CCMP {

    static byte[] generateNonce(){
        byte[] nonce = new byte[13];
        new SecureRandom().nextBytes(nonce);

        return nonce;
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[][] dividePayloadInBlocks(byte[] payload){
        byte[][] payloadBlocks;
        if(payload.length % 16 != 0) {
            payloadBlocks = new byte[(payload.length / 16) + 1][16];
        } else {
            payloadBlocks = new byte[payload.length / 16][16];
        }

        int ctr = 0;
        for(int i = 0; i < payload.length; i++) {
            for(int j=0; j<16; j++) {
                if(ctr != payload.length) {
                    payloadBlocks[i][j] = payload[ctr];
                    ctr=ctr+1;
                }else {
                    break;
                }
            }
            if(ctr == payload.length) {
                break;
            }
        }
        return payloadBlocks;
    }

    static SecretKey KEY;
    static IvParameterSpec IV;
    static byte [] NONCE = generateNonce();


    static {
        try {
            KEY = generateKey(128);
            IV = generateIv();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    static byte[] CBCModecalCulateMIC(ClearTextFrame clearTextFrame) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        int i = 0;

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,KEY);

        byte[] MIC = new byte[8];
        byte [] IVbytes = IV.toString().getBytes(StandardCharsets.UTF_8);
        byte [] sourceAddress = Arrays.copyOfRange(clearTextFrame.frameHeader.getSourceMAC(), 0, 16);
        byte [] destinationAddress = Arrays.copyOfRange(clearTextFrame.frameHeader.getDestinationMAC(),0,16);

        byte[] ivAES = cipher.update(IVbytes); // encrypt IV with AES
        byte[] tempEncryption = ivAES; //the encrypted IV is ready to be XOR-ed with the first block
        byte [][] payloadData = dividePayloadInBlocks(clearTextFrame.getPayloadData());// dividing the payload in blocks

        for(byte b : sourceAddress) // encrypting the source MAC address
        {
            tempEncryption[i] = (byte) ( b ^ tempEncryption[i]);
            i=i+1;
        }
        cipher.update(tempEncryption);// updating the encrypted source MAC address into the AAD

        i=0;
        for(byte b : destinationAddress)// encrypting the destination MAC address
        {
            tempEncryption[i] = (byte) ( b ^ tempEncryption[i]);
            i=i+1;
        }
        cipher.update(tempEncryption); // updating the encrypted destination MAC address into AAD

        for(byte[] block : payloadData){ //Calculating the MIC
            i=0;
            for(byte b : block){
                tempEncryption[i] = (byte) (b ^ tempEncryption[i]);
                i++;
            }
            tempEncryption= cipher.update(tempEncryption);
        }
        cipher.doFinal(tempEncryption);

        MIC = Arrays.copyOfRange(tempEncryption,0,8); // copying the first 64 bits for the MIC

        return MIC;
    }

    static EncryptedFrame ECBModeEncryptPayload(ClearTextFrame clearTextFrame) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        int i = 0;

        EncryptedFrame encryptedFrame = new EncryptedFrame();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,KEY);

        byte[] MIC = CBCModecalCulateMIC(clearTextFrame);
        byte [] encryptedPayloadData = new byte[clearTextFrame.getPayloadData().length];
        byte[] counter = {0, 0, 0};
        byte[] ctrPL = new byte[16]; // The CTR Preload which will be consisted of the nonce and a counter
        byte [][] payloadData = dividePayloadInBlocks(clearTextFrame.getPayloadData());// dividing the payload in blocks

        encryptedFrame.setFrameHeader(clearTextFrame.frameHeader);
        encryptedFrame.setMIC(MIC);
        System.arraycopy(NONCE, 0 , ctrPL , 0 , 13);
        System.arraycopy(counter, 0, ctrPL, 13 , 3);

        //Encrypting the payload with counter mode


        int currentLength = 0;
        for(i=0; i<clearTextFrame.getPayloadData().length ;i++)
        {
            byte[] tempEncryptedFrame = cipher.update(ctrPL);// encrypting the counter
            for(int j=0;j<16;j++)
            {
                if(currentLength==clearTextFrame.getPayloadData().length){
                    break;
                }

                encryptedPayloadData[i] = (byte) (tempEncryptedFrame[j] ^ clearTextFrame.getPayloadData()[currentLength]);
                currentLength=currentLength+1;
                if(j!=15){
                    i++;
                }
            }
            ctrPL[13]++; //incrementing the counter
            ctrPL[14]++;
            ctrPL[15]++;
        }

        //for(byte[] block : payloadData){
//            i=0;
//            for(byte b: block) {
//                encryptedPayload[i] = (byte) (b ^ tempEncryptedFrame[i]); //XOR with the payload and encrypted ctrPL
//                i++;
//            }
//
//            tempEncryptedFrame= cipher.update(ctrPL); // encrypting the counter
//            ctrPL[13]++; //incrementing the counter
//            ctrPL[14]++;
//            ctrPL[15]++;
//        }

        //we encrypt the MIC, and set it in the encrypted frame
        byte[] encryptedMIC = new byte[8];
        byte[] AESforMIC = Arrays.copyOfRange(cipher.doFinal(ctrPL),0,8);
        i=0;

        for(byte b: AESforMIC){
            encryptedMIC[i] = (byte) (b ^ MIC[i]);
            i++;
        }

        encryptedFrame.setEncryptedMIC(encryptedMIC);
        encryptedFrame.setFrameHeader(clearTextFrame.getFrameHeader());
        encryptedFrame.setPayloadData(encryptedPayloadData);

        return encryptedFrame;
    }

    public static ClearTextFrame decryptFrame(EncryptedFrame encryptedFrame) throws Exception {
        int i = 0;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, KEY);

        byte[] decryptedPayload = new byte[encryptedFrame.getPayloadData().length];
        byte[] counter = {0, 0, 0};
        byte[] ctrPL = new byte[16]; // The CTR Preload which will be consisted of the nonce and a counter
        byte[][] encryptedPayloadData = dividePayloadInBlocks(encryptedFrame.getPayloadData());// dividing the payload in blocks

        System.arraycopy(NONCE, 0 , ctrPL , 0 , 13);
        System.arraycopy(counter, 0, ctrPL, 13 , 3);

        int currentLength = 0;
        for(i=0; i<encryptedFrame.getPayloadData().length ;i++)
        {
            byte[] tempEncryptedFrame = cipher.update(ctrPL);// decrypting the counter
            for(int j=0;j<16;j++)
            {
                if(currentLength==encryptedFrame.getPayloadData().length){
                    break;
                }

                decryptedPayload[i] = (byte) (tempEncryptedFrame[j] ^ encryptedFrame.getPayloadData()[currentLength]);
                currentLength=currentLength+1;
                if(j!=15){
                    i++;
                }
            }
            ctrPL[13]++; //incrementing the counter
            ctrPL[14]++;
            ctrPL[15]++;
        }
//        for(byte[] block : encryptedPayloadData){
//            i=0;
//            for(byte b: block) {
//                decryptedPayload[i] = (byte) (b ^ tempEncryptedFrame[i]); //XOR with the payload and encrypted ctrPL
//                i++;
//            }
//
//            tempEncryptedFrame= cipher.update(ctrPL); // decrypting the counter
//            ctrPL[13]++; //incrementing the counter
//            ctrPL[14]++;
//            ctrPL[15]++;
//        }

        ClearTextFrame clearTextFrame =
                new ClearTextFrame(new String(decryptedPayload)
                        ,new String(encryptedFrame.getFrameHeader().getSourceMAC())
                        ,new String(encryptedFrame.getFrameHeader().getDestinationMAC())
                );

        clearTextFrame.setPayloadData(clearTextFrame.getPayloadData());

        return clearTextFrame;
    }

    static void checkAuthenticity(byte [] MIC, ClearTextFrame decryptedFrame) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] decryptedMIC = CBCModecalCulateMIC(decryptedFrame);

        if(MIC.equals(decryptedMIC))
            System.out.println("The message IS authentic");
        else
            System.out.println("The message is NOT authentic");
    }
}
