/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
@Component
public class CommandCreator {
	@Autowired
	@Qualifier("oauthRestTemplate")
	OAuth2RestTemplate oauthRestTemplate;
	@Value("${security.oauth2.client.accessTokenUri}")
    private String tokenUrl;
    @Value("${security.oauth2.client.clientId}")
    private String clientId;
    @Value("${security.oauth2.client.clientSecret}")
    private String clientSecret;
    @Value("${ec.client.command}")
    private String ecCommand;
    @Value("${ec.tunnelid}")
    private String tid;
    @Value("${ec.agentid}")
    private String aid;
    @Value("${ec.mode}")
    private String mode;
    @Value("${ec.websocket}")
    private String websocket;
    @Value("${ec.proxy}")
    private String pxy;
	public String commandCreate(String environmentType){
		ecCommand = ecCommand
					.replace("$tkn", oauthRestTemplate.getAccessToken().toString().trim())
					.replace("$command",environmentType.trim())
					.replace("$oa2",tokenUrl.trim())
					.replace("$hst",websocket.trim())
					.replace("$csc",clientSecret.trim())
					.replace("$cid",clientId.trim())
					.replace("$aid",aid.trim())
					.replace("$pxy",pxy.trim())
					.replace("$tid",tid.trim())
					.replace("$mod",mode.trim());
		return ecCommand;
	}

}
