package com.intellij.jira.settings.branch;

import com.intellij.jira.util.Separator;
import com.intellij.util.xmlb.annotations.OptionTag;

import java.util.List;

public class BranchSettingsState {

    private List<String> myBranchTypes;
    private NameConfig myNameConfig;

    private BranchSettingsState(List<String> branchTypes, NameConfig nameConfig) {
        myBranchTypes = branchTypes;
        myNameConfig = nameConfig;
    }

    public static BranchSettingsState getDefault(List<String> branchTypes) {
        return new BranchSettingsState(branchTypes, new NameConfig(false, Separator.HYPHEN, List.of("key")));
    }
    public static BranchSettingsState getCustom(List<String> branchTypes, Separator separator, List<String> fieldNames) {
       return new BranchSettingsState(branchTypes, new NameConfig(true, separator, fieldNames));
    }

    @OptionTag
    public List<String> getBranchTypes() {
        return myBranchTypes;
    }

    @OptionTag("separator")
    public Separator getFieldSeparator() {
        return myNameConfig.getFieldSeparator();
    }

    @OptionTag
    public List<String> getFieldNames() {
        return myNameConfig.getFieldNames();
    }

    @OptionTag("custom")
    public boolean isCustom() {
        return myNameConfig.isCustom();
    }


    private static class NameConfig {

        private boolean isCustom;

        private Separator fieldSeparator;

        private List<String> fieldNames;

        public NameConfig(boolean isCustom, Separator fieldSeparator, List<String> fieldNames) {
            this.isCustom = isCustom;
            this.fieldSeparator = fieldSeparator;
            this.fieldNames = fieldNames;
        }

        public boolean isCustom() {
            return isCustom;
        }

        public Separator getFieldSeparator() {
            return fieldSeparator;
        }

        public List<String> getFieldNames() {
            return fieldNames;
        }
    }

}
