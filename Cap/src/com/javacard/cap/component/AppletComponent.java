package com.javacard.cap.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.javacard.cap.Cap;
import com.javacard.cap.Formatter;
import com.watchdata.commons.lang.WDAssert;

public class AppletComponent extends Formatter {
	@Override
	public String format(String componentInfo) throws IOException {
		String headerFormat = read("AppletComponent");
		if (WDAssert.isNotEmpty(headerFormat)) {
			return paddingExt(headerFormat, new StringReader(componentInfo));
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> map = Cap.readCap("pboc1200.cap");
		System.out.println(map.get("Applet.cap"));
		String a = new AppletComponent().format(map.get("Applet.cap"));
		System.out.println(a);
	}
}
