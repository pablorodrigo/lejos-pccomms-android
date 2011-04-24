package org.raesch.java.lpcca.service;
import org.raesch.java.lpcca.service.Navigator;

interface InterfaceLPCCARemoteService {
	void requestConnectionToNXT();
	void establishBTConnection(String deviceKey, String deviceMac);
	Navigator get();
}