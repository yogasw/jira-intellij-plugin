package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSpentEditor extends TextFieldEditor {

    private static final Pattern TIME_SPENT_SIMPLE_PATTERN = Pattern.compile("(\\d+)([wdhm])");
    private static final Pattern TIME_SPENT_MULTI_PATTERN = Pattern.compile("(\\d+[wdhm])(\\s{1}\\d+[wdhm])*");

    public TimeSpentEditor(String issueKey, String fieldValue) {
        super("Time Spent", fieldValue, issueKey, true);
    }

    @Override
    public JsonElement getJsonValue() {
        int timeSpentInSeconds = 0;
        for(String ts : myTextField.getText().split(" ")){
            Matcher matcher = TIME_SPENT_SIMPLE_PATTERN.matcher(ts);
            if(matcher.find()){
                Integer number = Integer.parseInt(matcher.group(1));
                String letter = matcher.group(2);

                switch (letter){
                    case "w": timeSpentInSeconds += number * 144000; break;
                    case "d": timeSpentInSeconds += number * 28800; break;
                    case "h": timeSpentInSeconds += number * 3600; break;
                    case "m": timeSpentInSeconds += number * 60; break;
                }
            }
        }

        return JiraGsonUtil.createPrimitive(timeSpentInSeconds);
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        ValidationInfo info = super.validate();
        if(Objects.isNull(info)){
            String timeSpent = myTextField.getText();
            if(!TIME_SPENT_MULTI_PATTERN.matcher(timeSpent).matches()){
                return new ValidationInfo("Invalid time duration entered", myTextField);
            }
        }

        return info;
    }
}
