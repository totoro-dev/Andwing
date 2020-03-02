package top.totoro.swing.widget.view;

import top.totoro.swing.widget.util.SwingConstants;
import top.totoro.swing.widget.util.PaintUtil;

import javax.swing.*;
import java.awt.*;

public class ToastContent extends JComponent {

    private String text = "";
    private int x,y;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ToastContent(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
//        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
//        super.paint(g);
//        PaintUtil.drawBorderFifteenRadius(g, Color.decode("#b0b0b0"), Color.decode("#b0b0b0"),x,y, getWidth(),getHeight());
        PaintUtil.drawBorderFiveRadius(g, Color.decode("#b0b0b0"), getWidth());
        g.setColor(Color.WHITE);
        g.setFont(SwingConstants.TOAST_FONT);
        g.drawString(text, 15, 20);
    }

}
