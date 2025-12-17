package org.example.gui_client;

import java.nio.ByteBuffer;

public class Payload {
    public enum DataType {
        BYTE, SHORT, INT, LONG, BYTE_ARRAY, SHORT_ARRAY, INT_ARRAY, LONG_ARRAY, STRING
    }
    public static final int PUBLIC_KEY_LENGTH = 32;
    public static final int MESSAGE_IDS_LENGTH = 16;
    public static final int TEXT_LENGTH = 1024;
    public static final int CHAT_SETTINGS_LENGTH = 2048;
    public static final int IMAGE_RESOLUTION = 7680 * 4320;
    public static final int FILE_FRAME_LENGTH = 7680 * 4320;
    final ByteBuffer buffer;
    final Object[] objects;
    Payload(Object[] objects, int size) {
        this.objects = objects;
        buffer = ByteBuffer.allocate(size);
        for (Object obj : objects) {
            if (obj instanceof Byte) {
                buffer.put((byte) obj);
            } else if (obj instanceof Short) {
                buffer.putShort((short) obj);
            } else if (obj instanceof Integer) {
                buffer.putInt((int) obj);
            } else if (obj instanceof Long) {
                buffer.putLong((long) obj);
            } else if (obj instanceof byte[]) {
                buffer.putInt(((byte[]) obj).length);
                buffer.put((byte[]) obj);
            } else if (obj instanceof short[]) {
                buffer.putInt(((short[]) obj).length);
                for (short s : (short[]) obj) {
                    buffer.putShort(s);
                }
            } else if (obj instanceof int[]) {
                buffer.putInt(((int[]) obj).length);
                for (int i : (int[]) obj) {
                    buffer.putInt(i);
                }
            } else if (obj instanceof long[]) {
                buffer.putInt(((long[]) obj).length);
                for (long l : (long[]) obj) {
                    buffer.putLong(l);
                }
            } else if (obj instanceof String) {
                buffer.putInt(((String) obj).length());
                buffer.put(obj.toString().getBytes());
            }
        }
    }
    Payload(byte[] bytes, DataType[] types) {
        buffer = ByteBuffer.wrap(bytes);
        objects = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            if (types[i] == DataType.BYTE) {
                objects[i] = buffer.get();
            } else if (types[i] == DataType.SHORT) {
                objects[i] = buffer.getShort();
            } else if (types[i] == DataType.INT) {
                objects[i] = buffer.getInt();
            } else if (types[i] == DataType.LONG) {
                objects[i] = buffer.getLong();
            } else if (types[i] == DataType.BYTE_ARRAY) {
                objects[i] = new byte[buffer.getInt()];
                for (int j = 0; j < ((byte[]) objects[i]).length; j++) {
                    ((byte[]) objects[i])[j] = buffer.get();
                }
            } else if (types[i] == DataType.SHORT_ARRAY) {
                objects[i] = new short[buffer.getInt()];
                for (int j = 0; j < ((short[]) objects[i]).length; j++) {
                    ((short[]) objects[i])[j] = buffer.getShort();
                }
            } else if (types[i] == DataType.INT_ARRAY) {
                objects[i] = new int[buffer.getInt()];
                for (int j = 0; j < ((int[]) objects[i]).length; j++) {
                    ((int[]) objects[i])[j] = buffer.getInt();
                }
            } else if (types[i] == DataType.LONG_ARRAY) {
                objects[i] = new long[buffer.getInt()];
                for (int j = 0; j < ((long[]) objects[i]).length; j++) {
                    ((long[]) objects[i])[j] = buffer.getLong();
                }
            } else if (types[i] == DataType.STRING) {
                byte[] bytes1 = new byte[buffer.getInt()];
                for (int j = 0; j < bytes1.length; j++) {
                    bytes1[j] = buffer.get();
                    System.out.println(bytes1[j]);
                }
                objects[i] = new String(bytes1);
            } else {
                throw new IllegalArgumentException("Invalid type");
            }
        }
    }
    public static Payload fromBytes(byte[] bytes, Header.Type type) {
        if (type == Header.Type.KEY_EXCHANGE) {
            return new KeyExchangePayload(bytes);
        }
        if (type == Header.Type.AUTH) {
            return new AuthPayload(bytes);
        }
        if (type.ordinal() >= Header.Type.CREATE_PERSISTENT_CHAT.ordinal() && type.ordinal() <= Header.Type.CREATE_EPHEMERAL_CHAT.ordinal()) {
            return new CreateChatPayload(bytes);
        }
        if (type.ordinal() >= Header.Type.FREEZE_CHAT.ordinal() && type.ordinal() <= Header.Type.GET_USER_CHAT_SETTINGS.ordinal()) {
            return new MessageCommandPayload(bytes);
        }
        if (type == Header.Type.SET_USER_CHAT_SETTINGS) {
            return new SetUserChatSettingsPayload(bytes);
        }
        if (type.ordinal() >= Header.Type.START_DIRECT_CHAT.ordinal() && type.ordinal() <= Header.Type.END_DIRECT_CHAT.ordinal()) {
            return new DirectChatPayload(bytes);
        }
        if (type.ordinal() >= Header.Type.START_MICROPHONE_SHARE.ordinal() && type.ordinal() <= Header.Type.END_FILE_SHARE.ordinal()) {
            return new SharePayload(bytes);
        }
        if (type.ordinal() >= Header.Type.ADD_FILE_TO_SHARE.ordinal() && type.ordinal() <= Header.Type.REMOVE_FILE_FROM_SHARE.ordinal()) {
            return new FileSharePayload(bytes);
        }
        if (type.ordinal() >= Header.Type.CHANGE_FIRST_NAME.ordinal() && type.ordinal() <= Header.Type.CHANGE_PASSWORD.ordinal()) {
            return new ProfileStringPayload(bytes);
        }
        if (type == Header.Type.ADD_PROFILE_PICTURE) {
            return new AddProfileImagePayload(bytes);
        }
        if (type.ordinal() >= Header.Type.MOVE_PROFILE_PICTURE.ordinal() && type.ordinal() <= Header.Type.REMOVE_PROFILE_PICTURE.ordinal()) {
            return new MoveProfileImagePayload(bytes);
        }
        if (type == Header.Type.TEXT) {
            return new SendTextPayload(bytes);
        }
        if (type == Header.Type.FILE_FRAME) {
            return new SendFileFramePayload(bytes);
        }
        if (type == Header.Type.DELETE) {
            return new DeleteMessagePayload(bytes);
        }
        throw new IllegalArgumentException("Invalid type");
    }
    public static byte[] fixedStringBytes(String str, int length) {
        if (str.length() > length) {
            throw new IllegalArgumentException("String too long");
        }
        byte[] bytes = new byte[length];
        System.arraycopy(str.getBytes(), 0, bytes, 0, length);
        return bytes;
    }
    public byte[] toBytes() {
        return buffer.array();
    }
    public Object[] toObjects(DataType[] types) {
        return objects;
    }
    public int size() {
        return buffer.array().length;
    }
}



