package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.core.AccountAccessPolicy;
import com.jd.blockchain.utils.Bytes;

/**
 * 开放的访问策略； <br>
 * 
 * 不做任何访问限制；
 * 
 * @author huanghaiquan
 *
 */
public class OpeningAccessPolicy implements AccountAccessPolicy {

	@Override
	public boolean checkDataWriting(AccountHeader account) {
		return true;
	}

	@Override
	public boolean checkRegistering(Bytes address, PubKey pubKey) {
		return true;
	}

}