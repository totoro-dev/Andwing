package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;

import javax.swing.*;
import java.awt.*;

public class TextView extends View<ViewAttribute, JLabel> {

    public TextView(View parent) {
        super(parent);
        component = new JLabel("", JLabel.CENTER);
    }

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
        // minHeight = size + size / 5 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 0, minHeight = size + size / 5;
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

    @Override
    public void invalidate() {
        super.invalidate();
        remeasureSize();
    }

    /**
     * 设置文本内容
     *
     * @param text 文本
     */
    public void setText(String text) {
        attribute.setText(text);
        component.setText(text);
        invalidateSuper();
    }

    /**
     * 设置字体大小
     *
     * @param size 字体大小
     */
    public void setTextSize(int size) {
        if (attribute.getTextSize() == size) return;
        attribute.setTextSize(size);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        invalidateSuper();
    }

    /**
     * 设置字体颜色
     *
     * @param color 字体颜色
     */
    public void setTextColor(Color color) {
        attribute.setTextColor(color.toString());
        component.setForeground(color);
    }

    /**
     * 设置字体
     *
     * @param style SERIF字体{@link top.totoro.swing.widget.util.AttributeDefaultValue#SERIF}
     *              SANS_SERIF字体{@link top.totoro.swing.widget.util.AttributeDefaultValue#SANS_SERIF}
     *              DIALOG字体{@link top.totoro.swing.widget.util.AttributeDefaultValue#DIALOG}
     *              DIALOG_INPUT字体{@link top.totoro.swing.widget.util.AttributeDefaultValue#DIALOG_INPUT}
     *              MONOSPACED字体{@link top.totoro.swing.widget.util.AttributeDefaultValue#MONOSPACED}
     */
    public void setTextStyle(String style) {
        attribute.setTextStyle(style);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
    }

    /**
     * 设置字体的样式：正常、加粗、斜体
     *
     * @param font 正常样式{@link top.totoro.swing.widget.util.AttributeDefaultValue#PLAIN}
     *             加粗样式{@link top.totoro.swing.widget.util.AttributeDefaultValue#BOLD}
     *             斜体样式{@link top.totoro.swing.widget.util.AttributeDefaultValue#ITALIC}
     */
    public void setTextFont(String font) {
        attribute.setTextFont(font);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
    }

}
