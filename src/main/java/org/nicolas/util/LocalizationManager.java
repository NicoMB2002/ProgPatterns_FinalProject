package org.nicolas.util;

import java.util.ResourceBundle;

public class LocalizationManager {
    private static ResourceBundle messages;

    public static void setMessages(ResourceBundle bundle) {
        messages = bundle;
    }

    public static String getMessage(String key) {
        if (messages == null) {
            return "Missing resource bundle";
        }
        return messages.getString(key);
    }
}
