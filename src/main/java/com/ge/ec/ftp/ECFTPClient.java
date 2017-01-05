package com.ge.ec.ftp;

import java.io.IOException;
import java.util.List;

public interface ECFTPClient {
	String FTP_IMPL_TYPE="CLIENT";
	boolean connectToServer() throws IOException;
	boolean disconnectClient();
	boolean clientStatus();
	List<String> getAllFileNamesFromServer();
}
