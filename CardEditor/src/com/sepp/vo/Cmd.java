package com.sepp.vo;

public class Cmd {
	private byte Cla;
	private byte Ins;
	private byte P1;
	private byte P2;
	private long Lc;
	public static byte[] data=new byte[10<<20];
	private byte Le;
	public byte getCla() {
		return Cla;
	}
	public void setCla(byte cla) {
		Cla = cla;
	}
	public byte getIns() {
		return Ins;
	}
	public void setIns(byte ins) {
		Ins = ins;
	}
	public byte getP1() {
		return P1;
	}
	public void setP1(byte p1) {
		P1 = p1;
	}
	public byte getP2() {
		return P2;
	}
	public void setP2(byte p2) {
		P2 = p2;
	}
	public long getLc() {
		return Lc;
	}
	public void setLc(long lc) {
		Lc = lc;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public byte getLe() {
		return Le;
	}
	public void setLe(byte le) {
		Le = le;
	}
}
