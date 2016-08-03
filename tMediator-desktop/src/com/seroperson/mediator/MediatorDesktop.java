package com.seroperson.mediator;

import java.awt.Frame;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.seroperson.mediator.tori.stuff.Global;
import com.seroperson.mediator.tori.stuff.Server;
import com.seroperson.mediator.utils.Connector;

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
		final Server server = getServerByRoom(g.getServer());
		if (server != null)
		{
			tray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Connector.connectToServer(server);
					}
					catch (Exception ex) {
						System.err.println(ex.getMessage());
					}
				}
			});
			tray.displayMessage(new StringBuilder("tMediator | Ingame broadcast by ").append(g.getPlayer()).toString(), g.getMessage(), MessageType.INFO);	
		}
	}

}
