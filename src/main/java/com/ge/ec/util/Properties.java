package com.ge.ec.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class Properties {
	@Value("${mainProxyHost}")
	private String mainProxyHost;
	@Value("${mainProxyPort}")
	private String mainProxyPort;
	@Value("${applyProxy}")
	private String applyProxy;
	@Value("${proxyProtocol}")
	private String proxyProtocol;
	
	public String getProxyProtocol() {
		return proxyProtocol;
	}
	public void setProxyProtocol(String proxyProtocol) {
		this.proxyProtocol = proxyProtocol;
	}
	public String getApplyProxy() {
		return applyProxy;
	}
	public void setApplyProxy(String applyProxy) {
		this.applyProxy = applyProxy;
	}
	public String getMainProxyPort() {
		return mainProxyPort;
	}
	public void setMainProxyPort(String mainProxyPort) {
		this.mainProxyPort = mainProxyPort;
	}
	public String getMainProxyHost() {
		return mainProxyHost;
	}
	public void setMainProxyHost(String mainProxyHost) {
		this.mainProxyHost = mainProxyHost;
	}
}
