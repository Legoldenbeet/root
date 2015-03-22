/*
 * gpj - Global Platform for Java SmartCardIO
 *
 * Copyright (C) 2009 Wojciech Mostowski, woj@cs.ru.nl
 * Copyright (C) 2009 Francois Kooman, F.Kooman@student.science.ru.nl
 * Copyright (C) 2015 Martin Paljak, martin@martinpaljak.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package com.gerenhua.tool.globalplatform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.gerenhua.tool.logic.Constants;
import com.watchdata.commons.lang.WDByteUtil;

/**
 * Parses a CAP file as specified in JavaCard 2.2 VM Specification, chapter 6.
 * 
 */
public class CapFile {

	public static final String[] componentNames = { "Header", "Directory", "Import", "Applet", "Class", "Method", "StaticField", "Export", "ConstantPool", "RefLocation", "Descriptor", "Debug" };

	private final HashMap<String, byte[]> capComponents = new HashMap<String, byte[]>();
	private String packageName = null;
	private AID packageAID = null;
	private byte major_version = 0;
	private byte minor_version = 0;
	private final List<AID> appletAIDs = new ArrayList<AID>();
	private final List<byte[]> dapBlocks = new ArrayList<byte[]>();
	private final List<byte[]> loadTokens = new ArrayList<byte[]>();
	private final List<byte[]> installTokens = new ArrayList<byte[]>();
	private Manifest manifest = null;

	public CapFile(InputStream in) throws IOException {
		this(in, null);
	}

	private CapFile(InputStream in, String packageName) throws IOException {
		ZipInputStream zip = new ZipInputStream(in);
		Map<String, byte[]> entries = getEntries(zip);
		if (packageName != null) {
			packageName = packageName.replace('.', '/') + "/javacard/";
		} else {
			Iterator<? extends String> it = entries.keySet().iterator();
			String lookFor = "Header.cap";
			while (it.hasNext()) {
				String s = it.next();
				if (s.endsWith(lookFor)) {
					packageName = s.substring(0, s.lastIndexOf(lookFor));
					break;
				}
			}
		}

		// Parse manifest
		byte[] mf = entries.remove("META-INF/MANIFEST.MF");
		if (mf != null) {
			ByteArrayInputStream mfi = new ByteArrayInputStream(mf);
			manifest = new Manifest(mfi);
		}

		// Avoid a possible NPE
		if (packageName == null) {
			throw new RuntimeException("Could not figure out the package name of the applet!");
		}

		this.packageName = packageName.substring(0, packageName.lastIndexOf("/javacard/")).replace('/', '.');
		for (String name : componentNames) {
			String fullName = packageName + name + ".cap";
			byte[] contents = entries.get(fullName);
			capComponents.put(name, contents);
		}
		List<List<byte[]>> tables = new ArrayList<List<byte[]>>();
		tables.add(dapBlocks);
		tables.add(loadTokens);
		tables.add(installTokens);
		String[] names = { "dap", "lt", "it" };
		for (int i = 0; i < names.length; i++) {
			int index = 0;
			while (true) {
				String fullName = "meta-inf/" + packageName.replace('/', '-') + names[i] + (index + 1);
				byte[] contents = entries.get(fullName);
				if (contents == null) {
					break;
				}
				tables.get(i).add(contents);
				index++;
			}
		}
		zip.close();
		in.close();

		// Parse package.
		// See JCVM 2.2 spec section 6.3 for offsets.
		byte[] header = capComponents.get("Header");
		major_version = header[10];
		minor_version = header[11];
		packageAID = new AID(header, 13, header[12]);

		// Parse applets
		// See JCVM 2.2 spec section 6.5 for offsets.
		byte[] applet = capComponents.get("Applet");
		if (applet != null) {
			int offset = 4;
			for (int j = 0; j < (applet[3] & 0xFF); j++) {
				int len = applet[offset++];
				appletAIDs.add(new AID(applet, offset, len));
				// Skip install_method_offset
				offset += len + 2;
			}
		}
	}

