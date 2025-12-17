package org.example.gui_client;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.time.Instant;

public class MessageAPI {
    final byte[] addr;
    final int port;
    Socket socket;
    InputStream in;
    OutputStream out;
    public MessageAPI(byte[] addr, int port) throws Exception {
        this.addr = addr;
        this.port = port;
        this.socket = new Socket();
        this.socket.setSoTimeout(100000);
        in = null;
        out = null;
    }

    public synchronized void connect() throws IOException {
        this.socket = new Socket();
        this.socket.setSoTimeout(100000);
        socket.connect(new InetSocketAddress(InetAddress.getByAddress(addr), port));
        if (socket.isConnected()) {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } else {
            throw new RuntimeException("Connection failed");
        }
    }

    public synchronized void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
        if (out != null) {
            out.close();
            out = null;
        }
        if (socket != null) {
            socket.close();
        }
    }

    public Header makeHeader(Header.Type type, Payload payload) {
        try {
            byte[] hash = SHA256.hash(payload.toBytes());
            return new Header(Header.MAGIC, type, payload.size(), Instant.now().getNano(), hash, hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHeader(Header header) {
        try {
            out.write(header.toBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPayload(Payload payload) {
        try {
            byte[] payloadBytes = payload.toBytes();
            out.write(payloadBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // BEGIN SO MANY METHODS

    public void sendKeyExchangeMessage() {
        KeyExchangePayload payload = new KeyExchangePayload(new byte[32], new byte[32]);
        sendHeader(makeHeader(Header.Type.KEY_EXCHANGE, payload));
        sendPayload(payload);
    }

    public void sendAuthMessage(String username, String password) {
        AuthPayload payload = new AuthPayload(username, password);
        sendHeader(makeHeader(Header.Type.AUTH, payload));
        sendPayload(payload);
    }

    public void sendCreateChatMessage(Header.Type type, String name, int[] messageIDs) {
        CreateChatPayload payload = new CreateChatPayload(name, messageIDs);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendMessageCommandMessage(Header.Type type, int[] messageIDs) {
        MessageCommandPayload payload = new MessageCommandPayload(messageIDs);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendSetUserChatSettingsMessage(int[] messageIDs, byte[] settings) {
        SetUserChatSettingsPayload payload = new SetUserChatSettingsPayload(messageIDs, settings);
        sendHeader(makeHeader(Header.Type.SET_USER_CHAT_SETTINGS, payload));
        sendPayload(payload);
    }

    public void sendDirectChatMessage(Header.Type type, String username) {
        DirectChatPayload payload = new DirectChatPayload(username);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendShareMessage(Header.Type type) {
        SharePayload payload = new SharePayload();
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendFileShareMessage(Header.Type type, String filename) {
        FileSharePayload payload = new FileSharePayload(filename);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendProfileStringMessage(Header.Type type, String str) {
        ProfileStringPayload payload = new ProfileStringPayload(str);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendAddProfileImageMessage(int width, int height, byte[] image) {
        AddProfileImagePayload payload = new AddProfileImagePayload(width, height, image);
        sendHeader(makeHeader(Header.Type.ADD_PROFILE_PICTURE, payload));
        sendPayload(payload);
    }

    public void sendMoveProfileImageMessage(Header.Type type, int imageIndex, int offset) {
        MoveProfileImagePayload payload = new MoveProfileImagePayload(imageIndex, offset);
        sendHeader(makeHeader(type, payload));
        sendPayload(payload);
    }

    public void sendSendTextMessage(int[] messageIDs, String text) {
        SendTextPayload payload = new SendTextPayload(messageIDs, text);
        sendHeader(makeHeader(Header.Type.TEXT, payload));
        sendPayload(payload);
    }

    public void sendSendFileFrameMessage(int[] messageIDs, String filename, int index, byte[] frame) {
        SendFileFramePayload payload = new SendFileFramePayload(messageIDs, filename, index, frame);
        sendHeader(makeHeader(Header.Type.FILE_FRAME, payload));
        sendPayload(payload);
    }

    public void sendDeleteMessageMessage(int[] messageIDs) {
        DeleteMessagePayload payload = new DeleteMessagePayload(messageIDs);
        sendHeader(makeHeader(Header.Type.DELETE, payload));
        sendPayload(payload);
    }

    // END SO MANY METHODS

    public byte[] receiveAll(int size) {
        try {
            byte[] bytes = new byte[size];
            while (size > 0) {
                if (in.available() > 0) {
                    int read = in.read(bytes, bytes.length - size, size);
                    if (read == -1) {
                        throw new RuntimeException("Connection closed");
                    }
                    size -= read;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Header receiveHeader() {
        try {
            Header header = Header.fromBytes(receiveAll(Header.SIZE));
            if (header.magic() != Header.MAGIC) {
                throw new RuntimeException("Invalid magic");
            }
            return new Header(header.magic(), header.type(), header.size(), header.time(), header.hash(), header.signature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Payload receivePayload(Header header) {
        try {
            Payload payload = Payload.fromBytes(receiveAll(header.size()), header.type());
            if (Arrays.equals(SHA256.hash(payload.toBytes()), header.hash())) {
                return payload;
            }
            throw new RuntimeException("Invalid hash");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Message receiveKeyExchangeMessage() {
        try {
            Header header = receiveHeader();
            KeyExchangePayload payload = (KeyExchangePayload) receivePayload(header);
            return new Message(header, payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}