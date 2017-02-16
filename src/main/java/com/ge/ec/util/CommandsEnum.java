/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.util;

public enum CommandsEnum {
	CLIENT("$command -oa2 \"$oa2\" -hst \"$hst\" -csc \"$csc\" -cid \"$cid\" -aid \"$aid\" -tid \"$tid\" -mod \"client\" -lpt \"$lpt\" -pxy \"$pxy\" -hca $healthcheckport -dur $tokenRefreshDur -dbg"), 
	SERVER("$command -oa2 \"$oa2\" -hst \"$hst\" -csc \"$csc\" -cid \"$cid\" -aid \"$aid\" -mod \"server\" -lpt \"$lpt\" -pxy \"$pxy\" -hca $healthcheckport -dur $tokenRefreshDur -dbg");
	private String command;

	CommandsEnum(String command) {
		this.setCommand(command);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public static CommandsEnum retreiveCommand(String agentType){
		if(agentType.equalsIgnoreCase("SERVER"))
			return SERVER;
		else
			return CLIENT;

	}

}
