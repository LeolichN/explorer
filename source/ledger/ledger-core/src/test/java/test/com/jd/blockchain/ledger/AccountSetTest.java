package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.core.AccountSet;
import com.jd.blockchain.ledger.core.BaseAccount;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.impl.OpeningAccessPolicy;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class AccountSetTest {
	

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };


	@Test
	public void test() {
		OpeningAccessPolicy accessPolicy = new OpeningAccessPolicy();

		MemoryKVStorage storage = new MemoryKVStorage();

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		CryptoConfig cryptoConf = new CryptoConfig();
		cryptoConf.setSupportedProviders(supportedProviders);
		cryptoConf.setAutoVerifyHash(true);
		cryptoConf.setHashAlgorithm(ClassicAlgorithm.SHA256);

		String keyPrefix = "";
		AccountSet accset = new AccountSet(cryptoConf, keyPrefix, storage, storage, accessPolicy);

		BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
		accset.register(userKey.getAddress(), userKey.getPubKey());

		BaseAccount userAcc = accset.getAccount(userKey.getAddress());
		assertNotNull(userAcc);
		assertTrue(accset.contains(userKey.getAddress()));

		accset.commit();
		HashDigest rootHash = accset.getRootHash();
		assertNotNull(rootHash);

		AccountSet reloadAccSet = new AccountSet(rootHash, cryptoConf, keyPrefix, storage, storage, true, accessPolicy);
		BaseAccount reloadUserAcc = reloadAccSet.getAccount(userKey.getAddress());
		assertNotNull(reloadUserAcc);
		assertTrue(reloadAccSet.contains(userKey.getAddress()));

		assertEquals(userAcc.getAddress(), reloadUserAcc.getAddress());
		assertEquals(userAcc.getPubKey(), reloadUserAcc.getPubKey());
	}

}
