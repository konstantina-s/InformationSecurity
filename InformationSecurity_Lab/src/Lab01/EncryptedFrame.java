package Lab01;

import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;
import java.util.Base64;

public class EncryptedFrame {
    private FrameHeader frameHeader;
    private byte [] encryptedMIC;
    private byte [] encryptedPayload;
    private byte [] MIC;

    public byte[] getMIC() {
        return MIC;
    }

    public void setMIC(byte[] MIC) {
        this.MIC = MIC;
    }

    public FrameHeader getFrameHeader() {
        return frameHeader;
    }

    public void setFrameHeader(FrameHeader frameHeader) {
        this.frameHeader = frameHeader;
    }

    public byte[] getPayloadData() {
        return encryptedPayload;
    }

    public void setPayloadData(byte[] payloadData) {
        this.encryptedPayload = payloadData;
    }

    public byte[] getEncryptedMIC() {
        return encryptedMIC;
    }

    public void setEncryptedMIC(byte[] encryptedMIC) {
        this.encryptedMIC = encryptedMIC;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(frameHeader.toString() + "\n PayloadData: " + Base64.getEncoder().encodeToString(encryptedPayload) + "\n MIC: " + Base64.getEncoder().encodeToString(encryptedMIC));
        return sb.toString();
    }
}