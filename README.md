# Enterprise Connect 
Enterprise-Connect Springboot Example II.

  - If you want to set up a proxy for the java application, you can add it in com.ge.ec.util.ApplicationInitailizer on line 25,26.
  - All EC Related parameters are in application.properties. 
  - If EC Requires proxy too then add “-pxy "$pxy"” to ec.client.command.
  - EC Agent will automatically start itself in "client" if you want to switch to server mode then change application.properties.
  - Please check the console for EC Agent Logs. 
  - All other APIs you can find in the controller. 
  - If you want to test the Mail API then please modify the username/password in com.ge.ec.util.MailSender and the to & from mail addresses in Controller.


#Please Note:
	- Postgres and MS SQL Server have been tested, Oracle DB Connection is still WIP.   
 
  