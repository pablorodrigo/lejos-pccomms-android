package org.raesch.webmoterobot.client;

import org.raesch.webmoterobot.shared.Action;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface NXTControlService extends RemoteService {
	void runAction(Action action) throws IllegalArgumentException;
}
