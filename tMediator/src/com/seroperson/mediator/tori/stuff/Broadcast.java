package com.seroperson.mediator.tori.stuff;

//http://forum.toribash.com/tori_broadcast.php?format=json&explain
public class Broadcast {
    public BroadcastedMessage[] broadcasts;

    @Override
    public String toString() {
        if (broadcasts != null && broadcasts[0] != null)
            return broadcasts[0].toString();
        else 
            return "No broadcast";
    }
}
