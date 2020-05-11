package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;

@DataContract(code= DataCodes.ACCOUNT_HEADER)
public interface AccountHeader {
	
	@DataField(order=1, primitiveType = PrimitiveType.BYTES)
	Bytes getAddress();
	
	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	PubKey getPubKey();
	
	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	HashDigest getRootHash();
	
}
