# Enterprise Connect 
Enterprise-Connect Springboot Example II.

  - If you want to set up a proxy for the java application, open src/main/java/resources/application.properties and change the following: 
    > ``applyProxy=<boolean-flag>`` (true/false)
    
    >``proxyUrl=<proxy-url>`` (eg. http://proxy-src.reasearch.ge.com:80 )
    
  - All EC Related parameters are in application.properties. 
  - EC Agent will automatically start itself in "client" if you want to switch to server mode then change application.properties.
  - Please check the console for EC Agent Logs. 
  - All other APIs you can find in the controller. 
  - If you want to test the Mail API then please modify the username/password in com.ge.ec.util.MailSender and the to & from mail addresses in Controller.
  
# Please Note:
	- Postgres and MS SQL Server have been tested, Oracle DB Connection is still WIP.   
	- FTP is a WIP. 
	

# Maven Proxy Settings
> Generate an API key.

- Log in to `https://artifactory.predix.io`.
- Use your predix.io account user name and password to login.
- In the upper right of the screen, click your user name.
- Enter your predix.io password.
- Click Unlock to populate the API Key field.
- The API key values are masked.
- Copy the key.

> In the <user directory>.m2/settings.xml file:

- Configure the proxy settings for Maven based on your own site needs. Ask your network administrator if you have questions about your requirements.
- Add a server entry with the following information. (You will use the API key that you copied in Step 1.)

          <server>
                <id>artifactory.external</id>
                <username>{your predix cloud login}</username>
                <password>{encrypted password - API key}</password>
          </server>

>  You may also have to set proxy settings for the HTTPS protocol.

       <proxy>
          <id>optional</id>
          <active>true</active>
          <protocol>http</protocol>
          <username>proxyuser</username>
          <password>proxypass</password>
          <host>proxy.host.com</host>
          <port>80</port>
          <nonProxyHosts>*.host.com|localhost</nonProxyHosts>
        </proxy>

