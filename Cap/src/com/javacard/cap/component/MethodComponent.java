package com.javacard.cap.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.javacard.cap.Cap;
import com.javacard.cap.Formatter;
import com.watchdata.commons.lang.WDAssert;

public class MethodComponent extends Formatter {
	@Override
	public String format(String componentInfo) throws IOException {
		String headerFormat = read("MethodComponent");
		
		StringBuilder sb=new StringBuilder();
		StringReader sReader=new StringReader(componentInfo);
		
		if (WDAssert.isNotEmpty(headerFormat)) {
			sb.append(paddingExt(headerFormat, sReader));
		}
		int flags=readBit4(sReader);
		if (flags==Cap.ACC_EXTENDED) {
			
		}else {
			sb.append("\textended_method_header_info {\r\n\t\tu1 bitfield {\r\n\t\t\tbit[4] flags:0x");
			sb.append(Integer.toHexString(flags)+"\r\n\t\t\t");
			sb.append("bit[4] padding:0x"+readBit4(sReader));
		}
		
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> map = Cap.readCap("pboc1200.cap");
		System.out.println(map.get("Method.cap"));
		String a = new MethodComponent().format(map.get("Method.cap"));
		System.out.println(a);
	}
}
