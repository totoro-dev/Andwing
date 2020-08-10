package top.totoro.swing.widget.base;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.util.Log;

import java.net.URL;
import java.util.List;

public final class DefaultAttribute {
    private static final String DEFAULT_ATTRIBUTE_CONFIG_FILE = "values/styles.xml";
    public static String defaultBackgroundColor = AttributeDefaultValue.WHITE_COLOR;
    public static String defaultBorderColor = "#dbdbdb";
    public static String defaultThemeColor = AttributeDefaultValue.WHITE_COLOR;

    /**
     * 加载应用的默认属性，给布局控件使用
     * @param app 要加载的默认属性的应用
     */
    public static void loadDefaultAttribute(Class<?> app) {
        URL url = app.getClassLoader().getResource(DEFAULT_ATTRIBUTE_CONFIG_FILE);
        if (url == null) {
            Log.d("DefaultAttribute", "no exist default resource file");
            return;
        }
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(url);
            Element root = document.getRootElement();
            if (root == null) return;
            Element defaultElement = root.element("default");
            List elements = defaultElement.elements();
            for (Object element : elements) {
                if (element instanceof Element) {
                    List attributes = ((Element) element).attributes();
                    for (Object attribute : attributes) {
                        if (attribute instanceof Attribute) {
                            String name = ((Attribute) attribute).getValue();
                            String value = ((Element) element).getText();
                            if (value == null || "".equals(value)) continue;
                            switch (name) {
                                case "backgroundColor":
                                    defaultBackgroundColor = getColor(defaultBackgroundColor, value);
                                    break;
                                case "borderColor":
                                    defaultBorderColor = getColor(defaultBorderColor, value);
                                    break;
                                case "themeColor":
                                    defaultThemeColor = getColor(defaultThemeColor, value);
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取正确的颜色
     *
     * @param defaultColor 如果颜色值不正确，返回默认颜色
     * @param value        颜色值
     * @return 颜色
     */
    private static String getColor(String defaultColor, String value) {
        if (BaseAttribute.isColor(value)) {
            return value;
        }
        return defaultColor;
    }
}
