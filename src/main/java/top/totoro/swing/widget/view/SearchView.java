package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.util.PaintUtil;

import javax.swing.*;
import java.awt.*;

public class SearchView extends LinearLayout {
    private ImageView searchIcon;
    private EditText input;
    private ImageView deleteIcon;
    private Color bg = Color.gray;

    public SearchView(View<?, ?> parent) {
        super(parent);
        component = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                PaintUtil.drawButtonRadius(g, bg, 16, getWidth(), getHeight());
            }
        };
        component.setLayout(null);
    }

    @Override
    public void setAttribute(LayoutAttribute attribute) {
        super.setAttribute(attribute);
    }
}
