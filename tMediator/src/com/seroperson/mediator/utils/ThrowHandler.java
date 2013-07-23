package com.seroperson.mediator.utils;

public interface ThrowHandler {

	public void handleThrow(Throwable t);

	public void handleNetThrow(Throwable t);
	
}
