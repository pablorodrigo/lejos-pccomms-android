package org.raesch.java.lpcca.service;

interface Navigator {
	void forward();
	void left();
	void right();
	void stop();
	void backward();
	boolean connected();
}