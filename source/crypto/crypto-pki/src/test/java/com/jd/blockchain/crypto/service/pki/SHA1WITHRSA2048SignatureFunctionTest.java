package com.jd.blockchain.crypto.service.pki;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.*;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: SHA1WITHRSA2048SignatureFunctionTest
 * @description: TODO
 * @date 2019-05-16, 10:49
 */
public class SHA1WITHRSA2048SignatureFunctionTest {

    @Test
    public void getAlgorithmTest() {

        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA2048");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("SHA1withRsa2048");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = Crypto.getAlgorithm("rsa2048");
        assertNull(algorithm);
    }

    @Test
    public void test() {

        // generateKeyPairTest
        CryptoAlgorithm algorithm = Crypto.getAlgorithm("SHA1WITHRSA2048");
        assertNotNull(algorithm);
        SignatureFunction signatureFunction = Crypto.getSignatureFunction(algorithm);
        AsymmetricKeypair keyPair = signatureFunction.generateKeypair();
        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        assertEquals(PUBLIC.CODE, pubKey.getKeyType().CODE);
        assertTrue(pubKey.getRawKeyBytes().length > 259);
        assertEquals(PRIVATE.CODE, privKey.getKeyType().CODE);
        assertTrue(privKey.getRawKeyBytes().length > 1155);
        assertEquals(algorithm.code(), pubKey.getAlgorithm());
        assertEquals(algorithm.code(), privKey.getAlgorithm());
        byte[] algoBytes = CryptoAlgorithm.getCodeBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] { PUBLIC.CODE };
        byte[] privKeyTypeBytes = new byte[] { PRIVATE.CODE };
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes, pubKeyTypeBytes, rawPubKeyBytes), pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes, privKeyTypeBytes, rawPrivKeyBytes), privKey.toBytes());

        // retrievePubKeyTest
        PubKey retrievedPubKey = signatureFunction.retrievePubKey(privKey);
        assertEquals(pubKey.getKeyType(), retrievedPubKey.getKeyType());
        assertEquals(pubKey.getRawKeyBytes().length, retrievedPubKey.getRawKeyBytes().length);
        assertEquals(pubKey.getAlgorithm(), retrievedPubKey.getAlgorithm());
        assertArrayEquals(pubKey.toBytes(), retrievedPubKey.toBytes());

        // signTest
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        SignatureDigest signatureDigest = signatureFunction.sign(privKey, data);
        byte[] signatureBytes = signatureDigest.toBytes();
        assertEquals(2 + 256, signatureBytes.length);
        assertEquals(algorithm.code(), signatureDigest.getAlgorithm());
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), signatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                signatureDigest.getAlgorithm());

        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes, rawSinatureBytes), signatureBytes);

        // verifyTest
        assertTrue(signatureFunction.verify(signatureDigest, pubKey, data));

        // supportPrivKeyTest
        byte[] privKeyBytes = privKey.toBytes();
        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        // resolvePrivKeyTest
        PrivKey resolvedPrivKey = signatureFunction.resolvePrivKey(privKeyBytes);
        assertEquals(PRIVATE.CODE, resolvedPrivKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedPrivKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedPrivKey.getAlgorithm());
        assertArrayEquals(privKeyBytes, resolvedPrivKey.toBytes());

        // supportPubKeyTest
        byte[] pubKeyBytes = pubKey.toBytes();
        assertTrue(signatureFunction.supportPubKey(pubKeyBytes));

        // resolvedPubKeyTest
        PubKey resolvedPubKey = signatureFunction.resolvePubKey(pubKeyBytes);
        assertEquals(PUBLIC.CODE, resolvedPubKey.getKeyType().CODE);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedPubKey.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedPubKey.getAlgorithm());
        assertArrayEquals(pubKeyBytes, resolvedPubKey.toBytes());

        //supportDigestTest
        byte[] signatureDigestBytes = signatureDigest.toBytes();
        assertTrue(signatureFunction.supportDigest(signatureDigestBytes));

        // resolveDigestTest
        SignatureDigest resolvedSignatureDigest = signatureFunction.resolveDigest(signatureDigestBytes);
        assertEquals(256, resolvedSignatureDigest.getRawDigest().length);
        assertEquals(PKIAlgorithm.SHA1WITHRSA2048.code(), resolvedSignatureDigest.getAlgorithm());
        assertEquals((short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | ((byte) 31 & 0x00FF)),
                resolvedSignatureDigest.getAlgorithm());
        assertArrayEquals(signatureDigestBytes, resolvedSignatureDigest.toBytes());
    }
}
