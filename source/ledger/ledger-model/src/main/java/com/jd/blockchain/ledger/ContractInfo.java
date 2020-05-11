package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONTRACT)
public interface ContractInfo extends AccountHeader {

    @DataField(order=4, primitiveType= PrimitiveType.BYTES)
    byte[] getChainCode();
}
