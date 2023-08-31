package Lab02;

public class SenderReceiverEstablishing {
    byte[] sesKey;
    byte[] lifetime;
    byte[] senderID;
    byte[] timeStamp;

    //yAB constructor
    public SenderReceiverEstablishing(byte[] senderID, byte[] timeStamp) {
        this.senderID = senderID;
        this.timeStamp = timeStamp;
    }

    //yB constructor
    public SenderReceiverEstablishing(byte[] sesKey, byte[] lifetime, byte[] senderID) {
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

    public byte[] getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(byte[] timeStamp) {
        this.timeStamp = timeStamp;
    }
}
