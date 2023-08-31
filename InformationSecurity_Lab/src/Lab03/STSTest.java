package Lab03;

import java.math.BigInteger;
import java.security.*;


//Simple STS protocol (without certificates)
public class STSTest {

    //calculating a random big prime number for our generator g
    public static BigInteger generateBigPrime(int bits) {
        SecureRandom random = new SecureRandom();
        return BigInteger.probablePrime(bits, random);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("--STS Protocol Test--");
        //the public key
        BigInteger g = generateBigPrime(512);

        //The users in the communication, Alice and Bob
        User Alice = new User("Alice", g);
        User Bob = new User("Bob", g);

        //generating the private/public key pairs for each user
        Alice.PrivateAndPublicKeyGenerator();
        System.out.println("ALICE PRIVATE AND PUBLIC KEYS: \n" + Alice.getPrivateKey()
                                                                + " \nAND\n "
                                                                + Alice.getPersonalPublicKey());
        Bob.PrivateAndPublicKeyGenerator();
        System.out.println("BOB PRIVATE AND PUBLIC KEYS: \n" + Bob.getPrivateKey()
                                                                + " \nAND\n "
                                                                + Bob.getPersonalPublicKey() + "\n");

        //calculating g^exponent for each user
        BigInteger alice_g_exponent = Alice.calculateGExponent();
        System.out.println("ALICE EXPONENT AND G^EXPONENT: \n" + Alice.getExponent()
                                                                            + " " + Alice.getPersonal_g_exponent());

        BigInteger bob_g_exponent = Bob.calculateGExponent();
        System.out.println("BOB EXPONENT AND G^EXPONENT: \n" + Bob.getExponent()
                                                                        + " " + Bob.getPersonal_g_exponent() + "\n");
        //setting the received foreign information
        Bob.setForeign_g_exponent(alice_g_exponent);
        Alice.setForeign_g_exponent(bob_g_exponent);
        Alice.setForeignPublicKey(Bob.getPersonalPublicKey());
        Bob.setForeignPublicKey(Alice.getPersonalPublicKey());

        //calculating the shared key with DH protocol
        Bob.calculateSharedKey();
        System.out.println("BOB CALCULATED SHARED KEY:\n" + Bob.getSharedKey() + "\n");
        Message answerFromBob = Bob.CipherSignAndWrapBob();
        //
        Alice.calculateSharedKey();
        System.out.println("ALICE CALCULATED SHARED KEY:\n" + Alice.getSharedKey() + "\n");
        Alice.DecryptionAndVerification(answerFromBob);

        Message answerFromAlice = Alice.CipherSignAndWrapAlice();
        Bob.DecryptionAndVerification(answerFromAlice);

        System.out.println("--SIMULATION FINISHED--");
    }
}
