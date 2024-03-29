package top.totoro.swing.widget.view;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.base.DefaultAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.context.PopupWindow;
import top.totoro.swing.widget.event.MotionEvent;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.listener.OnTextChangeListener;
import top.totoro.swing.widget.util.AttributeKey;
import top.totoro.swing.widget.util.SLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 编辑框
 */
@SuppressWarnings("unused")
public class EditText extends View<ViewAttribute, JTextField> {

    private OnTextChangeListener onTextChangeListener;
    private String origin = "";

    public EditText(View<?, ?> parent) {
        super(parent);
        component = new JTextField();
        // 设置制表符占位数
//        component.setTabSize(4);
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (component.getText().equals(attribute.getHintText())) {
                    setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (component.getText().equals("")) {
                    setHint(attribute.getHintText());
                }
            }
        });
        component.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        if (attribute.getElement().attribute(AttributeKey.SHOW_MENU_ABLE) == null) {
            attribute.setShowMenuAble(BaseAttribute.VISIBLE);
        }
        super.setAttribute(attribute);
        remeasureSize();
        component.setSize(attribute.getWidth(), attribute.getHeight());
        if (!"".equals(attribute.getHintText())) {
            setHint(attribute.getHintText());
        } else {
            setText(attribute.getText());
        }
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, attribute.getBorderColor()));
    }

    /**
     * 设置提示语
     *
     * @param hint 提示语
     */
    public void setHint(String hint) {
        // change by HLM on 2020/9/26 只有当前编辑框没有内容时才显示hint
        if (getText().equals("")) {
            component.setFont(new Font(attribute.getTextStyle(), Font.ITALIC, attribute.getTextSize()));
            component.setForeground(Color.decode("#ababab"));
            component.setText(hint);
        }
        // change end
        attribute.setHintText(hint);
    }

    /**
     * 设置编辑框文本内容
     *
     * @param text 文本内容
     */
    public void setText(String text) {
        if (text == null || "".equals(text)) {
            setHint(attribute.getHintText());
        }
        //noinspection MagicConstant
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        component.setForeground(Color.decode(attribute.getTextColor()));
        component.setText(text);
    }

    /**
     * 获取编辑框的真实内容，而不返回hint内容
     *
     * @return 编辑框内容
     */
    public String getText() {
        if (component == null) return "";
        String text = component.getText();
        if (text == null || text.equals(attribute.getHintText())) return "";
        return text;
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    protected void remeasureSize() {
        // minHeight = size + size / 5 + 10 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 20, minHeight = size + size / 5 + 10;
        String text = attribute.getText();
        char[] chars = text.toCharArray();
        for (char c :
                chars) {
            // 根据英文、英文符号、中文中文符号来确定TextView至少要多大才能容的下
            if (Integer.parseInt(Integer.toString(c)) < 128) {
                minWidth += size / 2;
            } else {
                if (String.valueOf(c).matches("。？、“”——")) {
                    minWidth += 5 * size / 8;
                } else {
                    minWidth += size + 1; // 中文字符需要加1
                }
            }
        }
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    /**
     * 设置编辑框文本内容变化监听，
     * 文本发生变化的1秒钟内会触发。
     *
     * @param listener 监听器
     */
    public void addOnTextChangeListener(OnTextChangeListener listener) {
        onTextChangeListener = listener;
        origin = getText();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            if (onTextChangeListener == null || origin.equals(getText())) return;
            origin = getText();
            onTextChangeListener.onChange(origin);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static final String EDITTEXT_MENU_WINDOW_RES_FILE = "exit_text_menu_window.swing";
    private static final String SELECT_ALL_VIEW_ID = "selectAll";
    private static final String CUT_ALL_VIEW_ID = "cutAll";
    private static final String COPY_ALL_VIEW_ID = "copyAll";
    private static final String PASTE_ALL_VIEW_ID = "pasteAll";

    @Override
    public PopupWindow createMenuWindow() {
        return new PopupWindow(EDITTEXT_MENU_WINDOW_RES_FILE, 160, 122) {

            private OnClickListener opClickListener;

            @Override
            public void setContentView(String layoutId) {
                super.setContentView(layoutId);
                if (opClickListener == null) {
                    opClickListener = createOpListener();
                }
                findViewById(SELECT_ALL_VIEW_ID).addOnClickListener(opClickListener);
                findViewById(CUT_ALL_VIEW_ID).addOnClickListener(opClickListener);
                findViewById(COPY_ALL_VIEW_ID).addOnClickListener(opClickListener);
                findViewById(PASTE_ALL_VIEW_ID).addOnClickListener(opClickListener);
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
                    if (EditText.this.component == null) {
                        SLog.e(EditText.this, "click menu item, but component is null");
                        return;
                    }
                    switch (view.getId()) {
                        case SELECT_ALL_VIEW_ID:
                            component.requestFocus();
                            component.selectAll();
                            break;
                        case CUT_ALL_VIEW_ID:
                            component.cut();
                            break;
                        case COPY_ALL_VIEW_ID:
                            component.copy();
                            break;
                        case PASTE_ALL_VIEW_ID:
                            component.paste();
                            break;
                        default:
                    }
                };
            }

        };
    }
}
