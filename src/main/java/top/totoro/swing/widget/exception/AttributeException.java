package top.totoro.swing.widget.exception;

import top.totoro.swing.widget.base.BaseAttribute;

@SuppressWarnings("unused")
public class AttributeException extends Exception {
    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String resName, String nodeName, String nodeMsg, String attributeName, String attributeMsg) {
        super(resName + "文件中" + nodeName + "组件的" + nodeMsg + attributeName + "属性" + attributeMsg);
    }

    public static AttributeException getAttrNameInvalid(BaseAttribute attribute, String attrName) {
        return new AttributeException(attribute.getResName() + "文件中" + attribute.getNodeName() + "组件的" + attrName + "属性无效。");
    }

    public static AttributeException getValueInvalid(BaseAttribute attribute, String attrName, String value, String msg) {
        return new AttributeException(attribute.getResName() + "文件中" + attribute.getNodeName() + "组件的" + attrName + "属性值: " + value + " " + msg + "。");
    }
}
