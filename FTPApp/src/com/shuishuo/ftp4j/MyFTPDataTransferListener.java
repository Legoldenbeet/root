package com.shuishuo.ftp4j;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class MyFTPDataTransferListener implements FTPDataTransferListener {
	public long downLoadfileLen = 0;
	public long fileSize = 0;

	public MyFTPDataTransferListener(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		System.out.println("aborted.");
	}

	@Override
	public void completed() {
		// TODO Auto-generated method stub
		System.out.println("completed.");
	}

	@Override
	public void failed() {
		// TODO Auto-generated method stub
		System.out.println("failed.");
	}

	@Override
	public void started() {
		// TODO Auto-generated method stub
		System.out.println("started.");
	}

	@Override
	public void transferred(int len) {
		// TODO Auto-generated method stub
		downLoadfileLen += len;
		System.out.println("transferred:" + len + "|process:" + ((double)downLoadfileLen / fileSize) * 100 + "%");
	}

}
