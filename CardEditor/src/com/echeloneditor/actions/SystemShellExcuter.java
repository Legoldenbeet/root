package com.echeloneditor.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.echeloneditor.main.SystemShell;

public class SystemShellExcuter {
	public SystemShell systemShell;
	public static Process p;
	public static ProcessBuilder pb;

	public SystemShellExcuter(SystemShell systemShell) {
		this.systemShell = systemShell;
	}

	/**
	 * 
	 * @param dir
	 * @param windowCommand
	 * @throws Exception
	 */
	public void excute(File dir, String windowCommand) throws Exception {
		List<String> cmdList = new ArrayList<String>();
		for (String cmd : windowCommand.split(" ")) {
			cmdList.add(cmd);
		}
		excute(dir, cmdList);
	}

	/**
	 * 
	 * @param dir
	 * @param cmdList
	 * @param logPrint
	 * @throws Exception
	 */
	public void excute(File dir, List<String> cmdList) throws Exception {
		pb = new ProcessBuilder(cmdList);
		pb.directory(dir);

		p = pb.start();

		InputStream is = p.getInputStream();
		InputStream isErr = p.getErrorStream();
		
		print(is, isErr);
		p.waitFor();
		p.destroy();
	}

	public void printLog(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));
		String line = null;
		while ((line = br.readLine()) != null) {
			systemShell.append(line);
			systemShell.append("\n");
		}
		is.close();
		br.close();
	}

	private void print(InputStream in, InputStream err) throws IOException {
		printLog(in);
		printLog(err);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
