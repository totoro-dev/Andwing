package top.totoro.swing.widget.view;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.util.Log;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static top.totoro.swing.widget.util.AttributeDefaultValue.*;

public class ImageButton extends View<ViewAttribute, JPanel> {

    private final String TAG = getClass().getSimpleName();

    protected final JLabel mImageContainer;
    protected ImageIcon imageIcon;
    protected int width = -3, height = -3;
    protected String scaleType = scaleFitCenter;

    public ImageButton(View parent) {
        super(parent);
        component = new JPanel(null);
        mImageContainer = new JLabel("", JLabel.CENTER) {
            @Override
            public void paint(Graphics g) {
                switch (scaleType) {
                    default:
                    case scaleFitCenter:
                        fitCenter();
                    case scaleCenter:
                        // 居中显示
                        mImageContainer.setLocation((component.getWidth() - width) / 2, (component.getHeight() - height) / 2);
                        break;
                    case scaleFitXY:
                        fitXY();
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitStart:
                        fitCenter();
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitEnd:
                        fitCenter();
                        mImageContainer.setLocation(component.getWidth() - width, component.getHeight() - height);
                        break;
                }
                super.paint(g);
            }
        };
        component.add(mImageContainer);
    }

    private void fitCenter() {
        if (attribute.getWidth() != BaseAttribute.WRAP_CONTENT
                || attribute.getHeight() != BaseAttribute.WRAP_CONTENT) {
            float widthScale = component.getWidth() / (float) width;
            float heightScale = component.getHeight() / (float) height;
            float scale = Math.min(widthScale, heightScale);
            if ((widthScale < 1 || heightScale < 1) && scale != 1) {
                Log.d(TAG, "scale = " + scale);
                width *= scale;
                height *= scale;
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
                mImageContainer.setSize(width, height);
            }
        }
    }

    protected void fitXY() {
    }

    protected void fitStart() {
    }

    public void setImage(String src, boolean invalidate) {
        if (src == null || "".equals(src)) {
            Log.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不能为空");
            return;
        }
        URL url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            attribute.setSrc(src);
            imageIcon = new ImageIcon(url);
        } else {
            Log.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不正确");
            imageIcon = null;
        }
        mImageContainer.setIcon(imageIcon);
        if (invalidate) {
            invalidateSuper();
        } else {
            reSizeAsImageSize();
        }
    }

    public void setImage(String src) {
        setImage(src, true);
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        // 背景透明
        component.setOpaque(false);
        setImage(attribute.getSrc(), false);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reSizeAsImageSize();
    }

    /**
     * 根据宽高属性的wrap或确定的值，重置图片大小
     * 该方法一般在开始整体布局大小确定前调用
     */
    private void reSizeAsImageSize() {
        if (imageIcon == null) {
            width = height = 0;
        } else {
            width = imageIcon.getIconWidth();
            height = imageIcon.getIconHeight();
        }
        if (attribute.getWidth() == BaseAttribute.WRAP_CONTENT) {
            setMinWidth(width);
        }
        if (attribute.getHeight() == BaseAttribute.WRAP_CONTENT) {
            setMinHeight(height);
        }
        mImageContainer.setSize(width, height);
        // 具体的位置只有在绘制的时候才能确定
//        mImageContainer.setLocation((component.getWidth() - width) / 2, (component.getHeight() - height) / 2);
    }

}
