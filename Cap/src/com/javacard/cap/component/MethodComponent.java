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
		if (WDAssert.isNotEmpty(headerFormat)) {
			return paddingExt(headerFormat, new StringReader(componentInfo));
		}

		return null;
	}

	public static String paddingExt(String formatter, StringReader hexReader) throws IOException {
		String cacheFormatter = "";

		StringBuilder sb = new StringBuilder();
		String[] line = formatter.split(lineSep);
		int countpos = 0;
		for (String lineStr : line) {
			int linecharNum = lineStr.length() + lineSep.length();
			int u1pos = lineStr.indexOf("u1");
			int u2pos = lineStr.indexOf("u2");
			int u4pos = lineStr.indexOf("u4");
			int start = lineStr.indexOf("[");
			int end = lineStr.indexOf("]");
			int starth = lineStr.indexOf("{");
			int endh = lineStr.indexOf("}");
			int commentPos=lineStr.indexOf("//");
			if (commentPos >=0) {
				continue;
			} else if (u1pos > 0) {
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
				sb.append(lineStr + lineSep);
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
				sb.append(lineStr + lineSep);
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
				sb.append(lineStr + lineSep);
			} else if ((start > 0 && end > 0) && (starth > 0 || endh > 0)) {

				String key = lineStr.substring(start + 1, end);
				if (isNumeric(key)) {
				} else {
					int arrayCount = getArrayCount(key, sb.toString());
					if (arrayCount > 0) {
						if (endh > 0) {
							sb.append(initArray(lineStr, 0) + lineSep);
						}
						for (int i = 0; i < arrayCount - 1; i++) {
							if (starth > 0) {
								cacheFormatter = getCacheFormatter(formatter, countpos + end, countpos + starth, endh);
								sb.append(padding(cacheFormatter, hexReader).replaceAll("\\[" + key + "\\]", "[" + i + "]"));
							} else if (endh > 0) {
								cacheFormatter = getCacheFormatter(formatter, countpos + end, starth, countpos + endh);
								sb.append(padding(cacheFormatter, hexReader).replaceAll("\\[" + key + "\\]", "[" + (i + 1) + "]"));
							}
						}
						if (starth > 0) {
							sb.append(initArray(lineStr, arrayCount - 1) + lineSep);
						}
					} else {
						break;
					}
				}
			} else {
				sb.append(lineStr + lineSep);
			}

			countpos += linecharNum;
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
