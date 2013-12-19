package com.seroperson.mediator.screen;

import com.badlogic.gdx.InputProcessor;
import com.seroperson.mediator.refresh.ServerHandler;


public abstract class MainScreen extends ScreenAdapter {

	public abstract InputProcessor getInputProcessor();
	
	public abstract ServerHandler getServerHandler();
	
}
