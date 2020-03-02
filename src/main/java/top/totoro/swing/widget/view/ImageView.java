package top.totoro.swing.widget.view;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageView extends View<ViewAttribute, JLabel> {

    private URL url;
    private ImageIcon imageIcon;

    public ImageView(View parent) {
        super(parent);
        component = new JLabel("", JLabel.CENTER);
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        String src = attribute.getSrc();
        if (src == null || "".equals(src)) System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不能为空");
        url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            imageIcon = new ImageIcon(url);
            reSizeImage();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reSizeImage();
    }

    public void setBackgroundImage(String src) {
        if (src == null || "".equals(src)) System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不能为空");
        url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            attribute.setSrc(src);
            imageIcon = new ImageIcon(url);
            context.invalidate();
        } else System.err.println("为id为" + attribute.getId() + "的View设置背景图片时，图片路径不正确");
    }

    private void reSizeImage() {
        if (attribute.getWidth() == BaseAttribute.WRAP_CONTENT) {
            setMinWidth(imageIcon.getIconWidth());
        } else if (attribute.getWidth() != BaseAttribute.MATCH_PARENT) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(attribute.getWidth(), imageIcon.getIconHeight(), Image.SCALE_DEFAULT));
        }
        if (attribute.getHeight() == BaseAttribute.WRAP_CONTENT) {
            setMinHeight(imageIcon.getIconHeight());
        } else if (attribute.getHeight() != BaseAttribute.MATCH_PARENT) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth(), attribute.getHeight(), Image.SCALE_DEFAULT));
        }
        ;
        component.setIcon(imageIcon);
    }
}
