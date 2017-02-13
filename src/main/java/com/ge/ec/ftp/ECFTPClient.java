package com.ge.ec.ftp;

import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public interface ECFTPClient {
	String FTP_IMPL_TYPE="CLIENT";
	boolean connectToServer() throws IOException;
	boolean disconnectClient();
	boolean clientStatus();
	List<String> getAllFileNamesFromServer();
	void getDirectoryStructure();
	int recursiveCopy(FTPClient client, FTPFile file, String folderName, int file_copied_count, String rootFolder);
}
