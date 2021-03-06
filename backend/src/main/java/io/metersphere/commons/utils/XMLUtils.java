package io.metersphere.commons.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.performance.parse.EngineSourceParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.*;

public class XMLUtils {

    public static void setExpandEntityReferencesFalse(DocumentBuilderFactory documentBuilderFactory) {
        try {
            String FEATURE = null;
            FEATURE = "http://javax.xml.XMLConstants/feature/secure-processing";
            documentBuilderFactory.setFeature(FEATURE, true);
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            documentBuilderFactory.setFeature(FEATURE, true);
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            documentBuilderFactory.setFeature(FEATURE, false);
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            documentBuilderFactory.setFeature(FEATURE, false);
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            documentBuilderFactory.setFeature(FEATURE, false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    private static void jsonToXmlStr(JSONObject jObj, StringBuffer buffer, StringBuffer tab) {
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        StringBuffer nowTab = new StringBuffer(tab.toString());
        for (Map.Entry<String, Object> en : se) {
            if (en == null || en.getValue() == null) continue;
            if (en.getValue() instanceof JSONObject) {
                buffer.append(tab).append("<").append(en.getKey()).append(">\n");
                JSONObject jo = jObj.getJSONObject(en.getKey());
                jsonToXmlStr(jo, buffer, nowTab.append("\t"));
                buffer.append(tab).append("</").append(en.getKey()).append(">\n");
            } else if (en.getValue() instanceof JSONArray) {
                JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append(tab).append("<").append(en.getKey()).append(">\n");
                    if (StringUtils.isNotBlank(jarray.getString(i))) {
                        JSONObject jsonobject = jarray.getJSONObject(i);
                        jsonToXmlStr(jsonobject, buffer, nowTab.append("\t"));
                        buffer.append(tab).append("</").append(en.getKey()).append(">\n");
                    }
                }
            } else if (en.getValue() instanceof String) {
                buffer.append(tab).append("<").append(en.getKey()).append(">").append(en.getValue());
                buffer.append("</").append(en.getKey()).append(">\n");
            }
        }
    }

    public static String jsonToXmlStr(JSONObject jObj) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        try {
            jsonToXmlStr(jObj, buffer, new StringBuffer(""));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
        return buffer.toString();
    }

    //  ??????????????? xml ?????????????????? json ??????
    public static JSONObject XmlToJson(String xml) {
        JSONObject result = new JSONObject();
        if (xml == null)
            return null;
        List<String> list = preProcessXml(xml);
        try {
            result = (JSONObject) XmlTagToJsonObject(list);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
//            MSException.throwException(Translator.get("illegal_xml_format"));
        }
        return result;
    }

    //  ????????? xml ?????????????????? tag + data ?????????
    private static List<String> preProcessXml(String xml) {
        int begin = xml.indexOf("?>");
        if (begin != -1) {
            if (begin + 2 >= xml.length()) {
                return null;
            }
            xml = xml.substring(begin + 2);
        }   //  <?xml version="1.0" encoding="utf-8"?> ?????????????????????
        String rgex = ">";
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(xml);
        xml = m.replaceAll("> ");
        rgex = "\\s*</";
        pattern = Pattern.compile(rgex);
        m = pattern.matcher(xml);
        xml = m.replaceAll(" </");
        return Arrays.asList(xml.split(" "));
    }

    //  ???????????????????????????????????????????????? json ??????
    private static Object XmlTagToJsonObject(List<String> list) {
        if (list == null || list.size() == 0)
            return null;
        Stack<String> tagStack = new Stack<>(); //  tag ???
        Stack<Object> valueStack = new Stack<>();   //  ?????????
        valueStack.push(new JSONObject());  //  ???????????????????????????????????????????????????
        for (String item : list) {
            String beginTag = isBeginTag(item), endTag = isEndTag(item);    //  ???????????? tag ?????????????????????
            if (beginTag != null) {
                tagStack.push(beginTag);
                valueStack.push(new JSONObject());
            } else if (endTag != null) {
                if (endTag.equals(tagStack.peek())) { //  ????????? tag
                    Object topValue = valueStack.peek();
                    if (topValue instanceof String) {    //  ?????????????????? xml ??????
                        valueStack.pop();
                    }
                    valueStack.pop();
                    if (valueStack.peek() instanceof JSONObject) {
                        ((JSONObject) valueStack.peek()).put(tagStack.peek(), topValue);
                    }
                    tagStack.pop();
                }
            } else {
                valueStack.push(item);
            }
        }
        if (valueStack.empty())
            return null;
        return valueStack.peek();
    }

    private static String isEndTag(String tagLine) {
        String rgex = "</(\\w*)>";
        Pattern pattern = Pattern.compile(rgex);// ????????????????? ????
        Matcher m = pattern.matcher(tagLine);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static String isBeginTag(String tagLine) {
        String rgex = "<(\\w*)>";
        Pattern pattern = Pattern.compile(rgex);// ????????????????? ????
        Matcher m = pattern.matcher(tagLine);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static Document stringToDocument(String xml) {
        try {
            return EngineSourceParserFactory.getDocument(new ByteArrayInputStream(xml.getBytes("utf-8")));
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    public static JSONObject stringToJSONObject(String xml) {
        try {
            return elementToJSONObject(stringToDocument(xml).getRootElement());
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    public static JSONObject elementToJSONObject(Element node) {
        JSONObject result = new JSONObject();
        // ?????????????????????????????????????????????
        List<Attribute> listAttr = node.attributes();// ??????????????????????????????list
        for (Attribute attr : listAttr) {// ?????????????????????????????????
            result.put(attr.getName(), attr.getValue());
        }
        // ??????????????????????????????????????????
        List<Element> listElement = node.elements();// ????????????????????????list
        if (!listElement.isEmpty()) {
            for (Element e : listElement) {// ???????????????????????????
                if (e.attributes().isEmpty() && e.elements().isEmpty()) // ?????????????????????????????????????????????
                    result.put(e.getName(), e.getTextTrim());// ?????????????????????????????????????????????????????????
                else {
                    if (!result.containsKey(e.getName())) // ?????????????????????????????????????????????????????????
                        result.put(e.getName(), new JSONArray());// ???????????????
                    ((JSONArray) result.get(e.getName())).add(elementToJSONObject(e));// ???????????????????????????????????????????????????????????????
                }
            }
        }
        return result;
    }
}
