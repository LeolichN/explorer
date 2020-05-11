package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 
 * @author huanghaiquan
 *
 */
public class BytesData implements BytesValue {
	DataType type;
	Bytes value;

	private BytesData(DataType type, byte[] bytes) {
		this.type = type;
		this.value = new Bytes(bytes);
	}

	private BytesData(DataType type, Bytes bytes) {
		this.type = type;
		this.value = bytes;
	}
	
	public static BytesValue fromType(DataType type, byte[] value) {
		return new BytesData(type, value);
	}

	public static BytesValue fromBytes(byte[] value) {
		return new BytesData(DataType.BYTES, value);
	}

	public static BytesValue fromBytes(Bytes value) {
		return new BytesData(DataType.BYTES, value);
	}

	public static BytesValue fromImage(byte[] value) {
		return new BytesData(DataType.IMG, value);
	}

	public static BytesValue fromImage(Bytes value) {
		return new BytesData(DataType.IMG, value);
	}

	/**
	 * 以 UTF-8 编码从字符串转换为字节数组值；
	 * 
	 * @param value
	 * @return
	 */
	public static BytesValue fromText(String value) {
		return new BytesData(DataType.TEXT, BytesUtils.toBytes(value));
	}

	/**
	 * 以 UTF-8 编码把字节数组值转换为字符串；
	 * 
	 * @param bytesValue
	 * @return
	 */
	public static String toText(BytesValue bytesValue) {
		if (bytesValue == null) {
			return null;
		}
		if (bytesValue.getType() != DataType.TEXT) {
			throw new ValueTypeCastException("The expected value type is " + DataType.TEXT.toString()
					+ ", but it is actually " + bytesValue.getType().toString() + "!");
		}
		return bytesValue.getValue().toUTF8String();
	}

	public static BytesValue fromJSON(String value) {
		return new BytesData(DataType.JSON, BytesUtils.toBytes(value));
	}

	public static BytesValue fromXML(String value) {
		return new BytesData(DataType.XML, BytesUtils.toBytes(value));
	}

	public static BytesValue fromInt32(int value) {
		return new BytesData(DataType.INT32, BytesUtils.toBytes(value));
	}

	public static BytesValue fromInt64(long value) {
		return new BytesData(DataType.INT64, BytesUtils.toBytes(value));
	}

	public static BytesValue fromInt16(short value) {
		return new BytesData(DataType.INT16, BytesUtils.toBytes(value));
	}

	public static BytesValue fromInt8(byte value) {
		return new BytesData(DataType.INT8, BytesUtils.toBytes(value));
	}

	public static BytesValue fromTimestamp(long value) {
		return new BytesData(DataType.TIMESTAMP, BytesUtils.toBytes(value));
	}

	public static BytesValue fromBoolean(boolean value) {
		return new BytesData(DataType.BOOLEAN, BytesUtils.toBytes(value));
	}

	@Override
	public DataType getType() {
		return this.type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	@Override
	public Bytes getValue() {
		return this.value;
	}

}
