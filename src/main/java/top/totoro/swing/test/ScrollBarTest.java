package top.totoro.swing.test;

import top.totoro.swing.widget.base.BaseScrollBar;

import java.awt.*;

public class ScrollBarTest extends BaseScrollBar.Vertical {
    //    private int height = 10;
    @Override
    public void setWidth(int width) {

    }

    @Override
    public int getWidth() {
        return 10;
    }

//    @Override
//    public void setHeight(int height) {
//
//    }
//
//    @Override
//    public int getHeight() {
//        return height;
//    }

    @Override
    protected void drawBarHeadAndTail(Graphics graphics) {
//        drawLine(Color.BLACK,barX,0,getBarWidth(),getHeight());
        drawLine(Color.BLACK,0,getBarY(),getWidth(),getBarHeight());
    }

    @Override
    protected void drawMiddle(Graphics graphics) {

    }
}
