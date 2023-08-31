package Lab02;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class KerberosDemo {
    public static void main(String[] args) {

        try {
            KDCserver kdc = new KDCserver();

            User Alice = new User("Alice", kdc.getSenderKey().getEncoded(), "AliceID", "Hello Bob, I'm Alice.");
            User Bob = new User("Bob", kdc.getReceiverKey().getEncoded(), "BobID");

            RQST aliceRQST = Alice.sendRQSTtoKDC("BobID");

            ArrayList<KDCresponseToSender> serverResponse = kdc.cresponseToSenders(aliceRQST);

            System.out.println("Verifying credentials and establishing connection between users: ");

            if(Alice.verifyIDfromKDCRESPONSE(serverResponse.get(0)) &&
                    Alice.verifyLifeTime(serverResponse.get(0)) &&
                    Alice.verifyNONCE(serverResponse.get(0)))      {


                ArrayList<SenderReceiverEstablishing> senderReceiverEstablishings = Alice.responseToSenders(serverResponse.get(1));

                if(Bob.verifyIDSenderReceiverEstablishing(senderReceiverEstablishings.get(1)) &&
                        Alice.verifyLifeTimeSenderReceiverEstablishing(senderReceiverEstablishings.get(1)) &&
                        Alice.verifyTimeStampSenderReceiverEstablishing(senderReceiverEstablishings.get(0))){
                    SenderMessage aliceMessage = Alice.messageSending();
                    System.out.println("Message safely sent!");
                    System.out.println(Bob.receiveMessage(aliceMessage));
                }else
                    System.out.println("Session failed, try again!");

            }else
                System.out.println("Session failed, try again!");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }
}
