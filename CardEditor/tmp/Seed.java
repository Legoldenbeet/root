/*
	CLA : 
	INS : 0xE4
	P1  : Encrypt(0x00), Decrypt(0x01)
	P2  : 00
	LC  : Key + PlainText or Key + Encryption Data
	LE  : 
*/

package Seed;

import javacard.framework.*;

import koreanpackage.crypto.*;

public class Seed extends javacard.framework.Applet{

	KrCipher cipher_nrpad = null;

	private SeedKey	sk;

	private	byte[] Seed_Key = new byte[16];
	private byte[] EncDecData = new byte[128];
	private byte[] OutputData = new byte[128];

	short byteRead, OutLen;

	private Seed(){
		sk = (SeedKey) KrKeyBuilder.buildKey(KrConstants.TYPE_SEED, KrConstants.LENGTH_SEED, false);
		cipher_nrpad = KrCipher.getInstance(KrConstants.ALG_SEED_ECB_NRPAD, false);
	}

	public static void install(byte[] bArray, short bOffset, byte bLength){
		(new Seed()).register(bArray, (short)(bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu){
		byte[] buf = apdu.getBuffer();
		
		if (selectingApplet()) return;

		switch(buf[ISO7816.OFFSET_INS]){
			case (byte)0xE4:
				doEncDec(apdu);
				break;
			default:
				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	private void doEncDec(APDU apdu){
		byte[] buf = apdu.getBuffer();

		byteRead = apdu.setIncomingAndReceive();

		OutLen = (short)0;
		
		Util.arrayFillNonAtomic(Seed_Key, (short)0, (short)16, (byte)0x00);
		Util.arrayFillNonAtomic(EncDecData, (short)0, (short)128, (byte)0x00);
		Util.arrayFillNonAtomic(OutputData, (short)0, (short)128, (byte)0x00);
		
		byteRead -= (short)16;
		 
		Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_CDATA, Seed_Key, (short)0, (short)16);
		Util.arrayCopyNonAtomic(buf, (short)(ISO7816.OFFSET_CDATA+16), EncDecData, (short)0, (short)byteRead);

		sk.setKey(Seed_Key, (short)0);

		switch(buf[ISO7816.OFFSET_P1]){
			case (byte)0x00:
				cipher_nrpad.init(sk, KrConstants.MODE_ENCRYPT);
				OutLen = cipher_nrpad.doFinal(EncDecData, (short)0, (short)byteRead, OutputData, (short)0);
				break;
			case (byte)0x01:
				cipher_nrpad.init(sk, KrConstants.MODE_DECRYPT);
				OutLen = cipher_nrpad.doFinal(EncDecData, (short)0, (short)byteRead, OutputData, (short)0);
				break;
			default:
				ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		}

		//apdu.setOutgoing();
		Util.arrayCopyNonAtomic(OutputData, (short)0, buf, (short)0, OutLen);
		//apdu.setOutgoingLength(OutLen);
		apdu.setOutgoingAndSend((short)0, OutLen);
	}	
}