class KeyExchangePayload extends Payload {
    public KeyExchangePayload(byte[] publicKey, byte[] verificationKey) {
        super(new Object[] {
                publicKey, verificationKey
        }, 4 + PUBLIC_KEY_LENGTH + 4 + PUBLIC_KEY_LENGTH);
        if (publicKey.length != PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Invalid public key");
        }
        if (verificationKey.length != PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Invalid verifying key");
        }
    }
    public KeyExchangePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.BYTE_ARRAY,
                DataType.BYTE_ARRAY
        });
        if (getPublicKey().length != PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Invalid public key");
        }
        if (getVerificationKey().length != PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Invalid verifying key");
        }
    }
    public byte[] getPublicKey() {
        return (byte[]) objects[0];
    }
    public byte[] getVerificationKey() {
        return (byte[]) objects[1];
    }
}

class AuthPayload extends Payload {
    public AuthPayload(String username, String password) {
        super(new Object[] {
                username,
                password
        }, 4 + username.getBytes().length + 4 + password.getBytes().length);
        if (username.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Username too long");
        }
        if (password.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Password too long");
        }
    }
    public AuthPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.STRING,
                DataType.STRING
        });
        if (getUsername().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Username too long");
        }
        if (getPassword().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Password too long");
        }
    }
    public String getUsername() {
        return (String) objects[0];
    }
    public String getPassword() {
        return (String) objects[1];
    }
}

class CreateChatPayload extends Payload {
    public CreateChatPayload(String name, int[] messageIDs) {
        super(new Object[] {
                name, messageIDs
        }, 4 + name.getBytes().length + 4 + messageIDs.length * 4);
        if (name.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Name too long");
        }
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public CreateChatPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.STRING,
                DataType.INT_ARRAY
        });
        if (getName().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Name too long");
        }
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public String getName() {
        return (String) objects[0];
    }
    public int[] getMessageIDs() {
        return (int[]) objects[1];
    }
}

class MessageCommandPayload extends Payload {
    public MessageCommandPayload(int[] messageIDs) {
        super(new Object[] {
                messageIDs
        }, 4 + messageIDs.length * 4);
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public MessageCommandPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT_ARRAY
        });
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public int[] getMessageIDs() {
        return (int[]) objects[0];
    }
}

class SetUserChatSettingsPayload extends Payload {
    public SetUserChatSettingsPayload(int[] messageIDs, byte[] settings) {
        super(new Object[] {
                messageIDs, settings
        }, 4 + messageIDs.length * 4 + 4 + settings.length);
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (settings.length > CHAT_SETTINGS_LENGTH) {
            throw new IllegalArgumentException("Settings too long");
        }
    }
    public SetUserChatSettingsPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT_ARRAY,
                DataType.BYTE_ARRAY
        });
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (getSettings().length > CHAT_SETTINGS_LENGTH) {
            throw new IllegalArgumentException("Settings too long");
        }
    }
    public int[] getMessageIDs() {
        return (int[]) objects[0];
    }
    public byte[] getSettings() {
        return (byte[]) objects[1];
    }
}

