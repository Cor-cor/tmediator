package com.seroperson.mediator.utils;

import com.seroperson.mediator.tori.stuff.Global;

public interface CaseListener {

	public void unMinimize();

	public void minimize();

	public void handleGlobal(Global g);

}
