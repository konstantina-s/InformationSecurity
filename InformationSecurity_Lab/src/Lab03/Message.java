package Lab03;

import java.math.BigInteger;

public class Message{

    private byte[] cipherSignature;
    private BigInteger g_exponent;

    public Message(byte[] cipherSignature) {
        this.cipherSignature = cipherSignature;
    }

    public Message( BigInteger g_exponent, byte[] cipherSignature){
        this.cipherSignature = cipherSignature;
        this.g_exponent = g_exponent;
    }

    public byte[] getCipherSignature(){
        return cipherSignature;
    }
}