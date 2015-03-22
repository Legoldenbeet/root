package com.javacard.cap.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.javacard.cap.Cap;
import com.javacard.cap.Formatter;
import com.watchdata.commons.lang.WDAssert;

public class DebugComponent extends Formatter {
	@Override
	public String format(String componentInfo) throws IOException {
		String headerFormat = read("DebugComponent");
		if (WDAssert.isNotEmpty(headerFormat)) {
			return paddingExt(headerFormat, new StringReader(componentInfo));
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> map = Cap.readCap("pboc1200.cap");
		System.out.println(map.get("Debug.cap"));
		String a = new DebugComponent().format(map.get("Debug.cap"));
		System.out.println(a);
	}
}
