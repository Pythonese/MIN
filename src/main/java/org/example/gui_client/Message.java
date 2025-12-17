package org.example.gui_client;

public record Message(Header header, Payload payload) {
    public byte[] toBytes() {
        byte[] headerBytes = header.toBytes();
        byte[] payloadBytes = payload.toBytes();
        byte[] bytes = new byte[headerBytes.length + payloadBytes.length];
        System.arraycopy(headerBytes, 0, bytes, 0, headerBytes.length);
        System.arraycopy(payloadBytes, 0, bytes, headerBytes.length, payloadBytes.length);
        return bytes;
    }
}
