package org.example.gui_client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Header(int magic, Type type, int size, int time, byte[] hash, byte[] signature) {
    public static enum Type {
        KEY_EXCHANGE,

        AUTH,

        // Commands
        // chat name, array of messageIDs
        CREATE_PERSISTENT_CHAT, CREATE_EPHEMERAL_CHAT,

        // message command(array of messageIDs)
        FREEZE_CHAT, UNFREEZE_CHAT,
        LOAD_SOME_MESSAGES, DOWNLOAD_FILE,
        GET_USER_CHAT_SETTINGS,

        // message command(array of messageIDs, chat settings)
        SET_USER_CHAT_SETTINGS,

        // direct chat
        START_DIRECT_CHAT, END_DIRECT_CHAT,

        // share()
        START_MICROPHONE_SHARE, END_MICROPHONE_SHARE,
        START_CAMERA_SHARE, END_CAMERA_SHARE,
        START_SCREEN_SHARE, END_SCREEN_SHARE,
        START_FILE_SHARE, END_FILE_SHARE,

        // share(filepath)
        ADD_FILE_TO_SHARE, REMOVE_FILE_FROM_SHARE,

        // profile string
        CHANGE_FIRST_NAME, CHANGE_USERNAME, CHANGE_PASSWORD,

        // profile image
        ADD_PROFILE_PICTURE,

        MOVE_PROFILE_PICTURE, REMOVE_PROFILE_PICTURE,

        TEXT, FILE_FRAME, DELETE
    }
    public static final int MAGIC = 1;
    public static final int SIZE = 4 + 4 + 4 + 4 + 32 + 32;
    public static Header fromBytes(byte[] bytes) {
        if (bytes.length != SIZE) {
            throw new IllegalArgumentException("Invalid header size");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int magic = buffer.getInt();
        if (magic != 1) {
            throw new IllegalArgumentException("Invalid magic number");
        }
        Type type = Type.values()[buffer.getInt()];
        int size = buffer.getInt();
        int time = buffer.getInt();
        byte[] hash = new byte[32];
        buffer.get(hash);
        byte[] signature = new byte[32];
        buffer.get(signature);
        return new Header(magic, type, size, time, hash, signature);
    }
    public byte[] toBytes() {
        return ByteBuffer.allocate(SIZE)//.order(ByteOrder.LITTLE_ENDIAN)
                .putInt(magic)
                .putInt(type.ordinal())
                .putInt(size)
                .putInt(time)
                .put(hash)
                .put(signature)
                .array();
    }
}
