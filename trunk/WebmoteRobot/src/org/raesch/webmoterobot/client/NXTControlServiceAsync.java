package org.raesch.webmoterobot.client;

import org.raesch.webmoterobot.shared.Action;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface NXTControlServiceAsync {

	void runAction(Action action, AsyncCallback<Void> callback);
}
