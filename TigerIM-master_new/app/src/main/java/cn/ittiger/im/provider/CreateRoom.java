package cn.ittiger.im.provider;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.EnumSet;
import java.util.Map;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/26
 *      desc    :
 * </pre>
 */

public class CreateRoom extends IQ {

    public static final String ELEMENT = QUERY_ELEMENT;
    public static final String NAMESPACE = "http://mesoftware.cn/groupchat/group/create";

    private final String instructions;
    private final Map<String, String> attributes;

    private String groupid;
    private String groupname;


    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public CreateRoom() {
        this(null);
    }

    public CreateRoom(Map<String, String> attributes) {
        this(null, attributes);
    }

    public CreateRoom(String instructions, Map<String, String> attributes) {
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

//    public static class Feature implements ExtensionElement {
//
//        public static final String ELEMENT = "create";
//        public static final String NAMESPACE = "http://mesoftware.cn/groupchat/group/create";
//        public static final CreateRoom.Feature INSTANCE = new CreateRoom.Feature();
//
//        private Feature() {
//        }
//
//        @Override
//        public String getElementName() {
//            return ELEMENT;
//        }
//
//        @Override
//        public CharSequence toXML() {
//            return '<' + ELEMENT + " xmlns='" + NAMESPACE + "'/>";
//        }
//
//        @Override
//        public String getNamespace() {
//            return NAMESPACE;
//        }
//
//    }
}
