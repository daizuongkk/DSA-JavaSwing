package model;

public class DsaKeyPairText {
    private final String privateKeyBase64;
    private final String publicKeyBase64;

    public DsaKeyPairText(String privateKeyBase64, String publicKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String privateKeyBase64() {
        return privateKeyBase64;
    }

    public String publicKeyBase64() {
        return publicKeyBase64;
    }
}
