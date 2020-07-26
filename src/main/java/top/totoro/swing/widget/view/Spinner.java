package top.totoro.swing.widget.view;

import org.dom4j.Attribute;
import org.dom4j.Element;
import top.totoro.swing.widget.bar.ActionBar;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.listener.InvalidateListener;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.listener.OnItemSelectedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class Spinner extends View<ViewAttribute, JPanel> implements OnClickListener {
    private JLabel mSelectedLabel;
    private JLabel mDropDownButton;
    protected JWindow mDropDownWindow;
    private int mSelectedPosition = -1;
    private String[] mStringArray;
    private JLabel[] mDropDownItems;
    private Color mSelectedColor = Color.white;
    private Color mEnterColor = Color.white;

    private String arrayAttrKey = "array";
    private String selectedColorKey = "selectedColor";
    private String enterColorKey = "enterColor";

    public Spinner(View parent) {
        super(parent);
        mDropDownWindow = new JWindow();
        component = new JPanel(new BorderLayout());
        mSelectedLabel = new JLabel("", JLabel.CENTER);
        mDropDownButton = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/drop_down_arrow.png"))), JLabel.CENTER);
        component.add(mSelectedLabel, BorderLayout.CENTER);
        component.add(mDropDownButton, BorderLayout.EAST);
        mDropDownWindow.setAlwaysOnTop(true);
        mDropDownWindow.getContentPane().setBackground(Color.white);
        mDropDownWindow.getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#dbdbdb")));
        addOnClickListener(this);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        mSelectedLabel.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        mSelectedLabel.setText(attribute.getText());
        mSelectedLabel.setForeground(Color.decode(attribute.getTextColor()));
        component.setSize(attribute.getWidth(), attribute.getHeight());
        component.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#dbdbdb")));
        Element element = attribute.getElement();
        Attribute arrayAttr = element.attribute(arrayAttrKey);
        if (arrayAttr != null) {
            mStringArray = arrayAttr.getValue().split(",");
        }
        Attribute enterColorAttr = element.attribute(enterColorKey);
        if (enterColorAttr != null) {
            String colorVal = enterColorAttr.getValue();
            if (colorVal.startsWith("#") && (colorVal.length() == 4 || colorVal.length() == 7)) {
                mEnterColor = Color.decode(colorVal);
            }
        }
        Attribute selectedColorAttr = element.attribute(selectedColorKey);
        if (selectedColorAttr != null) {
            String colorVal = selectedColorAttr.getValue();
            if (colorVal.startsWith("#") && (colorVal.length() == 4 || colorVal.length() == 7)) {
                mSelectedColor = Color.decode(colorVal);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        View.mShowingSpinner = this;
        /* 确定下拉框的下拉位置 */
        moveTo();
        setDropDownItems();

        mDropDownWindow.setSize(component.getWidth(), component.getHeight() * mStringArray.length);

        mDropDownWindow.setVisible(true);
    }

    /* 创建下拉框中的内容 */
    private void setDropDownItems() {
        mDropDownWindow.getContentPane().removeAll();
        if (mStringArray == null) {
            if (attribute.getText() == null || "".equals(attribute.getText()))
                mStringArray = new String[]{"无选项"};
            else mStringArray = new String[]{attribute.getText()};
        }
        mDropDownWindow.getContentPane().setLayout(new GridLayout(mStringArray.length, 1));
        mDropDownItems = new JLabel[mStringArray.length];
        for (int i = 0; i < mDropDownItems.length; i++) {
            mDropDownItems[i] = new JLabel(mStringArray[i], JLabel.CENTER);
            mDropDownItems[i].setSize(component.getWidth(), component.getHeight());
            mDropDownItems[i].setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
            mDropDownWindow.add(mDropDownItems[i]);
            mDropDownItems[i].setOpaque(true);
            mDropDownItems[i].setBackground(Color.white);
            if (i < mDropDownItems.length - 1) {
                mDropDownItems[i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#dbdbdb")));
            }
            if (mStringArray[i].equals(mSelectedLabel.getText())) {
                mDropDownItems[i].setBackground(mSelectedColor);
                mSelectedPosition = i;
            }
            int finalI = i;
            mDropDownItems[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mSelectedLabel.setText(mStringArray[finalI]);
                    mDropDownWindow.dispose();
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onSelected(finalI, mStringArray[finalI]);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (finalI != mSelectedPosition) {
                        mDropDownItems[finalI].setBackground(mEnterColor);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (finalI != mSelectedPosition) {
                        mDropDownItems[finalI].setBackground(Color.white);
                    }
                }
            });
        }
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void addOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    public void moveTo() {
        Point spinnerLocation = component.getLocationOnScreen();
        mDropDownWindow.setLocation(spinnerLocation.x,
                spinnerLocation.y + component.getHeight());
    }

    public void dismiss() {
        View.mShowingSpinner.mDropDownWindow.dispose();
        View.mShowingSpinner = null;
    }
}
