package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;

import javax.swing.*;

public class Span extends View<ViewAttribute, JPanel> {
    public Span(View parent) {
        super(parent);
        component = new JPanel();
    }
}
