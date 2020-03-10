package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;

import javax.swing.*;
import java.awt.*;

public class EditText extends View<ViewAttribute, JTextField> {
    public EditText(View parent) {
        super(parent);
        component = new JTextField();
        component.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        component.setText(attribute.getText());
        component.setForeground(Color.decode(attribute.getTextColor()));
        remeasureSize();
        component.setSize(attribute.getWidth(), attribute.getHeight());
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

}
