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
			return padding(headerFormat, new StringReader(componentInfo));
		}

		return null;
	}

	public static String paddingExt(String formatter, StringReader hexReader) throws IOException {
		String cacheFormatter = "";

		StringBuilder sb = new StringBuilder();
		String[] line = formatter.split(lineSep);
		int countpos = 0;
		for (String lineStr : line) {
			int linecharNum=lineStr.length()+lineSep.length();
			int u1pos = lineStr.indexOf("u1");
			int u2pos = lineStr.indexOf("u2");
			int u4pos = lineStr.indexOf("u4");
			int start = lineStr.indexOf("[");
			int end = lineStr.indexOf("]");
			int starth = lineStr.indexOf("{");
			int endh = lineStr.indexOf("}");
			if (u1pos > 0) {
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);
					if (isNumeric(key)) {
						lineStr = lineStr + ":" + readU1Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU1Array(hexReader, arrayCount) + lineSep;
					}

				} else {
					lineStr = lineStr + ":" + readU1(hexReader) + lineSep;
				}
			} else if (u2pos > 0) {
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);

					if (isNumeric(key)) {
						lineStr = lineStr + ":" + readU2Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU2Array(hexReader, arrayCount) + lineSep;
					}
				} else {
					lineStr = lineStr + ":" + readU2(hexReader) + lineSep;
				}
			} else if (u4pos > 0) {
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);

					if (isNumeric(key)) {
						lineStr = lineStr + ":" + readU4Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU4Array(hexReader, arrayCount) + lineSep;
					}

				} else {
					lineStr = lineStr + ":" + readU4(hexReader) + lineSep;
				}
			} else if ((start > 0 && end > 0) && (starth > 0 || endh > 0)) {

				String key = lineStr.substring(start + 1, end);
				if (isNumeric(key)) {
				} else {
					int arrayCount = getArrayCount(key, sb.toString());
					if (arrayCount > 0) {
						for (int i = 0; i < arrayCount; i++) {
							if (starth > 0) {
								cacheFormatter = getCacheFormatter(formatter, countpos + end, countpos + starth, endh);
							} else if (endh > 0) {
								cacheFormatter = getCacheFormatter(formatter, countpos + end, starth, countpos + endh);
							}
							countpos=0;
							padding(cacheFormatter, hexReader);
						}
					} else {
						break;
					}
				}
			}
			sb.append(lineStr + lineSep);
			countpos += linecharNum;
		}

		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> map = Cap.readCap("pboc1200.cap");
		System.out.println(map.get("Applet.cap"));
		String a = new AppletComponent().format(map.get("Applet.cap"));
		System.out.println(a);
	}
}
