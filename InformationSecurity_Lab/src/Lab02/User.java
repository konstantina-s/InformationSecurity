package Lab02;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/*
Да се изработат соодветни методи за генерирање nonce, сесиски клуч, timestamp, lifetime.
Да се изработат соодветни методи за верификација на параметрите од KDC и верификација на timestamp/lifetime.
Да се изработат соодветни методи кои ќе симулираат комуникација помеѓу страните.
Откако успешно ќе заврши протоколот да се покаже енкрипција/декрипција на порака помеѓу Алис и Боб
 со било кој симетричен алгоритам користејќи го успешно разменетиот сесиски клуч.
 */
public class User {
    private String userName;
    private byte[] key;
    private String senderID;
    private String receiverID;
    private String message;
    private byte[] sesKey;
    private byte[] lifetime;
    private byte[] timestamp;
    private byte[] nonce;

    //Sender constructor
    public User(String userName, byte[] key, String senderID, String message) {
        this.userName = userName;
        this.key = key;
        this.senderID = senderID;
        this.message = message;
    }

    //receiver constructor
    public User(String userName, byte[] key, String receiverID) {
        this.userName = userName;
        this.key = key;
        this.receiverID = receiverID;
    }

    static byte[] generateNonce(){
        byte[] nonce = new byte[13];
        new SecureRandom().nextBytes(nonce);

        return nonce;
    }

    public RQST sendRQSTtoKDC(String receiverID){
        byte[] nonce = generateNonce();
        this.nonce = nonce;

        RQST rqst = new RQST(senderID, receiverID, nonce);
        return rqst;
    }

    public ArrayList<SenderReceiverEstablishing> responseToSenders(KDCresponseToSender response) throws NoSuchAlgorithmException {
        AES aes = new AES();

        byte[] timestamp = BigInteger.valueOf(generateTimestamp()).toByteArray();

        //The fisrt node in the list is the encrypted information with the senders' information, timestamp
        aes.setKey(response.getSesKey());
        this.sesKey = response.getSesKey();

        byte[] encryptSenderId = aes.encrypt(response.getSenderID());
        byte[] encryptTimeStamp = aes.encrypt(timestamp);
        SenderReceiverEstablishing yAB = new SenderReceiverEstablishing(encryptSenderId, encryptTimeStamp);

        //The second node is the encrypted information with the senders' information, lifetime, sesKey
        aes.setKey(response.getSesKey());
        byte[] encryptSessionKey = aes.encrypt(response.getSesKey());
        byte[] encryptLifetimeTime = aes.encrypt(response.getLifetime());

        SenderReceiverEstablishing yB = new SenderReceiverEstablishing(encryptSessionKey,encryptLifetimeTime, encryptSenderId);

        ArrayList<SenderReceiverEstablishing> responses = new ArrayList<>();
        responses.add(yAB);
        responses.add(yB);
        return responses;
    }

    //The verification of KDC response

    public boolean verifyIDfromKDCRESPONSE(KDCresponseToSender response){
        AES aes =new AES();

        this.sesKey = response.getSesKey();
        aes.setKey(this.sesKey);
        if(aes.decrypt(response.getReceiverID()).toString().equals(receiverID))
            return true;
        else
            return false;
    }

    public boolean verifyNONCE(KDCresponseToSender response ){
        AES aes =new AES();

        this.sesKey = response.getSesKey();
        aes.setKey(this.sesKey);
        if(aes.decrypt(response.getNonce()).equals(this.nonce))
            return true;
        else
            return false;
    }

    public boolean verifyLifeTime(KDCresponseToSender response){
        AES aes =new AES();
        this.sesKey = response.getSesKey();
        aes.setKey(this.sesKey);
        int lifetime = convertByteArrayToIntUsingShiftOperator(aes.decrypt(response.getLifetime()));

        if(lifetime>=-1)
            return true;
        else
            return false;
    }


    // The verifications of the receiver from the sender

    public boolean verifyIDSenderReceiverEstablishing(SenderReceiverEstablishing srEst){
        AES aes =new AES();
        this.sesKey = srEst.getSesKey();
        aes.setKey(this.sesKey);
        if(aes.decrypt(srEst.getSenderID()).equals(senderID.getBytes(StandardCharsets.UTF_8)))
            return true;
        else
            return false;
    }

    public boolean verifyLifeTimeSenderReceiverEstablishing(SenderReceiverEstablishing srEst){
        AES aes =new AES();
        this.sesKey = srEst.getSesKey();
        aes.setKey(this.sesKey);
        int lifetime = convertByteArrayToIntUsingShiftOperator(aes.decrypt(srEst.getLifetime()));

        if(lifetime>=-1)
            return true;
        else
            return false;
    }

    public boolean verifyTimeStampSenderReceiverEstablishing(SenderReceiverEstablishing srEst){
        AES aes =new AES();
        int timestamp = generateTimestamp();
        this.sesKey = srEst.sesKey;
        int srTimestamp = convertByteArrayToIntUsingShiftOperator(aes.decrypt(srEst.getTimeStamp()));
        if(srTimestamp <= timestamp)
            return true;
        else
            return false;
    }

    public int generateTimestamp(){
        Date currentDate = new Date();
        Long longTime = currentDate.getTime() / 1000;

        return longTime.intValue();
    }

    public SenderMessage messageSending(){
        AES aes = new AES();

        aes.setKey(this.sesKey);
        byte[] encryptMessageX = aes.encrypt(message.getBytes(StandardCharsets.UTF_8));

        SenderMessage senderMessage = new SenderMessage(encryptMessageX);

        return senderMessage;
    }

    public String receiveMessage(SenderMessage sm){
        AES aes = new AES();

        aes.setKey(this.sesKey);
        byte[] decryptMessageX = aes.decrypt(sm.getMessageX());

        String message = new String(decryptMessageX, StandardCharsets.UTF_8);
        return message;
    }


    private int convertByteArrayToIntUsingShiftOperator(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getSesKey() {
        return sesKey;
    }

    public void setSesKey(byte[] sesKey) {
        this.sesKey = sesKey;
    }

    public byte[] getLifetime() {
        return lifetime;
    }

    public void setLifetime(byte[] lifetime) {
        this.lifetime = lifetime;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(byte[] timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }
}
