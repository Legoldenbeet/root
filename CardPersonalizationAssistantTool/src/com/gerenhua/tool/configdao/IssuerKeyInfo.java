package com.gerenhua.tool.configdao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gerenhua.tool.utils.Config;

public class IssuerKeyInfo {
	private String acKey;
	private String macKey;
	private String encKey;
	private int derive;
	private String keyName;

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getAcKey() {
		return acKey;
	}

	public void setAcKey(String acKey) {
		this.acKey = acKey;
	}

	public String getMacKey() {
		return macKey;
	}

	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	public int getDerive() {
		return derive;
	}

	public void setDerive(int derive) {
		this.derive = derive;
	}

	public IssuerKeyInfo getIssuerKeyInfo(String sectionName) {
		String current = Config.getValue(sectionName, "currentAppKey");
		return getIssuerKeyInfo(sectionName, current);
	}

	public IssuerKeyInfo getIssuerKeyInfo(String sectionName, String item) {

		IssuerKeyInfo issuerKeyInfo = new IssuerKeyInfo();
		String itemV = Config.getValue(sectionName, item);
		String[] items = itemV.split("\\|");
		issuerKeyInfo.setKeyName(item);
		issuerKeyInfo.setDerive(Integer.parseInt(items[0].toString().trim()));
		issuerKeyInfo.setAcKey(items[1]);
		issuerKeyInfo.setMacKey(items[2]);
		issuerKeyInfo.setEncKey(items[3]);

		return issuerKeyInfo;
	}

	public List<IssuerKeyInfo> getIssuerKeyInfos(String iskSection) {
		List<IssuerKeyInfo> result = new ArrayList<IssuerKeyInfo>();
		Collection<String> isks = Config.getItems(iskSection);

		for (String isk : isks) {
			if (isk.equalsIgnoreCase("currentAppKey")) {
				continue;
			}
			String itemV = Config.getValue(iskSection, isk);
			String[] items = itemV.split("\\|");
			IssuerKeyInfo issuerKeyInfo = new IssuerKeyInfo();
			issuerKeyInfo.setKeyName(isk);
			issuerKeyInfo.setDerive(Integer.parseInt(items[0].toString().trim()));
			issuerKeyInfo.setAcKey(items[1]);
			issuerKeyInfo.setMacKey(items[2]);
			issuerKeyInfo.setEncKey(items[3]);
			result.add(issuerKeyInfo);
		}
		return result;
	}

	public boolean add(IssuerKeyInfo issuerKeyInfo) {
		Config.addItem("ApplicationKey", issuerKeyInfo.getKeyName());
		Config.setValue("ApplicationKey", issuerKeyInfo.getKeyName(), issuerKeyInfo.getDerive() + "|" + issuerKeyInfo.getAcKey() + "|" + issuerKeyInfo.getMacKey() + "|" + issuerKeyInfo.getEncKey());
		return true;
	}

	public boolean del(String keyName) {
		Config.delItem("ApplicationKey", keyName);
		return true;
	}
}
