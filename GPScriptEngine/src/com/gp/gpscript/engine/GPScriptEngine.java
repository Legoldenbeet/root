package com.gp.gpscript.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;

import com.gp.gpscript.script.NativeApplication;
import com.gp.gpscript.script.NativeByteString;
import com.gp.gpscript.script.NativeGPApplication;
import com.gp.gpscript.script.NativeGPSecurityDomain;
import com.watchdata.kms.kmsi.IKmsException;

public class GPScriptEngine extends ScriptEngine {
	public GPScriptEngine(String selectedFragment, String secript, String cardProfilePath) throws Exception {
		super(selectedFragment, secript, cardProfilePath);
	}
	
	public boolean execEngineIssue() throws Exception {
		try {
			prepareScriptContext(selectedFragment, secript, cardProfilePath);
		} catch (Exception e) {
			return false;
		}
		// 数据映射到脚本变量
		try {
			dataMapping();
		} catch (Exception e) {
			throw e;
		}

		// 执行脚本
		boolean succflag = false;
		try {
			succflag = evaluateScript();
		} catch (Exception e) {
			throw new Exception("脚本引擎出错,请检查!");
		}

		return succflag;
	}

	public static void issueTest() throws Exception {
		FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir") + "/demo.xml"));
		int len = fis.available();
		byte[] fileByte = new byte[len];

		fis.read(fileByte);
		fis.close();
		HashMap mapBean = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> mapValues = new HashMap<String, String>();
		// mapValues.put("dgi0101", "706B571862A4C0FB7C8D3E1C86B4E73D02636A73BEC3026872A306679F1F4E253939363233303736303032373030303030303031385E20202020202020202020202020202020202020202020202020205E3232313132323030303030303030303030303030303030303030303F");
		// mapValues.put("dgi0102", "70055F20022020");
		// mapValues.put("dgi0201", "70465F24032211285A0A6230760027000000018F5F3401009F0702FF008E0C000000000000000002031F009F0D05D86004A8009F0E0500109800009F0F05D86804F8005F28020156");
		mapValues.put("CPS_Output", "01010770055F2002202002014870465F24032211285A0A6230760027000000018F5F3401009F0702FF008E0C000000000000000002031F009F0D05D86004A8009F0E0500109800009F0F05D86804F8005F28020156");
		/*
		 * mapValues.put("dgi0202", ""); mapValues.put("dgi0203", ""); mapValues.put("dgi0204", ""); mapValues.put("dgi0205", ""); mapValues.put("dgi0206", ""); mapValues.put("dgi0301", ""); mapValues.put("dgi0302", ""); mapValues.put("dgi0401", ""); mapValues.put("dgi0501", "");
		 */
		// mapValues.put("pan", "6230760027000000018F");
		mapBean.put("0", mapValues);
		// GPScriptEngine gpScriptEngine=new GPScriptEngine("VSDC Data Preparation", new String(fileByte), System.getProperty("user.dir") + "/profiles/GPCardProfile.xml");
		GPScriptEngine gpScriptEngine = new GPScriptEngine("PERSONALIZE", new String(fileByte), System.getProperty("user.dir") + "/profiles/GPCardProfile.xml");
		gpScriptEngine.setReader("SCM Microsystems Inc. SCR3310 v2.0 USB SC Reader 0");
		// gpScriptEngine.setCount(1);
		// String[] a = gpScriptEngine.execEngineDP();
		gpScriptEngine.execEngineIssue();
		// System.out.println(a[0]);
	}

	public static void main(String[] args) throws Exception {
		issueTest();
	}
}
