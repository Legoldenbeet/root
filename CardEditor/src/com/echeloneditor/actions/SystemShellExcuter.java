package com.echeloneditor.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
		Thread.sleep(200);
		print(is, isErr);
		p.waitFor();
		if (p.exitValue() == 0) {
			System.out.println("excute successful!");
		} else {
			System.out.println("excute unsuccessful!");
		}
		p.destroy();
	}

	public void printLog(InputStream is) throws IOException {
		int len = is.available();
		if (len > 0) {
			byte[] buffer = new byte[len];
			is.read(buffer);
			systemShell.append(new String(buffer, Charset.forName("GBK")));
			systemShell.append("\n");
		}
	}

	private void print(InputStream in, InputStream err) throws IOException {
		printLog(in);
		printLog(err);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<String> cmdList = new ArrayList<String>();

		File file = new File(".");
		cmdList.add("cmd.exe");
		cmdList.add("/c");
		// cmdList.add("start");
		// cmdList.add("ipconfig/all");
		cmdList.add("javac");

		new SystemShellExcuter(null).excute(file, cmdList);
	}
}
