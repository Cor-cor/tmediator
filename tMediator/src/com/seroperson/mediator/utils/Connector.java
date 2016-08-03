package com.seroperson.mediator.utils;

import com.seroperson.mediator.tori.stuff.Server;

import java.awt.*;
import java.net.URI;

public class Connector {
    public  static void connectToServer(Server server)
    {
        final String room = server.getRoom();
        if (!room.equals("unknown room")) {
            try {
                final String uri = "steam://run/248570//+connect%20join%20" + room;
                System.out.println(uri);
                Desktop.getDesktop().browse(new URI(uri));
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
