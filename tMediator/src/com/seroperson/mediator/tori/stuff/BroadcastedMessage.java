package com.seroperson.mediator.tori.stuff;

import java.util.Date;

public class BroadcastedMessage {
    public Date post_time;
    public String message;
    public String username;
    
    @Override
    public String toString() {
        return  post_time.toString() + ": " + message + " by " + username;
    }
}
