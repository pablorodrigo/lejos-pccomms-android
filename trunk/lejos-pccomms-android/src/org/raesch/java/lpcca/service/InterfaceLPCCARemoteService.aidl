package org.raesch.java.lpcca.service;
import org.raesch.java.lpcca.service.Navigator;

interface InterfaceLPCCARemoteService {
	List<String> getAvailableDevicesList();
	void requestConnectionToNXT();
	void establishBTConnection(String deviceKey);
	void startDiscovery();
	Navigator get();
}