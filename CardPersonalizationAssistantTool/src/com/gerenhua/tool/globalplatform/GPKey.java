package com.gerenhua.tool.globalplatform;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import com.watchdata.commons.lang.WDByteUtil;
/**
 * GPKey encapsulates a key used with GlobalPlatform.
 * It either has value bytes available as plaintext
 * or encapsulates a key from JCA (PKCS#11 etc)
 */
public class GPKey {
	// FIXME: set enum value to what is in GPData
	public enum Type {
		UNKNOWN, DES, DES3, AES
	}
	private int version = 0;
	private int id = 0;
	private int length = -1;
	private Type type = null;

	private byte [] value = null;

	public int getID() {
		return id;
	}
	public int getVersion() {
		return version;
	}
	public byte[] getValue() {
		return value;
	}
	public int getLength() {
		return length;
	}
	public Type getType() {
		return type;
	}

	public GPKey(int version, int id, GPKey ref) {
		this.version = version;
		this.id = id;
		this.type = ref.getType();
		this.length = ref.getLength();
		this.value = new byte[ref.getLength()];
		System.arraycopy(ref.getValue(), 0, value, 0, ref.getLength());
	}

	// Called when parsing KeyInfo template
	public GPKey(int version, int id, int length, int type) {
		this.version = version;
		this.id = id;
		this.length = length;
		// FIXME: these values should be encapsulated somewhere
		if (type == 0x80) {
			this.type = Type.DES3;
		} else if (type == 0x88) {
			this.type = Type.AES;
		} else {
			throw new RuntimeException(getClass().getName() + " currently only support DES and AES keys");
		}
	}


	// Create a key of given type and given bytes
	public GPKey(byte [] v, Type type) {
		if (v.length != 16 && v.length != 24  && v.length != 32)
			throw new IllegalArgumentException("A valid key should be 16/24/32 bytes long");
		this.value = new byte[v.length];
		System.arraycopy(v, 0, value, 0, v.length);
		this.length = v.length;
		this.type = type;

		// Set default ID/version
		id = 0x00;
		version = 0x00;
	}


	public Key getKey(Type type) {
		if (type == Type.DES) {
			return new SecretKeySpec(enlarge(value, 8), "DES");
		} else if (type == Type.DES3) {
			return new SecretKeySpec(enlarge(value, 24), "DESede");
		} else if (type == Type.AES) {
			return new SecretKeySpec(value, "AES");
		} else {
			throw new RuntimeException("Don't know how to handle " + type + " yet");
		}
	}

	public Key getKey() {
		return getKey(this.type);
	}

	public String toString() {
		return "Ver:" + version  + " ID:" + id + " Type:" + type + " Len:" + length + " Value:" + WDByteUtil.bytes2HEX(value);
	}

	public String toStringKey() {
		return type + ":" + WDByteUtil.bytes2HEX(value);
	}
	
	private static byte[] enlarge(byte[] key, int length) {
		if (length == 24) {
			byte[] key24 = new byte[24];
			System.arraycopy(key, 0, key24, 0, 16);
			System.arraycopy(key, 0, key24, 16, 8);
			return key24;
		} else {
			byte[] key8 = new byte[8];
			System.arraycopy(key, 0, key8, 0, 8);
			return key8;
		}
	}
	
	public static void main(String[] args) {
		GPKey gpKey=new GPKey(WDByteUtil.HEX2Bytes("11111111111111111111111111111111"),Type.DES);
		System.out.println(gpKey.toString());
		System.out.println(gpKey.toStringKey());
	}
}