	private Map<String, byte[]> getEntries(ZipInputStream in) throws IOException {
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		while (true) {
			ZipEntry entry = in.getNextEntry();
			if (entry == null) {
				break;
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int c;
			while ((c = in.read(buf)) > 0) {
				bos.write(buf, 0, c);
			}
			result.put(entry.getName(), bos.toByteArray());
		}
		return result;
	}

	public AID getPackageAID() {
		return packageAID;
	}

	public List<AID> getAppletAIDs() {
		List<AID> result = new ArrayList<AID>();
		result.addAll(appletAIDs);
		return result;
	}

	public String getPackageName() {
		return packageName;
	}

	public int getCodeLength(boolean includeDebug) {
		int result = 0;
		for (String name : componentNames) {
			if (!includeDebug && (name.equals("Debug") || name.equals("Descriptor"))) {
				continue;
			}
			byte[] data = capComponents.get(name);
			if (data != null) {
				result += data.length;
			}
		}
		return result;
	}

	private byte[] createHeader(boolean includeDebug) {
		int len = getCodeLength(includeDebug);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		bo.write((byte) 0xC4);
		// FIXME: usual length encoding.
		if (len < 0x80) {
			bo.write((byte) len);
		} else if (len <= 0xFF) {
			bo.write((byte) 0x81);
			bo.write((byte) len);
		} else if (len <= 0xFFFF) {
			bo.write((byte) 0x82);
			bo.write((byte) ((len & 0xFF00) >> 8));
			bo.write((byte) (len & 0xFF));
		} else {
			bo.write((byte) 0x83);
			bo.write((byte) ((len & 0xFF0000) >> 16));
			bo.write((byte) ((len & 0xFF00) >> 8));
			bo.write((byte) (len & 0xFF));
		}
		return bo.toByteArray();
	}

	public List<byte[]> getLoadBlocks(boolean includeDebug, boolean separateComponents, int blockSize) {
		List<byte[]> blocks = new ArrayList<byte[]>();

		if (!separateComponents) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			try {
				bo.write(createHeader(includeDebug));
				bo.write(getRawCode(includeDebug));
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			blocks = splitArray(bo.toByteArray(), blockSize);
		} else {
			for (String name : componentNames) {
				if (!includeDebug && (name.equals("Debug") || name.equals("Descriptor"))) {
					continue;
				}

				byte[] currentComponent = capComponents.get(name);
				if (currentComponent == null) {
					continue;
				}
				if (name.equals("Header")) {
					ByteArrayOutputStream bo = new ByteArrayOutputStream();
					try {
						bo.write(createHeader(includeDebug));
						bo.write(currentComponent);
					} catch (IOException ioe) {
						throw new RuntimeException(ioe);
					}
					currentComponent = bo.toByteArray();
				}
				blocks = splitArray(currentComponent, blockSize);
			}
		}
		return blocks;
	}

	private byte[] getRawCode(boolean includeDebug) {
		byte[] result = new byte[getCodeLength(includeDebug)];
		int offset = 0;
		for (String name : componentNames) {
			if (!includeDebug && (name.equals("Debug") || name.equals("Descriptor"))) {
				continue;
			}
			byte[] currentComponent = capComponents.get(name);
			if (currentComponent == null) {
				continue;
			}
			System.arraycopy(currentComponent, 0, result, offset, currentComponent.length);
			offset += currentComponent.length;
		}
		return result;
	}

	public byte[] getLoadFileDataHash(boolean includeDebug) {
		try {
			return MessageDigest.getInstance("SHA1").digest(getRawCode(includeDebug));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Not possible", e);
		}
	}

	private List<byte[]> splitArray(byte[] array, int blockSize) {
		List<byte[]> result = new ArrayList<byte[]>();

		int len = array.length;
		int offset = 0;
		int left = len - offset;
		while (left > 0) {
			int currentLen = 0;
			if (left >= blockSize) {
				currentLen = blockSize;
			} else {
				currentLen = left;
			}
			byte[] block = new byte[currentLen];
			System.arraycopy(array, offset, block, 0, currentLen);
			result.add(block);
			left -= currentLen;
			offset += currentLen;
		}
		return result;
	}

	public void dump(List<String> out) {
		SimpleDateFormat sf = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
		// Print information about CAP. First try manifest.
		if (manifest != null) {
			Attributes mains = manifest.getMainAttributes();
			// iterate all packages
			Map<String, Attributes> ent = manifest.getEntries();
			if (ent.keySet().size() > 1) {
				throw new IllegalArgumentException("Too many elments in CAP");
			}
			Attributes caps = ent.get(ent.keySet().toArray()[0]);
			// Generic
			String jdk_name = mains.getValue("Created-By");
			// JC specific
			String cap_version = caps.getValue("Java-Card-CAP-File-Version");
			String cap_creation_time = sf.format(new Date(caps.getValue("Java-Card-CAP-Creation-Time")));
			String converter_version = caps.getValue("Java-Card-Converter-Version");
			String converter_provider = caps.getValue("Java-Card-Converter-Provider");
			String package_name = caps.getValue("Java-Card-Package-Name");
			String package_version = caps.getValue("Java-Card-Package-Version");
			String package_aid = caps.getValue("Java-Card-Package-AID");

			int num_applets = 0;
			int num_imports = 0;
			// Count applets and imports
			for (Object e : caps.keySet()) {
				Attributes.Name an = (Attributes.Name) e;
				String s = an.toString();
				if (s.startsWith("Java-Card-Applet-") && s.endsWith("-Name")) {
					num_applets++;
				} else if (s.startsWith("Java-Card-Imported-Package-") && s.endsWith("-AID")) {
					num_imports++;
				} else {
					continue;
				}
			}
			out.add("CAP file [v" + cap_version + "] generated on [" + cap_creation_time + "]");
			out.add("By [" + converter_provider + "] Converter-Version:" + converter_version + " with JDK [" + jdk_name + "]");
			out.add("-------------------------------------------------------");
			out.add("Java-Card-Package-Name: " + package_name);
			out.add("Java-Card-Package-Version:" + package_version);
			out.add("Java-Card-Package-AID:" + package_aid);
			out.add("-------------------------------------------------------");
			for (int i = 1; i <= num_applets; i++) {
				String applet_name_key = "Java-Card-Applet-" + i + "-Name";
				String applet_aid_key = "Java-Card-Applet-" + i + "-AID";
				String applet_name = applet_name_key + ":" + caps.getValue(applet_name_key);
				String applet_aid = applet_aid_key + ":" + caps.getValue(applet_aid_key);
				out.add(applet_name);
				out.add(applet_aid);
			}
			for (int i = 1; i <= num_imports; i++) {
				String import_aid_key = "Java-Card-Imported-Package-" + i + "-AID";
				String import_version_key = "Java-Card-Imported-Package-" + i + "-Version";
				String import_aid = import_aid_key + ":" + caps.getValue(import_aid_key);
				String import_version = import_version_key + ":" + caps.getValue(import_version_key);
				out.add(import_aid);
				out.add(import_version);
			}
		} else {
			String pkg_version = major_version + "." + minor_version;
			out.add("No manifest in CAP. Information from Header and Applet components:");
			out.add("Package:" + packageName);
			out.add("pkg_version:" + pkg_version);
			out.add("packageAID:" + packageAID);

			for (AID applet : appletAIDs) {
				out.add("Applet: AID " + WDByteUtil.bytes2HEX(applet.getBytes()));
			}
		}
		out.add("-------------------------------------------------------");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File capfile = new File("E:/capload/com/yct/javacard/applets.cap");
		CapFile cap = new CapFile(new FileInputStream(capfile));
		List<String> outList=new ArrayList<String>();
		cap.dump(outList);
		
		for (String out : outList) {
			System.out.println(out);
		}
		
		List<byte[]> blocksList=cap.getLoadBlocks(false, false, 0xa0);
		for (int i = 0; i < blocksList.size(); i++) {
//			System.out.println(WDByteUtil.bytes2HEX(blocksList.get(i)));
		}
	}
}
