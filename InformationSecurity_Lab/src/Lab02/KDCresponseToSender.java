package Lab02;

import javax.crypto.SecretKey;

public class KDCresponseToSender {
    byte[] sesKey;
    byte[] nonce;
    byte[] lifetime;
    byte[] senderID;
    byte[] receiverID;

    //yA constructor
    public KDCresponseToSender(byte[] sesKey, byte[] nonce, byte[] lifetime, byte[] receiverID) {
        this.sesKey = sesKey;
        this.nonce = nonce;
        this.lifetime = lifetime;
        this.receiverID = receiverID;
    }

    //yB constructor
    public KDCresponseToSender(byte[] sesKey, byte[] senderID, byte[] lifetime) {
        this.sesKey = sesKey;
        this.lifetime = lifetime;
        this.senderID = senderID;
    }

    public byte[] getSesKey() {
        return sesKey;
    }

    public void setSesKey(byte[] sesKey) {
        this.sesKey = sesKey;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    public byte[] getLifetime() {
        return lifetime;
    }

    public void setLifetime(byte[] lifetime) {
        this.lifetime = lifetime;
    }

    public byte[] getSenderID() {
        return senderID;
    }

    public void setSenderID(byte[] senderID) {
        this.senderID = senderID;
    }

    public byte[] getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(byte[] receiverID) {
        this.receiverID = receiverID;
    }
}
