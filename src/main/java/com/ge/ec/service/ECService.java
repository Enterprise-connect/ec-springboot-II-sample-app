/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.service;

public interface ECService {
	void startupECClient();

	boolean isECStarted();

	int killEC();
}
