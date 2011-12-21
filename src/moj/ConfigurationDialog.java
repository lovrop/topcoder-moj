package moj;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

class ConfigurationDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 6205878572134421087L;

    private Preferences pref;

    private ButtonGroup compilerButtonGroup = new ButtonGroup();
    private JRadioButton gccRadioButton     = new JRadioButton("GCC");
    private JRadioButton vcRadioButton      = new JRadioButton("Visual C++");

    private JTextField placeholdersTextField = new JTextField();

    private JCheckBox switchCheckBox        = new JCheckBox();
    private JCheckBox javaSupportCheckBox   = new JCheckBox();

    private JButton saveButton		        = new JButton("Save");
    private JButton closeButton		        = new JButton("Close");

    private WindowHandler windowHandler		= new WindowHandler();

    public ConfigurationDialog(Preferences pref) {

        super((JFrame)null, "moj configuration", true);

        this.pref = pref;
        setSize(new Dimension(600, 400));

        // Configure the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setForeground(Common.FG_COLOR);
        contentPane.setBackground(Common.WPB_COLOR);

        // Target compiler		
        JLabel compilerLabel = new JLabel("Target compiler:");
        compilerLabel.setForeground(Common.FG_COLOR);
        compilerLabel.setBackground(Common.WPB_COLOR);
        compilerLabel.setToolTipText("Select the compiler you will be using. moj needs to make adjustments to the testing code for it to compile under Visual C++.");

        compilerButtonGroup.add(gccRadioButton);
        compilerButtonGroup.add(vcRadioButton);
        if (pref.getTargetCompiler().equals(Preferences.TARGETCOMPILER_GCC)) {
            gccRadioButton.setSelected(true);
        } else {
            vcRadioButton.setSelected(true);
        }

        gccRadioButton.setForeground(Common.FG_COLOR);
        gccRadioButton.setBackground(Common.WPB_COLOR);
        gccRadioButton.setText("<html><body><font color=\"#ffffff\">" + gccRadioButton.getText() + "</font></body></html>");
        vcRadioButton.setForeground(Common.FG_COLOR);
        vcRadioButton.setBackground(Common.WPB_COLOR);
        vcRadioButton.setText("<html><body><font color=\"#ffffff\">" + vcRadioButton.getText() + "</font></body></html>");

        // Test case placeholders
        JLabel placeholdersLabel = new JLabel("Test case placeholders:");
        placeholdersLabel.setForeground(Common.FG_COLOR);
        placeholdersLabel.setBackground(Common.WPB_COLOR);
        placeholdersLabel.setToolTipText("Set the number of empty test case placeholders to be generated. You can use these to enter your own test cases while solving the problem.");
        placeholdersTextField.setText("" + pref.getNumPlaceholders());

        // Language-switch workaround checkbox
        switchCheckBox.setText("Enable workaround for FileEdit language switching issue");
        switchCheckBox.setForeground(Common.FG_COLOR);
        switchCheckBox.setBackground(Common.WPB_COLOR);
        switchCheckBox.setToolTipText("If checked, moj will try to allow you to switch between languages mid-contest.");
        switchCheckBox.setSelected(pref.getLanguageSwitchWorkaround());

        // Java support checkbox
        javaSupportCheckBox.setText("Enable Java test code generation");
        javaSupportCheckBox.setForeground(Common.FG_COLOR);
        javaSupportCheckBox.setBackground(Common.WPB_COLOR);
        javaSupportCheckBox.setToolTipText("Uncheck if you want to use a different plug-in to generate Java test code.");
        javaSupportCheckBox.setSelected(pref.getEnableJavaSupport());

        contentPane.add(compilerLabel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(gccRadioButton, new GridBagConstraints(1,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(vcRadioButton, new GridBagConstraints(2,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(placeholdersLabel, new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(placeholdersTextField, new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(switchCheckBox, new GridBagConstraints(0,2,3,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(javaSupportCheckBox, new GridBagConstraints(0,3,3,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,4,4),0,0));
        contentPane.add(saveButton, new GridBagConstraints(1,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,10,10,10),0,0));
        contentPane.add(closeButton, new GridBagConstraints(2,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,0,10,10),0,0));

        // Add listeners
        saveButton.addActionListener(this);
        closeButton.addActionListener(this);

        // Set the close operations
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		
        addWindowListener(windowHandler);

        this.pack();
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == saveButton) {
            save();
        } else if (src == closeButton) {
            windowHandler.windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public boolean save() {
        // Parse
        int numPlaceholders;
        try {
            numPlaceholders = Integer.valueOf(placeholdersTextField.getText());
        } catch(NumberFormatException e) {
            Common.showError("Parse error", "\"" + placeholdersTextField.getText() + "\" is not a valid integer.", null);
            return false;
        }

        if (numPlaceholders < 0 || numPlaceholders > 50) {
            Common.showError("Parse error", "The number of placeholders must be between 0 and 50.", null);
            return false;
        }

        // Write out the preferences
        try {
            pref.setTargetCompiler(gccRadioButton.isSelected() ? Preferences.TARGETCOMPILER_GCC : Preferences.TARGETCOMPILER_VC);
            pref.setNumPlaceholders(numPlaceholders);
            pref.save();
            Common.showMessage("Save", "Preferences were saved successfully", null);
            return true;
        } catch (IOException e) {
            Common.showError("Error saving preferences", e.toString(), null);
            return false;
        }

    }

    private class WindowHandler extends WindowAdapter {
        public void windowClosing(WindowEvent e) {

            // Find out if anything has save's pending
            boolean savePending=false;

            savePending =
                    gccRadioButton.isSelected() != pref.getTargetCompiler().equals(Preferences.TARGETCOMPILER_GCC)
                    ||	!placeholdersTextField.getText().equals("" + pref.getNumPlaceholders())
                    ||  switchCheckBox.isSelected() != pref.getLanguageSwitchWorkaround()
                    ||  javaSupportCheckBox.isSelected() != pref.getEnableJavaSupport();

            // If so...
            if (savePending) {

                // Should we save?
                if (Common.confirm("Save Pending", "Changes are pending.  Do you want to save before closing?", null)) {
                    // Try to save
                    if (!save()) return;
                }
            }
            // Close the window
            dispose();
        }
    }

    public static void main(String[] args) {
        new ConfigurationDialog(null).setVisible(true);
    }
}
