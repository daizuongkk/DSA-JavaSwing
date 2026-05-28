package crypto;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import model.DsaKeyPairText;
import util.EncodingUtils;

public class DsaService {
    public static final String KEY_ALGORITHM = "DSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withDSA";
    public static final String HASH_ALGORITHM = "SHA-256";
    public static final int KEY_SIZE = 2048;

    public DsaKeyPairText generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();

        return new DsaKeyPairText(
                EncodingUtils.toBase64(keyPair.getPrivate().getEncoded()),
                EncodingUtils.toBase64(keyPair.getPublic().getEncoded()));
    }

    public String sign(byte[] data, String privateKeyBase64) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(parsePrivateKey(privateKeyBase64));
        signature.update(data);
        return EncodingUtils.toBase64(signature.sign());
    }

    public boolean verify(byte[] data, String signatureBase64, String publicKeyBase64) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(parsePublicKey(publicKeyBase64));
        signature.update(data);
        return signature.verify(EncodingUtils.fromBase64(signatureBase64));
    }

    public String sha256(byte[] data) throws GeneralSecurityException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return EncodingUtils.toHex(digest.digest(data));
    }

    public PrivateKey parsePrivateKey(String privateKeyBase64) throws GeneralSecurityException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(EncodingUtils.fromBase64(privateKeyBase64));
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(spec);
    }

    public PublicKey parsePublicKey(String publicKeyBase64) throws GeneralSecurityException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(EncodingUtils.fromBase64(publicKeyBase64));
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(spec);
    }
}
