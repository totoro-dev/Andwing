package top.totoro.swing.widget.context;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.manager.DialogManager;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 对话框
 */
public class Dialog extends Context {
    private final long mDialogId = System.currentTimeMillis();
    private final JWindow mDialogMarkWindow; // 蒙版
    private final JWindow mDialogWindow;
    private final Activity mActivity;
    private int mActivityWidth = 0, mActivityHeight = 0;
    private int width = 0, height = 0;
    private boolean mShowing = false; // 是否处于显示状态（不受窗口最小化的影响）

    public Dialog(Activity activity) {
        assert activity != null;
        mActivity = activity;
        mActivityWidth = activity.getSize().width;
        mActivityHeight = activity.getSize().height;
        width = mActivityWidth - 50;
        height = mActivityHeight - 50;

        mDialogMarkWindow = new JWindow();
        mDialogMarkWindow.setSize(mActivityWidth, mActivityHeight);
        mDialogMarkWindow.setOpacity(0.5F);
        MouseListener mDialogMarkWindowMouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dismiss();
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        };
        mDialogMarkWindow.addMouseListener(mDialogMarkWindowMouseListener);
        mDialogWindow = new JWindow();
        mDialogWindow.getContentPane().add(getMainView().getComponent());
        // 设置背景全透明
        mDialogWindow.getRootPane().setOpaque(false);
        mDialogWindow.setSize(mActivityWidth, mActivityHeight);
    }

    public void show() {
        // 保证永远只有一个dialog显示中
        if (DialogManager.getTopDialog() != null && DialogManager.getTopDialog().mDialogId != mDialogId) {
            DialogManager.dismiss();
        }
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            mDialogMarkWindow.setVisible(true);
            mDialogWindow.setVisible(true);
            DialogManager.setTopDialog(this);
            mShowing = true;
        }
    }


    public void hide(boolean... needToShowingAuto) {
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            mDialogMarkWindow.setVisible(false);
            mDialogWindow.setVisible(false);
            mShowing = needToShowingAuto.length > 0 && needToShowingAuto[0];
        }
        mActivity.getFrame().setOpacity(1);
    }

    public void dismiss() {
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            hide();
            mDialogMarkWindow.dispose();
            mDialogWindow.dispose();
            DialogManager.setTopDialog(null);
        }
    }

    public boolean isShowing() {
        return mDialogMarkWindow != null && mDialogWindow != null && mShowing;
    }

    public void setContentView(String layoutId) {
        getMainView().removeAllSon();
        // 将activity的窗口大小赋予dialog的大小
        LayoutAttribute mainViewAttr = new LayoutAttribute();
        mainViewAttr.setId("dialog_main_view");
        mainViewAttr.setWidth(width);
        mainViewAttr.setHeight(height);
        getMainView().setAttribute(mainViewAttr);

        layoutManager.inflate(getMainView(), layoutId);
        layoutManager.setMainLayout(getMainView());
        View<?, ?> contentView = getMainView().getSonByIndex(0);
        if (contentView.getAttribute().getWidth() >= 0) {
            width = contentView.getComponent().getWidth();
        }
        if (contentView.getAttribute().getHeight() >= 0) {
            height = contentView.getComponent().getHeight();
        }
        mainViewAttr.setWidth(width);
        mainViewAttr.setHeight(height);
        mDialogWindow.setSize(width, height);
        resetDialogWindowLocation();
        layoutManager.invalidate();
    }

    public void resetDialogWindowLocation() {
        int x = mActivity.getFrame().getX() + (mActivityWidth - width) / 2;
        int y = mActivity.getFrame().getY() + (mActivityHeight - height) / 2;
        mDialogMarkWindow.setLocation(mActivity.getFrame().getX(), mActivity.getFrame().getY());
        mDialogWindow.setLocation(x, y);
    }

}
