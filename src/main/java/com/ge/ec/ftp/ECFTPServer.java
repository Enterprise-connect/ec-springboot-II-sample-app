package com.ge.ec.ftp;

public interface ECFTPServer {
	String FTP_IMPL_TYPE="SERVER";
	boolean createFtpServer();
	boolean isServerCreated();
}