class DirectChatPayload extends Payload {
    public DirectChatPayload(String username) {
        super(new Object[] {
                username
        }, 4 + username.getBytes().length);
        if (username.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Username too long");
        }
    }
    public DirectChatPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.STRING
        });
        if (getUsername().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Username too long");
        }
    }
    public String getUsername() {
        return (String) objects[0];
    }
}

class SharePayload extends Payload {
    public SharePayload() {
        super(new Object[] {}, 0);
    }
    public SharePayload(byte[] bytes) {
        super(bytes, new DataType[] {});
    }
}

class FileSharePayload extends Payload {
    public FileSharePayload(String filename) {
        super(new Object[] {
                filename
        }, 4 + filename.getBytes().length);
        if (filename.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Filename too long");
        }
    }
    public FileSharePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.STRING
        });
        if (getFilepath().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Filename too long");
        }
    }
    public String getFilepath() {
        return (String) objects[0];
    }
}

class ProfileStringPayload extends Payload {
    public ProfileStringPayload(String str) {
        super(new Object[]{
                str
        }, 4 + str.getBytes().length);
        if (str.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("String too long");
        }
    }
    public ProfileStringPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.STRING
        });
        if (getString().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("String too long");
        }
    }
    public String getString() {
        return (String) objects[0];
    }
}

class AddProfileImagePayload extends Payload {
    public AddProfileImagePayload(int width, int height, byte[] image) {
        super(new Object[] {
                width, height, image
        }, 4 + 4 + 4 + image.length);
        if ((image.length / 4) > IMAGE_RESOLUTION) {
            throw new IllegalArgumentException("Image resolution too large");
        }
    }
    public AddProfileImagePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT, DataType.INT, DataType.BYTE_ARRAY
        });
        if ((getImage().length / 4) > IMAGE_RESOLUTION) {
            throw new IllegalArgumentException("Image resolution too large");
        }
    }
    public int getWidth() {
        return (int) objects[0];
    }
    public int getHeight() {
        return (int) objects[1];
    }
    public byte[] getImage() {
        return (byte[]) objects[2];
    }
}

class MoveProfileImagePayload extends Payload {
    public MoveProfileImagePayload(int imageIndex, int offset) {
        super(new Object[] {
                imageIndex, offset
        }, 4 + 4);
    }
    public MoveProfileImagePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT, DataType.INT
        });
    }
    public int getImageIndex() {
        return (int) objects[0];
    }
    public int getOffset() {
        return (int) objects[1];
    }
}

class SendTextPayload extends Payload {
    public SendTextPayload(int[] messageIDs, String text) {
        super(new Object[] {
                messageIDs, text
        }, 4 + messageIDs.length * 4 + 4 + text.getBytes().length);
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (text.length() > TEXT_LENGTH) {
            throw new IllegalArgumentException("Text too long");
        }
    }
    public SendTextPayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT_ARRAY,
                DataType.STRING
        });
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (getText().length() > TEXT_LENGTH) {
            throw new IllegalArgumentException("Text too long");
        }
    }
    public int[] getMessageIDs() {
        return (int[]) objects[0];
    }
    public String getText() {
        return (String) objects[1];
    }
}

class SendFileFramePayload extends Payload {
    public SendFileFramePayload(int[] messageIDs, String filename, int index, byte[] frame) {
        super(new Object[] {
                messageIDs, filename, index, frame
        }, 4 + messageIDs.length * 4 + 4 + filename.getBytes().length + 4 + 4 + frame.length);
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (filename.length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Filename too long");
        }
        if (frame.length > FILE_FRAME_LENGTH) {
            throw new IllegalArgumentException("File frame too long");
        }
    }
    public SendFileFramePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT_ARRAY,
                DataType.STRING,
                DataType.INT,
                DataType.BYTE_ARRAY
        });
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
        if (getFilename().length() > PUBLIC_KEY_LENGTH) {
            throw new IllegalArgumentException("Filename too long");
        }
        if (getFrame().length > FILE_FRAME_LENGTH) {
            throw new IllegalArgumentException("File frame too long");
        }
    }
    public int[] getMessageIDs() {
        return (int[]) objects[0];
    }
    public String getFilename() {
        return (String) objects[1];
    }
    public int getIndex() {
        return (int) objects[2];
    }
    public byte[] getFrame() {
        return (byte[]) objects[3];
    }
}

class DeleteMessagePayload extends Payload {
    public DeleteMessagePayload(int[] messageIDs) {
        super(new Object[] {
                messageIDs
        }, 4 + messageIDs.length * 4);
        if (messageIDs.length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public DeleteMessagePayload(byte[] bytes) {
        super(bytes, new DataType[] {
                DataType.INT_ARRAY
        });
        if (getMessageIDs().length > MESSAGE_IDS_LENGTH) {
            throw new IllegalArgumentException("The chat is too deep");
        }
    }
    public int[] getMessageIDs() {
        return (int[]) objects[0];
    }
}