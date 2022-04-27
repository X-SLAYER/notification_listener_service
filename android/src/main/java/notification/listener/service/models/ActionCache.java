package notification.listener.service.models;

import java.util.HashMap;

abstract public class ActionCache {
    public static HashMap<Integer, Action> cachedNotifications = new HashMap<>();
}
