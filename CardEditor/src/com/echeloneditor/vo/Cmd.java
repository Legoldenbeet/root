package com.echeloneditor.vo;

public class Cmd {
	private byte Cla;
	private byte Ins;
	private byte P1;
	private byte P2;
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
}
