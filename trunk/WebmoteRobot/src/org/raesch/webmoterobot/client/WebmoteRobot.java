package org.raesch.webmoterobot.client;

import org.raesch.webmoterobot.shared.Action;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebmoteRobot implements EntryPoint {
	private final NXTControlServiceAsync nxtControlService = GWT
			.create(NXTControlService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		VerticalPanel mainVerticalPanel = new VerticalPanel();
		HorizontalPanel firstRowPanel = new HorizontalPanel();
		HorizontalPanel secondRowPanel = new HorizontalPanel();
		HorizontalPanel thirdRowPanel = new HorizontalPanel();

		mainVerticalPanel.setWidth("100%");
		mainVerticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		firstRowPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		secondRowPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		thirdRowPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		mainVerticalPanel.add(firstRowPanel);
		mainVerticalPanel.add(secondRowPanel);
		mainVerticalPanel.add(thirdRowPanel);

		Button forwardButton = new Button("Forward", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nxtControlService.runAction(Action.FORWARD, null);
			}
		});
		Button leftButton = new Button("Left", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nxtControlService.runAction(Action.LEFT, null);
			}
		});
		Button backwardButton = new Button("Backward", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nxtControlService.runAction(Action.BACKWARD, null);
			}
		});
		Button rightButton = new Button("Right", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nxtControlService.runAction(Action.RIGHT, null);
			}
		});

		Button requestButton = new Button("Right", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nxtControlService.runAction(Action.REQUEST, null);
			}
		});

		forwardButton.setPixelSize(100, 100);
		backwardButton.setPixelSize(100, 100);
		leftButton.setPixelSize(100, 100);
		rightButton.setPixelSize(100, 100);

		firstRowPanel.add(forwardButton);
		secondRowPanel.add(leftButton);
		secondRowPanel.add(backwardButton);
		secondRowPanel.add(rightButton);
		thirdRowPanel.add(requestButton);

		RootPanel.get().add(mainVerticalPanel);
	}
}
