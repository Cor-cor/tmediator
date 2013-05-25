package com.seroperson.mediator;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class MediatorDesktop extends JFrame {
	
	public static final String title = "tMediator";
	public static final int strokeLineWidth = 2;
	private static final long serialVersionUID = 8617963261285685374L;
	private final Mediator mediator;
	
	public MediatorDesktop() { 
		super(title);
		int scale = 2;
		int w = 360/scale;
		int h = 360/scale;
		
		final TrayIcon tray; 
	    if(SystemTray.isSupported())
        	tray = initTray();
        else
        	tray = null;
		
		mediator = new com.seroperson.mediator.Mediator(new CaseListener() {
			@Override
			public void minimize(Mediator m) {
				setExtendedState(Frame.ICONIFIED);
			}
			
			@Override
			public void handleThrow(Throwable t) { 
				tray.displayMessage("ERROR :C", t.getMessage() == null ? "unknown error" : t.getMessage(), MessageType.ERROR);
			}
		}, scale, strokeLineWidth);		
		 
        try {
			setIconImage(ImageIO.read(getClass().getResource("/skin/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    setExtendedState(Frame.NORMAL);
	    setUndecorated(true);
	    
	    Container c = getContentPane();
		LwjglAWTCanvas canvas = new LwjglAWTCanvas(mediator, false);
		
		c.add(canvas.getCanvas());
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(getWindowListener(tray));
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(w, h);
		setLocation(d.width-w, d.height-h-getTaskBarHeight()+strokeLineWidth);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
				
	}
	
	private int getTaskBarHeight() { 
		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		return winSize.y > 0 ? /*-(scrnSize.height - winSize.height)*/0 : scrnSize.height - winSize.height;
	}
	
	private WindowAdapter getWindowListener(final TrayIcon trayIcon) { 
		return new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) { 
				trayIcon.getImage().flush();
				SystemTray.getSystemTray().remove(trayIcon);
				mediator.dispose();
			}
			
            @Override
            public void windowStateChanged(WindowEvent e) {
            	switch(e.getNewState()) { 
					case(NORMAL):
				    	setVisible(true);
						break;
				}
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
 				setVisible(false);
            }
            
        };
	}
	
	private TrayIcon initTray() { 
        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/skin/icon.png"));
        
        PopupMenu popup = new PopupMenu();     
        
        final MenuItem open = new MenuItem("Open");
        open.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                setVisible(true);
                setExtendedState(Frame.NORMAL);
            }
        });
        
        final MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                new com.seroperson.mediator.settings.SettingsSaver(Mediator.getSettings());
            }
        });
                        
        final CheckboxMenuItem ontop = new CheckboxMenuItem("Over all windows", true);
        ontop.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(isAlwaysOnTop())
					setAlwaysOnTop(false);
				else
					setAlwaysOnTop(true);
			}	
        });
                       
        final MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                Gdx.app.exit();
            }
        });
        
        popup.add(open);
        popup.add(settings);
        if(isAlwaysOnTopSupported())
        	popup.add(ontop);
        popup.add(exit);
        
        TrayIcon trayIcon = new TrayIcon(image, title, popup);
        trayIcon.setImageAutoSize(true);
        try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
        
        return trayIcon;
	}
	
	public static void main(String[] a) { 
		new MediatorDesktop();
	}
	
}
