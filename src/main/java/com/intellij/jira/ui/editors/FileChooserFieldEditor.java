package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.io.File;

public class FileChooserFieldEditor extends AbstractFieldEditor<String> {

    private File mySelectedFile;

    private JTextField myTextField;
    private JPanel myPanel;
    private JButton myButton;

    public FileChooserFieldEditor() {
        this("File", true);
    }

    protected FileChooserFieldEditor(String fieldName, boolean required) {
        super(fieldName, null, required);
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        myButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(myPanel);
            if (JFileChooser.APPROVE_OPTION == result) {
                File selectedFile = fileChooser.getSelectedFile();
                mySelectedFile = selectedFile;
                myTextField.setText(selectedFile.getName());
                myButton.setEnabled(true);
            }
        });

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myPanel, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        return null;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && StringUtil.isEmpty(myTextField.getText())){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    public File getSelectedFile() {
        return mySelectedFile;
    }
}
