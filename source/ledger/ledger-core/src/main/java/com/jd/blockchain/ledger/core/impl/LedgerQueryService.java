package com.jd.blockchain.ledger.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.ContractAccountSet;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.DataAccountSet;
import com.jd.blockchain.ledger.core.LedgerAdministration;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.TransactionSet;
import com.jd.blockchain.ledger.core.UserAccountSet;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.QueryUtil;

public class LedgerQueryService implements BlockchainQueryService {

	private LedgerService ledgerService;

	public LedgerQueryService(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return ledgerService.getLedgerHashs();
	}

	@Override
	public LedgerInfo getLedger(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerInfo ledgerInfo = new LedgerInfo();
		ledgerInfo.setHash(ledger.getHash());
		ledgerInfo.setLatestBlockHash(ledger.getLatestBlockHash());
		ledgerInfo.setLatestBlockHeight(ledger.getLatestBlockHeight());
		return ledgerInfo;
	}

	@Override
	public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash) {
		return ledgerAdministration(ledgerHash).getParticipants();
	}

	@Override
	public LedgerMetadata getLedgerMetadata(HashDigest ledgerHash) {
		return ledgerAdministration(ledgerHash).getMetadata();
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, long height) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		return ledger.getBlock(height);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, HashDigest blockHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		return ledger.getBlock(blockHash);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, long height) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		TransactionSet txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, HashDigest blockHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		TransactionSet txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getTransactionTotalCount(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionSet txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, long height) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotalCount();
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, HashDigest blockHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotalCount();
	}

	@Override
	public long getDataAccountTotalCount(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotalCount();
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, long height) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		UserAccountSet userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotalCount();
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, HashDigest blockHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		UserAccountSet userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotalCount();
	}

	@Override
	public long getUserTotalCount(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountSet userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotalCount();
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, long height) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		ContractAccountSet contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotalCount();
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, HashDigest blockHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		ContractAccountSet contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotalCount();
	}

	@Override
	public long getContractTotalCount(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountSet contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotalCount();
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, long height, int fromIndex, int count) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock ledgerBlock = ledger.getBlock(height);
		TransactionSet transactionSet = ledger.getTransactionSet(ledgerBlock);
		int lastHeightTxTotalNums = 0;

		if (height > 0) {
			lastHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height - 1)).getTotalCount();
		}

		int currentHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height)).getTotalCount();
		// 取当前高度的增量交易数，在增量交易里进行查找
		int currentHeightTxNums = currentHeightTxTotalNums - lastHeightTxTotalNums;
//
//		if (fromIndex < 0 || fromIndex >= currentHeightTxNums) {
//			fromIndex = 0;
//		}
//		if (count == -1) {
//			fromIndex = 0;
//			count = currentHeightTxNums;
//		}
//		if (count > currentHeightTxNums) {
//			count = currentHeightTxNums - fromIndex;
//		}
		int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex, count, currentHeightTxNums);
		return transactionSet.getTxs(lastHeightTxTotalNums + indexAndCount[0], indexAndCount[1]);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, HashDigest blockHash, int fromIndex, int count) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock ledgerBlock = ledger.getBlock(blockHash);
		long height = ledgerBlock.getHeight();
		TransactionSet transactionSet = ledger.getTransactionSet(ledgerBlock);
		int lastHeightTxTotalNums = 0;

		if (height > 0) {
			lastHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height - 1)).getTotalCount();
		}

		int currentHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height)).getTotalCount();
		// 取当前块hash的增量交易数，在增量交易里进行查找
		int currentHeightTxNums = currentHeightTxTotalNums - lastHeightTxTotalNums;

