/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.DataAccountRegisterOpTemplateTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午11:03
 * Description:
 */
package test.com.jd.blockchain.ledger.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.transaction.DataAccountRegisterOpTemplate;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class DataAccountRegisterOpTemplateTest {

    private DataAccountRegisterOpTemplate data;

    @Before
    public void initDataAccountRegisterOpTemplate() {
        DataContractRegistry.register(DataAccountRegisterOperation.class);
        DataContractRegistry.register(Operation.class);
        SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		PubKey pubKey = signFunc.generateKeypair().getPubKey();
        BlockchainIdentity contractID = new BlockchainIdentityData(pubKey);
        data = new DataAccountRegisterOpTemplate(contractID);

    }

    @Test
    public void testSerialize_DataAccountRegisterOperation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, DataAccountRegisterOperation.class);
        DataAccountRegisterOperation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        assertEquals(resolvedData.getAccountID().getAddress(), data.getAccountID().getAddress());
        assertEquals(resolvedData.getAccountID().getPubKey(), data.getAccountID().getPubKey());

        System.out.println("------Assert OK ------");
    }

    @Test
    public void testSerialize_Operation() throws Exception {
        byte[] serialBytes = BinaryProtocol.encode(data, Operation.class);
        Operation resolvedData = BinaryProtocol.decode(serialBytes);
        System.out.println("------Assert start ------");
        System.out.println("serialBytesLength=" + serialBytes.length);
        System.out.println(resolvedData);
        System.out.println("------Assert OK ------");
    }
}