package com.ge.ec.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class Properties {
	@Value("${applyProxy}")
	private String applyProxy;
	@Value("${proxyUrl}")
	private String proxyUrl;

	public String getProxyUrl() {
		return proxyUrl;
	}
	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}
	public String getApplyProxy() {
		return applyProxy;
	}
	public void setApplyProxy(String applyProxy) {
		this.applyProxy = applyProxy;
	}
}
