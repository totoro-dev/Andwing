package top.totoro.swing.widget.view;

import org.dom4j.Attribute;
import org.dom4j.Element;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.base.DefaultAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.context.PopupWindow;
import top.totoro.swing.widget.context.Toast;
import top.totoro.swing.widget.event.MotionEvent;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.util.AttributeKey;
import top.totoro.swing.widget.util.SLog;
import top.totoro.swing.widget.util.ThreadPoolUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeDefaultValue.*;

/**
 * 图片显示
 */
@SuppressWarnings("unused")
public class ImageView extends View<ViewAttribute, JPanel> {

    private final String TAG = getClass().getSimpleName();

    private final JLabel mImageContainer;
    private ImageIcon imageIcon;
    private int width = -3, height = -3;
    protected String scaleType = scaleFitCenter;

    public static final String FIT_CENTER = scaleFitCenter;
    public static final String CENTER = scaleCenter;
    public static final String FIT_XY = scaleFitXY;
    public static final String FIT_START = scaleFitStart;
    public static final String FIT_END = scaleFitEnd;

    public ImageView(View<?, ?> parent) {
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
                        fitXY(); // 拉伸
                        // 满控件显示
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitStart:
                        fitCenter();
                        // 左上显示
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitEnd:
                        fitCenter();
                        // 右下显示
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
                SLog.d(TAG, "scale = " + scale);
                width *= scale;
                height *= scale;
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
                mImageContainer.setSize(width, height);
            }
        }
    }

    /**
     * 设置新的图片资源
     *
     * @param src              图片资源路径
     * @param invalidateParent 是否全局刷新
     */
    public void setImage(String src, boolean invalidateParent) {
        if (src == null || "".equals(src)) {
            SLog.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不能为空");
            return;
        }
        URL url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            attribute.setSrc(src);
            imageIcon = new ImageIcon(url);
        } else {
            SLog.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不正确");
            imageIcon = null;
        }
        mImageContainer.setIcon(imageIcon);
        if (invalidateParent) {
            invalidateSuper();
        } else {
            reSizeAsImageSize();
        }
    }

    /**
     * 设置新的图片资源，并且全局刷新
     *
     * @param src 图片资源路径
     */
    public void setImage(String src) {
        setImage(src, true);
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        if (attribute.getElement().attribute(AttributeKey.SHOW_MENU_ABLE) == null) {
            attribute.setShowMenuAble(BaseAttribute.VISIBLE);
        }
        super.setAttribute(attribute);
        // 背景透明
        component.setOpaque(false);
        // 获取设置的缩放类型
        Element element = attribute.getElement();
        Attribute scaleType = element.attribute(AttributeKey.scaleType);
        if (scaleType != null) {
            setScaleType(scaleType.getValue(), false);
        } else {
            setImage(attribute.getSrc(), false);
        }
    }

    /**
     * 设置图片显示时的缩放模式，并且全局刷新
     *
     * @param scaleType 缩放模式
     * @see ImageView#FIT_CENTER 居中等比例缩放，整个图片可见（默认模式）
     * @see ImageView#FIT_XY 拉伸宽高，填满整个视图
     * @see ImageView#FIT_START 从左上角开始显示图片，等比例缩放，整个图片可见（右下角可能有空隙）
     * @see ImageView#FIT_END 从右下角开始显示图片，等比例缩放，整个图片可见（左上角可能有空隙）
     * @see ImageView#CENTER 不拉伸图片，保持图片大小居中显示
     */
    public void setScaleType(String scaleType) {
        // 全局刷新
        setScaleType(scaleType, true);
    }

    /**
     * 设置图片显示时的缩放模式
     *
     * @param scaleType        缩放模式
     * @param invalidateParent 是否需要全局刷新
     * @see ImageView#FIT_CENTER 居中等比例缩放，整个图片可见（默认模式）
     * @see ImageView#FIT_XY 拉伸宽高，填满整个视图
     * @see ImageView#FIT_START 从左上角开始显示图片，等比例缩放，整个图片可见（右下角可能有空隙）
     * @see ImageView#FIT_END 从右下角开始显示图片，等比例缩放，整个图片可见（左上角可能有空隙）
     * @see ImageView#CENTER 不拉伸图片，保持图片大小居中显示
     */
    public void setScaleType(String scaleType, boolean invalidateParent) {
        if (!Objects.equals(this.scaleType, scaleType)) {
            this.scaleType = scaleType;
        }
        // 触发图片的刷新，并决定是否刷新父布局
        setImage(getAttribute().getSrc(), invalidateParent);
    }

    private void fitXY() {
        if (getAttribute().getWidth() != BaseAttribute.WRAP_CONTENT) {
            width = component.getWidth();
        }
        if (getAttribute().getHeight() != BaseAttribute.WRAP_CONTENT) {
            height = component.getHeight();
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
        mImageContainer.setSize(width, height);
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

    private static final String IMAGE_VIEW_MENU_WINDOW_RES_FILE = "imageview_menu_window.swing";
    private static final String COPY_IMAGE_VIEW_ID = "copyImage";
    private static final String SAVE_IMAGE_VIEW_ID = "saveImage";

    @Override
    public PopupWindow createMenuWindow() {
        return new PopupWindow(IMAGE_VIEW_MENU_WINDOW_RES_FILE, 160, 62) {

            private OnClickListener opClickListener;

            @Override
            public void setContentView(String layoutId) {
                super.setContentView(layoutId);
                if (opClickListener == null) {
                    opClickListener = createOpListener();
                }
                findViewById(COPY_IMAGE_VIEW_ID).addOnClickListener(opClickListener);
                findViewById(SAVE_IMAGE_VIEW_ID).addOnClickListener(opClickListener);
            }

            @Override
            public void dispatchMotionEvent(View<?, ?> view, MotionEvent event) {
                super.dispatchMotionEvent(view, event);
                if (view instanceof BaseLayout) {
                    return;
                }
                if (event.getAction() == MotionEvent.ACTION_INSIDE) {
                    view.setBackgroundColor(Color.decode(DefaultAttribute.defaultBorderColor));
                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    view.setBackgroundColor(Color.WHITE);
                }
            }

            private OnClickListener createOpListener() {
                return view -> {
                    if (ImageView.this.component == null) {
                        SLog.e(ImageView.this, "click menu item, but component is null");
                        return;
                    }
                    if (COPY_IMAGE_VIEW_ID.equals(view.getId())) {
                        copyImageToClipboard();
                        dismiss();
                    } else if (SAVE_IMAGE_VIEW_ID.equals(view.getId())) {
                        saveImageToFile();
                    }
                };
            }

        };
    }

    private void saveImageToFile() {
        ThreadPoolUtil.execute(() -> {
            FileDialog saveDialog = new FileDialog(ActivityManager.getTopActivity().getFrame(), "保存图片", FileDialog.SAVE);
            saveDialog.setVisible(true);
            String dirPath = saveDialog.getDirectory();
            String fileName = saveDialog.getFile();
            if (dirPath == null) {
                SLog.e(ImageView.this, "save image break for no directory");
                return;
            }
            if (fileName == null) {
                SLog.e(ImageView.this, "save image break for no file name");
                return;
            } else if (!fileName.contains(".")) {
                fileName = fileName + ".png";
            }
            File file = new File(dirPath, fileName);
            try {
                BufferedImage image = getImage();
                ImageIO.write(image, "png", file);
                Toast.makeText(context, "图片已保存").show();
            } catch (Exception e) {
                SLog.e(ImageView.this, "save image error %s", e);
            } finally {
                saveDialog.dispose();
            }
        }, 0);
    }

    private void copyImageToClipboard() {
        ThreadPoolUtil.execute(() -> {
            BufferedImage image = getImage();

            Transferable trans = new Transferable() {

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (isDataFlavorSupported(flavor)) {
                        return image;
                    }
                    throw new UnsupportedFlavorException(flavor);
                }

                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }

            };

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
            Toast.makeText(ImageView.this.context, "图片已复制").show();
        }, 0);

    }

    private BufferedImage getImage() {
        Dimension size = mImageContainer.getSize();
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();

        mImageContainer.paint(g2);
        g2.dispose();
        return image;
    }

}
