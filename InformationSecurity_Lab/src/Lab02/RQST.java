package Lab02;

public class RQST {
    String senderID;
    String ReceiverID;
    byte[] nonce;

    public RQST(String senderID, String receiverID, byte[] nonce) {
        this.senderID = senderID;
        ReceiverID = receiverID;
        this.nonce = nonce;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }
}
