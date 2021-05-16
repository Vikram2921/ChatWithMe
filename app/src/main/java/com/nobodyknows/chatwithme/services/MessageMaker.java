package com.nobodyknows.chatwithme.services;

import java.util.Date;
import java.util.UUID;

public class MessageMaker {

    public static String createMessageId(String myid) {
        String id = myid+""+new Date().getTime();
        return id;
    }
}
