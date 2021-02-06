package com.intellij.jira.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.components.ServiceManager;

import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;

public class JiraNotificationManager {

    private static final String BALLON_NOTIFICATION_GROUP_NAME = "Jira Balloon Notifications";
    private static final String STICKY_BALLON_NOTIFICATION_GROUP_NAME = "Jira Sticky Balloon Notifications";

    public static JiraNotificationManager getInstance(){
        return ServiceManager.getService(JiraNotificationManager.class);
    }

    public Notification createNotification(String title, String content){
        return getNotificationGroup(BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, null, content, INFORMATION);
    }

    public Notification createNotificationError(String title, String content){
        return getNotificationGroup(STICKY_BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, null, content, ERROR);
    }

    public Notification createSilentNotification(String title, String content){
        return getNotificationGroup(BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, null, content, INFORMATION);
    }

    private static NotificationGroup getNotificationGroup(String name) {
        return NotificationGroupManager.getInstance().getNotificationGroup(name);
    }

}
