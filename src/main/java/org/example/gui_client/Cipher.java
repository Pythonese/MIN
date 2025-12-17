package org.example.gui_client;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import javax.crypto.KeyAgreement;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

//public interface Cipher {
//    void encrypt(byte[] data);
//    int encrypt(int data);
//    void decrypt(byte[] data);
//    int decrypt(int data);
//}
//class XorCipher implements Cipher {
//    byte[] key;
//    XorCipher(byte[] key) {
//        this.key = key;
//    }
//    @Override
//    public void encrypt(byte[] data) {
//        for (int i = 0; i < data.length; i++) {
//            data[i] ^= key[i % key.length];
//        }
//    }
//    @Override
//    public int encrypt(int data) {
//        byte[] bytes = ByteBuffer.wrap(key).putInt(data).array();
//        encrypt(bytes);
//        return ByteBuffer.wrap(bytes).getInt();
//    }
//    @Override
//    public void decrypt(byte[] data) {
//        encrypt(data);
//    }
//    @Override
//    public int decrypt(int data) {
//        return encrypt(data);
//    }
//}
//
//class ECCKeyAgreement {
//    byte[] privateKey;
//    byte[] publicKey;
//    public ECCKeyAgreement() throws Exception {
//        KeyPairGenerator kpg;
//        kpg = KeyPairGenerator.getInstance("EC","SunEC");
//        ECGenParameterSpec ecsp;
//
//        ecsp = new ECGenParameterSpec("secp256r1");
//        kpg.initialize(ecsp);
//
//        KeyPair kpU = kpg.genKeyPair();
//        PrivateKey privKeyU = kpU.getPrivate();
//        this.privateKey = privKeyU.getEncoded();
//        PublicKey pubKeyU = kpU.getPublic();
//        if (pubKeyU instanceof ECPublicKey) {
//            this.publicKey = pubKeyU.getEncoded();
//        }
//    }
//    byte[] getPublicKey() {
//        return publicKey;
//    }
//    byte[] generateSecret(byte[] key) throws Exception {
//        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
//        ecdh.init(KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(key)));
//        ecdh.doPhase(KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(privateKey)),true);
//        return ecdh.generateSecret();
//    }
//}
//
//class ECDSASignature {
//    private static final String ALGORITHM = "EC";
//    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
//    private static final String CURVE_NAME = "secp256r1"; // Also known as P-256
//
//    private PrivateKey privateKey;
//    private PublicKey publicKey;
//
//    // Constructor for when you have existing keys
//    public ECDSASignature(PrivateKey privateKey, PublicKey publicKey) {
//        this.privateKey = privateKey;
//        this.publicKey = publicKey;
//    }
//
//    // Constructor for generating new key pair
//    public ECDSASignature() throws Exception {
//        generateKeyPair();
//    }
//
//
//    /**
//     * Generate a new EC key pair
//     */
//    public void generateKeyPair() throws Exception {
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
//        ECGenParameterSpec ecSpec = new ECGenParameterSpec(CURVE_NAME);
//        keyGen.initialize(ecSpec, new SecureRandom());
//
//        KeyPair keyPair = keyGen.generateKeyPair();
//        this.privateKey = keyPair.getPrivate();
//        this.publicKey = keyPair.getPublic();
//    }
//
//    /**
//     * Sign data with the private key
//     */
//    public byte[] sign(byte[] data) throws Exception {
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign(privateKey);
//        signature.update(data);
//        return signature.sign();
//    }
//
//    /**
//     * Sign data and return base64 encoded signature
//     */
//    public String signToBase64(byte[] data) throws Exception {
//        byte[] signature = sign(data);
//        return Base64.getEncoder().encodeToString(signature);
//    }
//
//    /**
//     * Sign a string message
//     */
//    public String sign(String message) throws Exception {
//        byte[] data = message.getBytes();
//        byte[] signature = sign(data);
//        return Base64.getEncoder().encodeToString(signature);
//    }
//
//    /**
//     * Verify a signature
//     */
//    public boolean verify(byte[] data, byte[] signature) throws Exception {
//        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
//        sig.initVerify(publicKey);
//        sig.update(data);
//        return sig.verify(signature);
//    }
//
//    /**
//     * Verify a base64 encoded signature
//     */
//    public boolean verify(byte[] data, String base64Signature) throws Exception {
//        byte[] signature = Base64.getDecoder().decode(base64Signature);
//        return verify(data, signature);
//    }
//
//    /**
//     * Verify a string message with base64 signature
//     */
//    public boolean verify(String message, String base64Signature) throws Exception {
//        byte[] data = message.getBytes();
//        return verify(data, base64Signature);
//    }
//
//    // Getters for keys
//    public PublicKey getPublicKey() {
//        return publicKey;
//    }
//
//    public PrivateKey getPrivateKey() {
//        return privateKey;
//    }
//
//    /**
//     * Get public key as base64 string
//     */
//    public String getPublicKeyBase64() {
//        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
//    }
//
//    /**
//     * Get private key as base64 string
//     */
//    public String getPrivateKeyBase64() {
//        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
//    }
//
//    /**
//     * Load keys from base64 strings
//     */
//    public static ECDSASignature loadFromBase64(String base64PrivateKey, String base64PublicKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
//
//        // Decode and load private key
//        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
//        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
//        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
//
//        // Decode and load public key
//        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
//        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//
//        return new ECDSASignature(privateKey, publicKey);
//    }
//
//    /**
//     * Create instance with only public key (for verification only)
//     */
//    public static ECDSASignature createVerifier(String base64PublicKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
//        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
//        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//
//        return new ECDSASignature(null, publicKey);
//    }
//}

class SHA256 {
    public static byte[] hash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input);
    }
}