//		if (fromIndex < 0 || fromIndex >= currentHeightTxNums) {
//			fromIndex = 0;
//		}
//		if (count == -1) {
//			fromIndex = 0;
//			count = currentHeightTxNums;
//		}
//		if (count > currentHeightTxNums) {
//			count = currentHeightTxNums - fromIndex;
//		}
		int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex, count, currentHeightTxNums);
		return transactionSet.getTxs(lastHeightTxTotalNums + indexAndCount[0], indexAndCount[1]);
	}

	@Override
	public LedgerTransaction getTransactionByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionSet txset = ledger.getTransactionSet(block);
		return txset.get(contentHash);
	}

	@Override
	public TransactionState getTransactionStateByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionSet txset = ledger.getTransactionSet(block);
		return txset.getTxState(contentHash);
	}

	@Override
	public UserInfo getUser(HashDigest ledgerHash, String address) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountSet userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getUser(address);

	}

	@Override
	public AccountHeader getDataAccount(HashDigest ledgerHash, String address) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getDataAccount(Bytes.fromBase58(address));
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, String... keys) {
		if (keys == null || keys.length == 0) {
			return null;
		}
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getDataAccount(Bytes.fromBase58(address));

		KVDataEntry[] entries = new KVDataEntry[keys.length];
		long ver;
		for (int i = 0; i < entries.length; i++) {
			final String currKey = keys[i];

			ver = dataAccount.getDataVersion(Bytes.fromString(currKey));

			if (ver < 0) {
				entries[i] = new KVDataObject(currKey, -1, null);
			} else {
				BytesValue value = dataAccount.getBytes(Bytes.fromString(currKey), ver);
				entries[i] = new KVDataObject(currKey, ver, value);
			}
		}

		return entries;
	}

	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, KVInfoVO kvInfoVO) {
		// parse kvInfoVO;
		List<String> keyList = new ArrayList<>();
		List<Long> versionList = new ArrayList<>();
		if (kvInfoVO != null) {
			for (KVDataVO kvDataVO : kvInfoVO.getData()) {
				for (Long version : kvDataVO.getVersion()) {
					keyList.add(kvDataVO.getKey());
					versionList.add(version);
				}
			}
		}
		String[] keys = keyList.toArray(new String[keyList.size()]);
		Long[] versions = versionList.toArray(new Long[versionList.size()]);

		if (keys == null || keys.length == 0) {
			return null;
		}
		if (versions == null || versions.length == 0) {
			return null;
		}
		if (keys.length != versions.length) {
			throw new ContractException("keys.length!=versions.length!");
		}

		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getDataAccount(Bytes.fromBase58(address));

		KVDataEntry[] entries = new KVDataEntry[keys.length];
		long ver = -1;
		for (int i = 0; i < entries.length; i++) {
//			ver = dataAccount.getDataVersion(Bytes.fromString(keys[i]));
//			dataAccount.getBytes(Bytes.fromString(keys[i]),1);
			ver = versions[i];
			if (ver < 0) {
				entries[i] = new KVDataObject(keys[i], -1, null);
			} else {
				if (dataAccount.getDataEntriesTotalCount() == 0
						|| dataAccount.getBytes(Bytes.fromString(keys[i]), ver) == null) {
					// is the address is not exist; the result is null;
					entries[i] = new KVDataObject(keys[i], -1, null);
				} else {
					BytesValue value = dataAccount.getBytes(Bytes.fromString(keys[i]), ver);
					entries[i] = new KVDataObject(keys[i], ver, value);
				}
			}
		}

		return entries;
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, int fromIndex, int count) {

		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getDataAccount(Bytes.fromBase58(address));

		return dataAccount.getDataEntries(fromIndex, count);
	}

	@Override
	public long getDataEntriesTotalCount(HashDigest ledgerHash, String address) {

		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getDataAccount(Bytes.fromBase58(address));

		return dataAccount.getDataEntriesTotalCount();
	}

	@Override
	public ContractInfo getContract(HashDigest ledgerHash, String address) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountSet contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getContract(Bytes.fromBase58(address));
	}

	@Override
	public AccountHeader[] getUsers(HashDigest ledgerHash, int fromIndex, int count) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountSet userAccountSet = ledger.getUserAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCount(fromIndex, count, (int) userAccountSet.getTotalCount());
		return userAccountSet.getAccounts(pages[0], pages[1]);
	}

	@Override
	public AccountHeader[] getDataAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountSet dataAccountSet = ledger.getDataAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCount(fromIndex, count, (int) dataAccountSet.getTotalCount());
		return dataAccountSet.getAccounts(pages[0], pages[1]);
	}

	@Override
	public AccountHeader[] getContractAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountSet contractAccountSet = ledger.getContractAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCount(fromIndex, count, (int) contractAccountSet.getTotalCount());
		return contractAccountSet.getAccounts(pages[0], pages[1]);
	}

	private LedgerAdministration ledgerAdministration(HashDigest ledgerHash) {
		LedgerRepository ledger = ledgerService.getLedger(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		LedgerAdministration administration = ledger.getAdminAccount(block);
		return administration;
	}
}
