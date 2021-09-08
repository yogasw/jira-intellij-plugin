package com.intellij.jira.util;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JiraIconUtil {

    private static final Map<String, Icon> iconsCache = new ConcurrentHashMap<>();

    private JiraIconUtil() { }

    public static Icon getIcon(@Nullable String iconUrl){
        if(StringUtil.isEmpty(iconUrl)){
            return null;
        }

        Icon icon = iconsCache.get(iconUrl);
        if (Objects.nonNull(icon)) {
            return icon;
        }

        ImageIcon _icon = null;
        try {
            _icon = new ImageIcon(new URL(iconUrl));
            if (_icon.getImageLoadStatus() == 8) {
                iconsCache.put(iconUrl, _icon);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return _icon;
    }

}
