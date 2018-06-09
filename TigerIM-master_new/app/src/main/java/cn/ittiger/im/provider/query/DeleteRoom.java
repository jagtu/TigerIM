package cn.ittiger.im.provider.query;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.Map;

import cn.ittiger.im.provider.join.JoinRoom;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class DeleteRoom extends IQ {

    public static final String ELEMENT = QUERY_ELEMENT;
    public static final String NAMESPACE = "http://mesoftware.cn/groupchat/group/remove";

    private final String instructions;
    private final Map<String, String> attributes;


    public DeleteRoom() {
        this(null);
    }

    public DeleteRoom(Map<String, String> attributes) {
        this(null, attributes);
    }

    public DeleteRoom(String instructions, Map<String, String> attributes) {
        super(ELEMENT, NAMESPACE);
        this.instructions = instructions;
        this.attributes = attributes;
    }


    /**
     * Returns the registration instructions, or <tt>null</tt> if no instructions
     * have been set. If present, instructions should be displayed to the end-user
     * that will complete the registration process.
     *
     * @return the registration instructions, or <tt>null</tt> if there are none.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Returns the map of String key/value pairs of account attributes.
     *
     * @return the account attributes.
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("instructions", instructions);
        if (attributes != null && attributes.size() > 0) {
            for (String name : attributes.keySet()) {
                String value = attributes.get(name);
                xml.append(new Item(name,value).toXML());
            }
        }

        return xml;
    }
    public static class Item {
        private String name;
        private String value;

        public Item (String name,String value){
            this.name = name;
            this.value = value;
        }
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement("item");
            xml.optAttribute(name, value);
            xml.closeEmptyElement();
            return xml;
        }
    }
}

