package top.totoro.swing.widget.view;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.context.Context;
import top.totoro.swing.widget.listener.InvalidateListener;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageView extends View<ViewAttribute, JLabel> implements InvalidateListener {

    private URL url;
    private ImageIcon imageIcon;
    private int width = -3, height = -3;

    public ImageView(View parent) {
        super(parent);
        component = new JLabel("", JLabel.CENTER);
        if (context != null) {
            context.requestInvalidateListener(this);
        }
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        if (context != null) {
            context.requestInvalidateListener(this);
        }
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        String src = attribute.getSrc();
        if (src == null || "".equals(src)) System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不能为空");
        url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            imageIcon = new ImageIcon(url);
            // 解决布局加载后无法显示图片的问题
            component.setIcon(imageIcon);
            reSizeAsImageSize();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reSizeAsImageSize();
    }

    public void setBackgroundImage(String src) {
        if (src == null || "".equals(src)) System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不能为空");
        url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            attribute.setSrc(src);
            imageIcon = new ImageIcon(url);
            component.setIcon(imageIcon);
            invalidateSuper();
            reSizeAsComponentSize();
        } else System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不正确");
    }

    /**
     * 根据属性的match重置置图片的大小适应View的大小
     * 该方法需要在布局大小确定后才调用，确保大小是正确的
     */
    public void reSizeAsComponentSize() {
        if (imageIcon != null) {
            if (getAttribute().getWidth() == BaseAttribute.MATCH_PARENT && getAttribute().getHeight() != BaseAttribute.MATCH_PARENT) {
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(component.getWidth(), imageIcon.getIconHeight(), Image.SCALE_FAST));
            } else if (getAttribute().getWidth() != BaseAttribute.MATCH_PARENT && getAttribute().getHeight() == BaseAttribute.MATCH_PARENT) {
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth(), component.getHeight(), Image.SCALE_FAST));
            } else if (getAttribute().getWidth() == BaseAttribute.MATCH_PARENT && getAttribute().getHeight() == BaseAttribute.MATCH_PARENT) {
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(component.getWidth(), component.getHeight(), Image.SCALE_FAST));
            }
        }
    }

    /**
     * 根据宽高属性的wrap或确定的值，重置图片大小
     * 该方法一般在开始整体布局大小确定前调用
     */
    private void reSizeAsImageSize() {
        width = height = -3;
        if (attribute.getWidth() == BaseAttribute.WRAP_CONTENT) {
            setMinWidth(imageIcon.getIconWidth());
            width = getMinWidth();
        } else if (attribute.getWidth() != BaseAttribute.MATCH_PARENT) {
            width = attribute.getWidth();
        }
        if (attribute.getHeight() == BaseAttribute.WRAP_CONTENT) {
            setMinHeight(imageIcon.getIconHeight());
            height = getMinHeight();
        } else if (attribute.getHeight() != BaseAttribute.MATCH_PARENT) {
            height = attribute.getHeight();
        }
        if (width != -3 && height == -3) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, imageIcon.getIconHeight(), Image.SCALE_FAST));
        } else if (width == -3 && height != -3) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth(), height, Image.SCALE_FAST));
        } else if (width != -3) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
        }
    }

    @Override
    public void onInvalidateFinished() {
        reSizeAsComponentSize();
    }
}
