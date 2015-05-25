package com.javacard.cap;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.watchdata.commons.lang.WDByteUtil;

public class Cap {
	// COMPONENT_Header 1
	// COMPONENT_Directory 2
	// COMPONENT_Applet 3
	// COMPONENT_Import 4
	// COMPONENT_ConstantPool 5
	// COMPONENT_Class 6
	// COMPONENT_Method 7
	// COMPONENT_StaticField 8
	// COMPONENT_ReferenceLocation 9
	// COMPONENT_Export 10
	// COMPONENT_Descriptor 11
	public static final int COMPONENT_Header = 0x01;
	public static final int COMPONENT_Directory = 0x02;
	public static final int COMPONENT_Applet = 0x03;
	public static final int COMPONENT_Import = 0x04;
	public static final int COMPONENT_ConstantPool = 0x05;
	public static final int COMPONENT_Class = 0x06;
	public static final int COMPONENT_Method = 0x07;
	public static final int COMPONENT_StaticField = 0x08;
	public static final int COMPONENT_ReferenceLocation = 0x09;
	public static final int COMPONENT_Export = 0x0A;
	public static final int COMPONENT_Descriptor = 0x0B;

	public static final String MAGIC_NUMBER="DECAFFED";
//	public static int MAJOR_VERSION = 0x02;
//	public static int MINOR_VERSION = 0x02;
	public static final int ACC_INTERFACE = 0x08;
	
	public static final int ACC_EXTENDED = 0x08;
	public static final int ACC_ABSTRACT = 0x04;

	// read pkg cap
	public static Map<String, String> readCap(String capFilePath) throws IOException {
		Map<String, String> mapBean = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		ZipFile zipFile = new ZipFile(capFilePath);
		Enumeration<?> en = zipFile.entries();
		do {
			// 清空
			sb.setLength(0);

			if (!en.hasMoreElements())
				break;
			ZipEntry ze = (ZipEntry) en.nextElement();
			String zeName = ze.getName();

			if (!zeName.equals("META-INF/MANIFEST.MF") && zeName.endsWith(".cap")) {
				InputStream in = zipFile.getInputStream(ze);
				byte buf[] = new byte[10240];
				do {
					int length = in.read(buf, 0, buf.length);
					if (length != -1) {
						sb.append(WDByteUtil.bytes2HEX(buf, 0, length));
					} else {
						break;
					}
				} while (true);
				zeName = zeName.substring(zeName.lastIndexOf("/") + 1);
				mapBean.put(zeName, sb.toString());
			}
		} while (true);
		zipFile.close();

		checkJavaCardVer(mapBean);

		return mapBean;
	}

	private static void checkJavaCardVer(Map<String, String> mapBean) {
		String headerInfo=mapBean.get("Header.cap");
		try {
			StringReader sReader=new StringReader(headerInfo);
			String comType=Formatter.readU1_NOPading(sReader);
			if (Integer.parseInt(comType)!=COMPONENT_Header) {
				return;
			}
			Formatter.readU2(sReader);//跳过size
			Formatter.readU4(sReader);
			
			mapBean.put("MINOR_VERSION", Formatter.readU1_NOPading(sReader));
			mapBean.put("MAJOR_VERSION", Formatter.readU1_NOPading(sReader));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> mapPse = Cap.readCap("pse.cap");
		Map<String, String> mapPpse = Cap.readCap("ppse.cap");
		Map<String, String> mapPboc = Cap.readCap("pboc1200.cap");

		System.out.println(mapPse);
		System.out.println(mapPpse);
		System.out.println(mapPboc);

		Iterator<?> iterator = mapPboc.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
}
