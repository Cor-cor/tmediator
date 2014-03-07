package com.seroperson.mediator;

import java.awt.Frame;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.JFrame;

import com.seroperson.mediator.tori.stuff.Global;

public class MediatorDesktop extends Mediator {
	
	private final JFrame frame;
	private final TrayIcon tray;
	
	protected MediatorDesktop(InitializationListener initialization, JFrame frame, TrayIcon tray) { 
		super(initialization);
		this.frame = frame;
		this.tray = tray;
	}
	
	@Override
	public void unMinimize() { 
		super.unMinimize();
		frame.setVisible(true);
		frame.setExtendedState(Frame.NORMAL);
	}
	
	@Override
	public void minimize() {
		super.minimize();
		frame.setExtendedState(Frame.ICONIFIED);
	}
	
	@Override
	public void handleNetThrow(final Throwable t) {
		tray.displayMessage("Connection error", t.getMessage(), MessageType.ERROR);
	}
	
	@Override
	public void handleThrow(final Throwable t) { 
		super.handleThrow(t);
		getTimer().cancel();
		frame.dispose();
		try {
			new MediatorCrashReporter(t);
		}
		catch (Throwable e) {	}
	}
	
	@Override
	public void handleGlobal(Global g) { 
		tray.displayMessage(new StringBuilder("tMediator | Ingame broadcast by ").append(g.getPlayer()).toString(), g.getMessage(), MessageType.INFO);
	}

}
