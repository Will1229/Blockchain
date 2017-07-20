package agent;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block<T> implements Serializable {
    private int index;
    private Long timestamp;
    private String hash;
    private String previousHash;
    private T payload;

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
//                ", hash='" + hash + '\'' +
//                ", previousHash='" + previousHash + '\'' +
                ", payload=" + payload +
                '}';
    }

    public Block(int index, String preHash, T payload) {
        this.index = index;
        this.previousHash = preHash;
        this.payload = payload;
        timestamp = System.currentTimeMillis();
        hash = calculateHash(String.valueOf(index) + previousHash + String.valueOf(timestamp) + payload.toString());
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public T getPayload() {
        return payload;
    }

    private String calculateHash(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return "HASH_ERROR";
        }

        final byte bytes[] = digest.digest(text.getBytes());
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}