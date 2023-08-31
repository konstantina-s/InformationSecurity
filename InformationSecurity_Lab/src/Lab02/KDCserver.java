package Lab02;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

public class KDCserver {

    private SecretKeySpec senderKey;
    private SecretKeySpec receiverKey;
    private SecretKeySpec sesKey;

    public KDCserver() throws NoSuchAlgorithmException {
        this.senderKey = generateKey();
        this.receiverKey = generateKey();
    }

    public ArrayList<KDCresponseToSender> cresponseToSenders(RQST request) throws NoSuchAlgorithmException {
        this.sesKey = generateKey();
        byte[] lifetime = BigInteger.valueOf(generateLifetime()).toByteArray(); //Only BigInt class has toByteArray function
        AES aes = new AES();

        //The fisrt node in the list is the encrypted information with the receivers' information, nonce, lifetime, sesKey
        aes.setKey(senderKey.getEncoded());
        byte[] encryptSessionKey = aes.encrypt(sesKey.getEncoded());
        byte[] encryptNonce = aes.encrypt(request.getNonce());
        byte[] encryptLifetimeTime = aes.encrypt(lifetime);
        byte[] encryptReceiverId = aes.encrypt(request.getReceiverID().getBytes());

        KDCresponseToSender yA = new KDCresponseToSender(encryptSessionKey,encryptNonce,encryptLifetimeTime,encryptReceiverId);

        //The second node is the encrypted information with the senders' information, lifetime, sesKey
        aes.setKey(receiverKey.getEncoded());
        encryptSessionKey = aes.encrypt(sesKey.getEncoded());
        byte[] encryptSenderId = aes.encrypt(request.getSenderID().getBytes());
        encryptLifetimeTime = aes.encrypt(lifetime);

        KDCresponseToSender yB = new KDCresponseToSender(encryptSessionKey,encryptLifetimeTime, encryptSenderId);

        ArrayList<KDCresponseToSender> responses = new ArrayList<>();
        responses.add(yA);
        responses.add(yB);
        return responses;
    }

    //Because we assume the KDC has both keys, all the keys needed for the session will be generated in this class,
    // they will be passed on to the users
    public static SecretKeySpec generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKeySpec key = (SecretKeySpec) keyGenerator.generateKey();
        return key;

    }

    //google says average lifetime of a connection lasts about 45s
    //Generating a random number between -1 to 55 interval, -1 meaning there is no limit to the connection
    public static Integer generateLifetime(){
        Random lifetime = new Random();

        return lifetime.ints(-1, 55)
                .findFirst().getAsInt();
        }

    public SecretKeySpec getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(SecretKeySpec senderKey) {
        this.senderKey = senderKey;
    }

    public SecretKeySpec getReceiverKey() {
        return receiverKey;
    }

    public void setReceiverKey(SecretKeySpec receiverKey) {
        this.receiverKey = receiverKey;
    }

    public SecretKeySpec getSesKey() {
        return sesKey;
    }

    public void setSesKey(SecretKeySpec sesKey) {
        this.sesKey = sesKey;
    }
}
