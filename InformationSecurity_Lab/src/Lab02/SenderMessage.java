package Lab02;

public class SenderMessage {

    byte [] messageX;

    public SenderMessage(byte[] messageX) {
        this.messageX = messageX;
    }

    public byte[] getMessageX() {
        return messageX;
    }

    public void setMessageX(byte[] messageX) {
        this.messageX = messageX;
    }
}
