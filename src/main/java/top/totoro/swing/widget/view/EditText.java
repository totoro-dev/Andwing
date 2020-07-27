package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.listener.OnTextChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EditText extends View<ViewAttribute, JTextArea> {

    private OnTextChangeListener onTextChangeListener;
    private String origin = "";

    public EditText(View parent) {
        super(parent);
        component = new JTextArea();
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (component.getText().equals(attribute.getHintText())) {
                    setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (component.getText().equals("")) {
                    setHint(attribute.getHintText());
                }
            }
        });
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        component.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        remeasureSize();
        component.setSize(attribute.getWidth(), attribute.getHeight());
        if (!"".equals(attribute.getHintText())) {
            setHint(attribute.getHintText());
        } else {
            setText(attribute.getText());
        }
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, attribute.getBorderColor()));
    }

    public void setHint(String hint) {
        component.setFont(new Font(attribute.getTextStyle(), Font.ITALIC, attribute.getTextSize()));
        component.setForeground(Color.decode("#ababab"));
        component.setText(hint);
        attribute.setHintText(hint);
    }

    public void setText(String text) {
        if (text == null || "".equals(text)) {
            setHint(attribute.getHintText());
        }
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        component.setForeground(Color.decode(attribute.getTextColor()));
        component.setText(text);
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    protected void remeasureSize() {
        // minHeight = size + size / 5 + 10 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 20, minHeight = size + size / 5 + 10;
        String text = attribute.getText();
        char[] chars = text.toCharArray();
        for (char c :
                chars) {
            // 根据英文、英文符号、中文中文符号来确定TextView至少要多大才能容的下
            if (Integer.valueOf(Integer.toString(c)) < 128) {
                minWidth += size / 2;
            } else {
                if (String.valueOf(c).matches("。？、“”——")) {
                    minWidth += 5 * size / 8;
                } else {
                    minWidth += size + 1; // 中文字符需要加1
                }
            }
        }
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    public void addOnTextChangeListener(OnTextChangeListener listener) {
        onTextChangeListener = listener;
        origin = component.getText();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            if (!origin.equals(component.getText())) {
                origin = component.getText();
                onTextChangeListener.onChange(origin);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

}
