package com.javacard.cap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public abstract class Formatter {
	public static String lineSep = System.getProperty("line.separator");
	public static byte session = -1;

	public abstract String format(String pName, String componentName) throws IOException;

	public static String read(String fFileName) throws IOException {
		String filePath = "/com/javacard/formatter/";
		filePath += fFileName;
		filePath += ".format";

		InputStream is = Formatter.class.getResourceAsStream(filePath);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();
		String str = "";
		while ((str = br.readLine()) != null) {
			sb.append(str).append(lineSep);
		}

		return sb.toString();
	}

	public static String padding(String formatter, StringReader hexReader) throws IOException {
		// StringReader hexReader = new StringReader(hex);

		StringBuilder sb = new StringBuilder();
		String[] line = formatter.split(lineSep);

		for (String lineStr : line) {
			int u1pos = lineStr.indexOf("u1");
			int u2pos = lineStr.indexOf("u2");
			int u4pos = lineStr.indexOf("u4");
			int commentPos = lineStr.indexOf("//");
			if (commentPos >= 0) {
				continue;
			} else if (u1pos > 0) {
				int start = lineStr.indexOf("[");
				int end = lineStr.indexOf("]");
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);
					if (isNumeric(key)) {
						// int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU1Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU1Array(hexReader, arrayCount) + lineSep;
					}

				} else {
					lineStr = lineStr + ":" + readU1(hexReader) + lineSep;
				}
			} else if (u2pos > 0) {
				int start = lineStr.indexOf("[");
				int end = lineStr.indexOf("]");
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);

					if (isNumeric(key)) {
						// int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU2Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU2Array(hexReader, arrayCount) + lineSep;
					}
				} else {
					lineStr = lineStr + ":" + readU2(hexReader) + lineSep;
				}
			} else if (u4pos > 0) {
				int start = lineStr.indexOf("[");
				int end = lineStr.indexOf("]");
				if (start > 0 && end > 0) {
					String key = lineStr.substring(start + 1, end);

					if (isNumeric(key)) {
						// int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU4Array(hexReader, Integer.parseInt(key)) + lineSep;
					} else {
						int arrayCount = getArrayCount(key, sb.toString());
						lineStr = lineStr + ":" + readU4Array(hexReader, arrayCount) + lineSep;
					}

				} else {
					lineStr = lineStr + ":" + readU4(hexReader) + lineSep;
				}
			}
			sb.append(lineStr + lineSep);
		}

		return sb.toString();
	}

	public static String paddingExt(String componentName, String formatter, StringReader hexReader) throws IOException {
		String cacheFormatter = "";

		StringBuilder sb = new StringBuilder();
		String[] line = formatter.split(lineSep);
		int countpos = 0;
		for (String lineStr : line) {
			int linecharNum = lineStr.length() + lineSep.length();
			int u1pos = lineStr.indexOf("u1");
			int u2pos = lineStr.indexOf("u2");
			int u4pos = lineStr.indexOf("u4");
			// int bit4lpos = lineStr.indexOf("bit[4]_left");
			// int bit4rpos = lineStr.indexOf("bit[4]_right");
			int start = lineStr.indexOf("[");
			int end = lineStr.indexOf("]");
			int starth = lineStr.indexOf("{");
			int endh = lineStr.indexOf("}");
			int commentPos = lineStr.indexOf("//");
			if (commentPos >= 0) {
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

	public static int readU1Left(StringReader hexReader, int len) throws IOException {
		char[] u1 = new char[2];
		hexReader.read(u1);
		session = WDByteUtil.HEX2Bytes(String.valueOf(u1))[0];
		byte target = (byte) (session >> len);
		target &= 0x0F;
		return target;
	}

	public static int readU1Right(StringReader hexReader) throws IOException {
		byte target = (byte) (session & 0x0F);
		return target;
	}

	public static String readU1(StringReader hexReader) throws IOException {
		return toHexStyle(readU1_NOPading(hexReader));
	}

	public static String readU1_NOPading(StringReader hexReader) throws IOException {
		char[] u1 = new char[2];
		hexReader.read(u1);
		return String.valueOf(u1);
	}

	public static String readU2(StringReader hexReader) throws IOException {
		return toHexStyle(readU2_NOPading(hexReader));
	}

	public static String readU2_NOPading(StringReader hexReader) throws IOException {
		char[] u2 = new char[4];
		hexReader.read(u2);
		return String.valueOf(u2);
	}

	public static String readU4(StringReader hexReader) throws IOException {
		return toHexStyle(readU4_NOPading(hexReader));
	}

	public static String readU4_NOPading(StringReader hexReader) throws IOException {
		char[] u4 = new char[8];
		hexReader.read(u4);
		return String.valueOf(u4);
	}

	public static String readU1Array(StringReader hexReader, int num) throws IOException {
		return toHexStyle(readU1Array_NOPading(hexReader, num));
	}

	public static String readU1Array_NOPading(StringReader hexReader, int num) throws IOException {
		char[] u1 = new char[2 * num];
		hexReader.read(u1);
		return String.valueOf(u1);
	}

	public static String readU2Array(StringReader hexReader, int num) throws IOException {
		return toHexStyle(readU2Array_NOPading(hexReader, num));
	}

	public static String readU2Array_NOPading(StringReader hexReader, int num) throws IOException {
		char[] u2 = new char[4 * num];
		hexReader.read(u2);
		return String.valueOf(u2);
	}

	public static String readU4Array(StringReader hexReader, int num) throws IOException {
		return toHexStyle(readU4Array_NOPading(hexReader, num));
	}

	public static String readU4Array_NOPading(StringReader hexReader, int num) throws IOException {
		char[] u4 = new char[8 * num];
		hexReader.read(u4);
		return String.valueOf(u4);
	}

	public static String toHexStyle(String hex) {
		StringBuilder sb = new StringBuilder();
		hex = hex.toUpperCase();
		if (hex.length() % 2 != 0) {
			return null;
		}
		int pos = 0;
		while (pos < hex.length()) {
			sb.append("0x" + hex.substring(pos, pos + 2) + " ");
			pos += 2;
		}
		return sb.toString();
	}

	public static String byteHex1(int i) {
		return toHexStyle(WDStringUtil.paddingHeadZero(Integer.toHexString(i), 2));
	}

	public static String initArray(String hex, int index) {
		StringBuilder sb = new StringBuilder();

		int start = hex.indexOf("[");
		int end = hex.indexOf("]");

		sb.append(hex.substring(0, start));
		sb.append("[").append(index).append("]");
		sb.append(hex.substring(end + 1));

		return sb.toString();
	}

	public static int getArrayCount(String key, String buffer) {
		int pos = buffer.lastIndexOf(key);
		pos += key.length();
		if (pos > 0) {
			String hex = buffer.substring(pos + 1, buffer.indexOf(lineSep, pos + 1));
			hex = hex.replaceAll("0x", "").replaceAll(" ", "");
			int count = Integer.parseInt(hex.trim(), 16);
			return count;
		}
		return -1;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String getCacheFormatter(String formatter, int end, int starth, int endh) {
		String res = "";
		int pos = -1;
		if (starth > 0) {
			pos = formatter.lastIndexOf(lineSep, starth);
			endh = formatter.indexOf("}", starth);
			res = formatter.substring(pos, endh + lineSep.length());
		} else if (endh > 0) {
			pos = formatter.lastIndexOf("{", endh);
			pos = formatter.lastIndexOf(lineSep, pos);
			res = formatter.substring(pos + lineSep.length(), end + lineSep.length());
		}
		// System.out.println(res);
		return res;
	}

	// public static String headerComponentExt(String capFileName,StringReader sReader) {
	// StringBuilder sb=new StringBuilder();
	// int major=Integer.parseInt(CapInsight.sessionMap.get(capFileName).get("MAJOR_VERSION"));
	// int minor=Integer.parseInt(CapInsight.sessionMap.get(capFileName).get("MINOR_VERSION"));
	// if (major>=2&&minor>1) {
	// try {
	// sb.append("\r\n\tpackage_name_info {");
	// sb.append("\r\n\t\tu1 name_length:");
	// sb.append(readU1(sReader)+"\r\n");
	// int nameLength = getArrayCount("name_length", sb.toString());
	// sb.append("\t\tu1 name[name_length]：");
	// sb.append(readU1Array(sReader, nameLength));
	// sb.append("\r\n\t}");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// sb.append("\r\n}");
	// return sb.toString();
	// }
	public static void main(String[] args) throws IOException {
		System.out.println(Formatter.read("headercomponent"));
		System.out.println(Formatter.toHexStyle("0021"));
		System.out.println("2".matches("/d"));
		System.out.println(initArray("} applets[count]", 0));
	}
}
