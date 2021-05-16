package com.nobodyknows.chatwithme.services;

import android.content.Context;

public class MessageHelper {
    private static String key;
    private static String plainText;
    private static Context context;
    private static MessageHelper messageHelper;

    public static MessageHelper with(MessageHelper messageHelperInstance,Context applicationContext) {
       context = applicationContext;
       messageHelper = messageHelperInstance;
       return messageHelper;
    }

    public static MessageHelper setKey(String passKey) {
        key = passKey;
        return messageHelper;
    }

    public static MessageHelper setText(String text) {
        plainText = text;
        return messageHelper;
    }

    public static String encrypt() {
        String cipherText = "";
        return cipherText;
    }

    public static String decrypt() {
        String plainText = "";
        return plainText;
    }
}
