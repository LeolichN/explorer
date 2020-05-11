package com.jd.blockchain.transaction;

import java.util.Collection;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.utils.Bytes;

public class TxBuilder implements TransactionBuilder {

	static {
		DataContractRegistry.register(TransactionContentBody.class);
	}

	private BlockchainOperationFactory opFactory = new BlockchainOperationFactory();

	private static final String DEFAULT_HASH_ALGORITHM = "SHA256";

	private HashDigest ledgerHash;

	public TxBuilder(HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	@Override
	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	@Override
	public TransactionRequestBuilder prepareRequest() {
		return prepareRequest(System.currentTimeMillis());
	}

	@Override
	public TransactionContent prepareContent() {
		return prepareContent(System.currentTimeMillis());
	}

	@Override
	public TransactionRequestBuilder prepareRequest(long time) {
		TransactionContent txContent = prepareContent(time);
		return new TxRequestBuilder(txContent);
	}

	@Override
	public TransactionContent prepareContent(long time) {
		TxContentBlob txContent = new TxContentBlob(ledgerHash);
		txContent.addOperations(opFactory.getOperations());
		txContent.setTime(time);

		byte[] contentBodyBytes = BinaryProtocol.encode(txContent, TransactionContentBody.class);
		HashDigest contentHash = Crypto.getHashFunction(DEFAULT_HASH_ALGORITHM).hash(contentBodyBytes);
		txContent.setHash(contentHash);

		return txContent;
	}

	public Collection<OperationResultHandle> getReturnValuehandlers() {
		return opFactory.getReturnValuetHandlers();
	}

	@Override
	public LedgerInitOperationBuilder ledgers() {
		return opFactory.ledgers();
	}

	@Override
	public UserRegisterOperationBuilder users() {
		return opFactory.users();
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		return opFactory.dataAccounts();
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		return opFactory.dataAccount(accountAddress);
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		return opFactory.dataAccount(accountAddress);
	}

	@Override
	public ContractCodeDeployOperationBuilder contracts() {
		return opFactory.contracts();
	}

	public ContractEventSendOperationBuilder contractEvents() {
		return opFactory.contractEvents();
	}

	@Override
	public <T> T contract(Bytes address, Class<T> contractIntf) {
		return opFactory.contract(address, contractIntf);
	}

	@Override
	public <T> T contract(String address, Class<T> contractIntf) {
		return opFactory.contract(address, contractIntf);
	}

}
