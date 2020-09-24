package top.totoro.swing.widget.layout;

import org.dom4j.Attribute;
import org.dom4j.Element;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.util.AttributeKey;
import top.totoro.swing.widget.view.View;

public class GridLayout extends BaseLayout {

    private int column = 1; // 默认只有一列
    private int visibleSize = 0;

    public GridLayout(View parent) {
        super(parent);
    }

    @Override
    public void setAttribute(LayoutAttribute attribute) {
        super.setAttribute(attribute);
        Element element = attribute.getElement();
        Attribute columnAttr = element.attribute(AttributeKey.column);
        if (columnAttr != null) {
            column = Integer.parseInt(columnAttr.getValue());
        }
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * 获取当前GridLayout布局中真实可见的子控件数量。
     * 可以用于测量网格的显示数量。
     *
     * @return 可见控件数量
     */
    public int getVisibleSize() {
        visibleSize = 0;
        for (View<?, ?> sonView : getSonViews()) {
            if (sonView.getVisible()) visibleSize++;
        }
        return visibleSize;
    }

}
