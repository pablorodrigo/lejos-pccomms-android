package org.raesch.java.lpcca.service;
import org.raesch.java.lpcca.service.Navigator;

interface InterfaceLPCCARemoteService {
	void requestConnectionToNXT();
	Navigator get();
}