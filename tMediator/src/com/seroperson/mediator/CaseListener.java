package com.seroperson.mediator;

public interface CaseListener {

	public void minimize(Mediator mediator);

	public void handleThrow(Throwable t);

}
