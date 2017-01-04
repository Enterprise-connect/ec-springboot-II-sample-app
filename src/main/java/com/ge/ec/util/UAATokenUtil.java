/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
@Component
public class UAATokenUtil {
	/*To Get Token*/
	@Value("${security.oauth2.client.accessTokenUri}")
    private String tokenUrl;
    @Value("${security.oauth2.client.clientId}")
    private String clientId;
    @Value("${security.oauth2.client.clientSecret}")
    private String clientSecret;
    
    @Bean
	@Primary
	public OAuth2RestTemplate oauthRestTemplate() {
		ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setAccessTokenUri(tokenUrl);
		resourceDetails.setClientId(clientId);
		resourceDetails.setClientSecret(clientSecret);
		DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
		return restTemplate;
	}	
}
