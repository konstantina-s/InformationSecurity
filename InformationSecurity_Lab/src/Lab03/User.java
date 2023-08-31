package Lab03;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Random;

public class User{
    private final String name;
    private Integer exponent;
    private final BigInteger g;

    private PrivateKey privateKey;
    private PublicKey personalPublicKey;
    private PublicKey foreignPublicKey;
    private BigInteger personal_g_exponent;
    private BigInteger foreign_g_exponent;
    private BigInteger sharedKey;

    //Constructor with name and known g, and immediately generating the exponent
    public User(String name, BigInteger generator){
        this.name = name;
        this.g = generator;
        setExponent();
    }

    //getters and setters for keys and calculated exponents
    public void setForeign_g_exponent(BigInteger exp){
        this.foreign_g_exponent = exp;
    }

    public void setExponent(){
        this.exponent = randomNumberGenerator();
    }

    public void setForeignPublicKey(PublicKey pk){
        this.foreignPublicKey = pk;
    }

    public PublicKey getPersonalPublicKey(){
        return this.personalPublicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }

    public Integer getExponent(){
        return exponent;
    }

    public BigInteger getPersonal_g_exponent(){
        return personal_g_exponent;
    }

    public BigInteger getSharedKey(){
        return sharedKey;
    }

    //For generating the keys of a user
    public void PrivateAndPublicKeyGenerator() throws NoSuchAlgorithmException{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.privateKey = keyPair.getPrivate();
        this.personalPublicKey = keyPair.getPublic();
    }

    //Calculation methods

    //Calculating g^exponent
    public BigInteger calculateGExponent(){
        return this.personal_g_exponent = this.g.pow(exponent);
    }

    //Calculating the shared key, (g^foreign_exponent)^personal_exponent
    public void calculateSharedKey(){
        sharedKey = foreign_g_exponent.pow(exponent);
    }

    public Message CipherSignAndWrapAlice() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
        //the signature of the message
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(this.privateKey);
        signature.update(byteArrayWrap(personal_g_exponent.toByteArray(), foreign_g_exponent.toByteArray()));
        byte[] signBytes = signature.sign();

        //the encryption of the signed message
        AES aes = new AES();
        aes.setKey(sharedKey.toByteArray());
        byte[] cipheredSignature = aes.encrypt(signBytes);

        return new Message(cipheredSignature);
    }

    public Message CipherSignAndWrapBob() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
        //the signature of the message
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(this.privateKey);
        signature.update(byteArrayWrap(personal_g_exponent.toByteArray(), foreign_g_exponent.toByteArray()));
        byte[] signBytes = signature.sign();

        //the encryption of the signed message
        AES aes = new AES();
        aes.setKey(sharedKey.toByteArray());
        byte[] cipheredSignature = aes.encrypt(signBytes);

        return new Message(personal_g_exponent, cipheredSignature);
    }

    public void DecryptionAndVerification(Message aliceAnswer) throws Exception{
        //decryption of the received message
        AES aes = new AES();
        aes.setKey(sharedKey.toByteArray());
        byte[] decryptedSignature = aes.decrypt(aliceAnswer.getCipherSignature());

        byte[] wrap = byteArrayWrap(foreign_g_exponent.toByteArray(), personal_g_exponent.toByteArray());

        //verification of the signature
        Signature verifySignature = Signature.getInstance("SHA256withRSA");
        verifySignature.initVerify(this.foreignPublicKey);
        verifySignature.update(wrap);
        boolean isVerified = verifySignature.verify(decryptedSignature);
        if(!isVerified){
            throw new Exception("Verification failed!");
        }
    }

    //Additional methods

    //Generating the exponent (the power of g later on)
    public Integer randomNumberGenerator(){
        Random random = new Random();
        return random.nextInt(20);
    }

    private byte[] byteArrayWrap(byte[] arr1, byte[] arr2){
        byte[] result = new byte[arr1.length + arr2.length];
        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.put(arr1);
        buffer.put(arr2);
        result = buffer.array();
        return result;
    }
}