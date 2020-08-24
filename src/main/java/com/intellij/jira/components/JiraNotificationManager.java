package com.intellij.jira.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.components.ServiceManager;

import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;
import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;

public class JiraNotificationManager {

    private static final NotificationGroup BALLON_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Jira Notifications");
    private static final NotificationGroup STICKY_BALLOON_NOTIFICATION_GROUP = new NotificationGroup("Jira Notifications", STICKY_BALLOON, true);


    public static JiraNotificationManager getInstance(){
        return ServiceManager.getService(JiraNotificationManager.class);
    }

    public Notification createNotification(String title, String content){
        return BALLON_NOTIFICATION_GROUP.createNotification(title, null, content, INFORMATION);
    }

    public Notification createNotificationError(String title, String content){
        return STICKY_BALLOON_NOTIFICATION_GROUP.createNotification(title, null, content, ERROR);
    }

    public Notification createSilentNotification(String title, String content){
        return BALLON_NOTIFICATION_GROUP.createNotification(title, null, content, INFORMATION);
    }

}
