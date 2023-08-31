package Lab01;

import java.nio.charset.StandardCharsets;

public class FrameHeader {
    String sourceMAC;
    String destinationMAC;

    public FrameHeader(String sourceMAC, String destinationMAC) throws Exception {
        this.sourceMAC = sourceMAC;
        this.destinationMAC = destinationMAC;
    }

    public byte [] getSourceMAC() {
        return sourceMAC.getBytes(StandardCharsets.UTF_8);
    }

    public byte [] getDestinationMAC() {
        return destinationMAC.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return " SourceMACAddress=" + sourceMAC + "\n" +
                " DestinationMACAddress=" + destinationMAC;
    }
}