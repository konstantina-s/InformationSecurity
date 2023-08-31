package Lab01;

import java.nio.charset.StandardCharsets;

public class ClearTextFrame {

    FrameHeader frameHeader;
    byte [] payloadData;

    public ClearTextFrame(String payloadData, String SourceAddress, String DestinationAddress) throws Exception {
        frameHeader = new FrameHeader(SourceAddress,DestinationAddress);
        this.payloadData = payloadData.getBytes(StandardCharsets.UTF_8);
    }

    public FrameHeader getFrameHeader() {
        return frameHeader;
    }

    public void setFrameHeader(FrameHeader frameHeader) {
        this.frameHeader = frameHeader;
    }

    public byte[] getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(byte[] payloadData) {
        this.payloadData = payloadData;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(frameHeader.toString());
        sb.append("\n Payload Data: "  + new String(payloadData,StandardCharsets.UTF_8));
        return sb.toString();
    }

}
