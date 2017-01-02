/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 *
 * @author chia.chang@ge.com
 *
 */

package com.ge.ec.libs;

import java.io.IOException;

public interface ECClient {
    
    public boolean launch() throws IOException;
    
    public boolean isAlive();
    
    public boolean terminate() throws IOException;
    
    public String version();
    
    public void setSetting(String key, Object val);
    
    public String getSetting(String key);

	void createScriptFile(String fileName, String environmentName);

	void copyAllLibraryFiles(String libraryName);

}
