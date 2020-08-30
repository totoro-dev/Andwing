package top.totoro.swing.widget.view;

import org.dom4j.Attribute;
import org.dom4j.Element;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.util.AttributeKey;

import java.awt.*;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeDefaultValue.*;

public class ImageView extends ImageButton {

    public static final String FIT_CENTER = scaleFitCenter;
    public static final String CENTER = scaleCenter;
    public static final String FIT_XY = scaleFitXY;
    public static final String FIT_START = scaleFitStart;
    public static final String FIT_END = scaleFitEnd;

    public ImageView(View parent) {
        super(parent);
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        // 获取设置的缩放类型
        Element element = attribute.getElement();
        Attribute scaleType = element.attribute(AttributeKey.scaleType);
        if (scaleType != null) {
            this.scaleType = scaleType.getValue();
        }
    }

    public void setScaleType(String scaleType) {
        if (Objects.equals(this.scaleType, scaleType)) return;
        this.scaleType = scaleType;
        // 触发图片的刷新
        setImage(getAttribute().getSrc());
    }

    @Override
    protected void fitXY() {
        if (getAttribute().getWidth() != BaseAttribute.WRAP_CONTENT) {
            width = component.getWidth();
        }
        if (getAttribute().getHeight() != BaseAttribute.WRAP_CONTENT) {
            height = component.getHeight();
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
        mImageContainer.setSize(width, height);
    }

}
