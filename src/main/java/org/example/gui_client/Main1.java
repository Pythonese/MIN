package org.example.gui_client;

public class Main1 {
    public static void main(String[] args) throws Exception {
        MessageAPI messageAPI = new MessageAPI(new byte[] {127, 0, 0, 1}, 8080);
        messageAPI.connect();
        messageAPI.sendKeyExchangeMessage();
        System.out.println("Sent key exchange message");
        messageAPI.receiveKeyExchangeMessage();
        System.out.println("Received key exchange message");
        messageAPI.sendAuthMessage("user1", "password1");
        Header header = messageAPI.receiveHeader();
        if (header.type() == Header.Type.TEXT) {
            SendTextPayload payload = (SendTextPayload) messageAPI.receivePayload(header);
            System.out.println(payload.getText());
            messageAPI.sendSendTextMessage(new int[] {1, 2}, "Hi");
            header = messageAPI.receiveHeader();
            payload = (SendTextPayload) messageAPI.receivePayload(header);
            System.out.println(payload.getText());
            messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[] {1, 3});
            header = messageAPI.receiveHeader();
            payload = (SendTextPayload) messageAPI.receivePayload(header);
            System.out.println(payload.getText());
            System.out.println(payload.getMessageIDs()[1]);
        }
    }
}