package moj;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

class Common {
    public static final Color FG_COLOR = Color.white;
    public static final Color BG_COLOR = Color.black;
    public static final Color WPB_COLOR = Color.decode("0x333333");
    public static final Color TF_COLOR = Color.white;
    public static final Color TB_COLOR = Color.black;
    public static final Color HF_COLOR = Color.white;
    public static final Color HB_COLOR = Color.decode("0x003300"); 
    public final static Font DEFAULTFONT = new Font("SansSerif", Font.PLAIN, 12);
    public final static Box createHorizontalBox(Component[] a) {
        return Common.createHorizontalBox(a, true);
    }
    public final static Box createHorizontalBox(Component[] a, boolean endGlue) {
        Box temp = Box.createHorizontalBox();
        if (a.length == 0)
            return temp;
        // Add all but the last one
        for (int x = 0; x < a.length - 1; x++) {
            temp.add(a[x]);
            temp.add(Box.createHorizontalStrut(5));
        }
        // Add the last one
        temp.add(a[a.length - 1]);
        if (endGlue)
            temp.add(Box.createHorizontalGlue());
        return temp;
    }

    public final static JTable createJTable() {
        JTable table = new JTable();
        table.setBackground(Common.TB_COLOR);
        table.setForeground(Common.TF_COLOR);
        table.setSelectionBackground(Common.HB_COLOR);
        table.setSelectionForeground(Common.HF_COLOR);
        table.setShowGrid(false);
        return table;
    }
    public final static JLabel createJLabel(String text) {
        return Common.createJLabel(text, DEFAULTFONT);
    }
    public final static JLabel createJLabel(String text, Font font) {
        return Common.createJLabel(text, null, SwingConstants.LEFT, font);
    }
    public final static JLabel createJLabel(String text, Dimension size) {
        return Common.createJLabel(text, size, SwingConstants.LEFT, DEFAULTFONT);
    }
    public final static JLabel createJLabel(
            String text,
            Dimension size,
            int alignment) {
        return Common.createJLabel(text, size, alignment, DEFAULTFONT);
    }
    public final static JLabel createJLabel(
            String text,
            Dimension size,
            int alignment,
            Font font) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(WPB_COLOR);
        temp.setFont(font);
        temp.setHorizontalAlignment(alignment);
        if (size != null) {
            temp.setMinimumSize(size);
            temp.setPreferredSize(size);
            temp.setMaximumSize(size);
        }
        return temp;
    }
    public final static JTextField createJTextField(int size, Dimension max) {
        return Common.createJTextField(size, max, DEFAULTFONT);
    }
    public final static JTextField createJTextField(
            int size,
            Dimension max,
            Font font) {
        JTextField temp = new JTextField(size);
        temp.setForeground(FG_COLOR);
        temp.setBackground(BG_COLOR);
        temp.setCaretColor(FG_COLOR);
        temp.setFont(font);
        temp.setBorder(BorderFactory.createLineBorder(FG_COLOR, 1));
        temp.setMaximumSize(max);
        temp.addFocusListener(new Common.SelectAll(temp));
        return temp;
    }
    public final static JScrollPane createJScrollPane(Component a) {
        return Common.createJScrollPane(a, null, null);
    }

    public final static JScrollPane createJScrollPane(Component a, Dimension size) {
        return Common.createJScrollPane(a, size, null);
    }

    public final static JScrollPane createJScrollPane(Component a, Dimension size, Border border) {

        JScrollPane temp = new JScrollPane(a);
        temp.setBackground(WPB_COLOR);
        temp.getViewport().setBackground(WPB_COLOR);
        if (size!=null) temp.getViewport().setPreferredSize(size);
        if (border!=null) temp.setBorder(border);

        return temp;
    }

    public final static void setDefaultAttributes(Container panel) {
        Common.setDefaultAttributes(panel, new BorderLayout());
    }
    public final static void setDefaultAttributes(
            Container panel,
            LayoutManager layout) {
        panel.setLayout(layout);
        panel.setBackground(WPB_COLOR);
    }
    private static class SelectAll extends FocusAdapter {
        JTextComponent parent;
        public SelectAll(JTextComponent parent) {
            this.parent = parent;
        }
        public void focusGained(FocusEvent e) {
            parent.selectAll();
        }
    }
    public final static JButton createJButton(String text) {
        return Common.createJButton(text, null, DEFAULTFONT);
    }
    public final static JButton createJButton(String text, Dimension size) {
        return Common.createJButton(text, size, DEFAULTFONT);
    }
    public final static JButton createJButton(String text, Font font) {
        return Common.createJButton(text, null, font);
    }
    public final static JButton createJButton(
            String text,
            Dimension size,
            Font font) {
        JButton temp = new JButton(text);
        temp.setFont(font);
        if (size != null) {
            temp.setMinimumSize(size);
            temp.setPreferredSize(size);
            temp.setMaximumSize(size);
        }
        return temp;
    }
    public static void showMessage(String title, String msg, Component comp) {
        JOptionPane.showMessageDialog(
                comp,
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showError(String title, String msg, Component comp) {
        JOptionPane.showMessageDialog(
                comp,
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirm(String title, String msg, Component comp) {
        int choice =
                JOptionPane.showConfirmDialog(
                        comp,
                        msg,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            return (true);
        }
        return (false);
    }
    public static String input(String title, String msg, Component comp) {
        String value =
                JOptionPane.showInputDialog(comp, msg, title, JOptionPane.QUESTION_MESSAGE);
        return (value);
    }
}
/* @(#)Common.java */