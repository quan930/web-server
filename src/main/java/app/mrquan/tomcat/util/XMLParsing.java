package app.mrquan.tomcat.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XMLParsing {
    /**
     * 根据web.xml文件解析 url -> .class 映射
     * @param filePath web.xml文件相对路径
     * @return map映射
     * @throws Exception 异常
     */
    public static Map<String,String> parsingXML(String filePath) throws Exception {
        Map<String,String> servletMappingMap = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(filePath);
        Document document = builder.parse(file);
        Map<String,String> mappingMap = xMLFindByNameToMap(document,"servlet-mapping","url-pattern","servlet-name");
        Map<String,String> servletMap = xMLFindByNameToMap(document,"servlet","servlet-name","servlet-class");
        for (String key:mappingMap.keySet()){
            String s = servletMap.get(mappingMap.get(key));
            if (s==null){
                throw new Exception("servlet映射有问题");
            }else {
                servletMappingMap.put(key,s);
            }
        }
        return servletMappingMap;
    }
    /**
     * 解析xml指定标签 转为map
     * @param document Document对象
     * @param tagName 标签名字
     * @param keyName 需要进行映射 key名字
     * @param valueName 需要进行映射 value 名字
     * @return map
     * @throws Exception 解析异常
     */
    private static Map<String,String> xMLFindByNameToMap(Document document,String tagName,String keyName,String valueName) throws Exception {
        if (document==null||tagName==null||keyName==null||valueName==null){
            throw new NullPointerException();
        }
        Map<String,String> map = new HashMap<>();
        NodeList servletMappingList = document.getElementsByTagName(tagName);//读取映射
        for (int i = 0; i < servletMappingList.getLength(); i++) {
            org.w3c.dom.Node mappingItem = servletMappingList.item(i);
            //解析节点的子节点
            NodeList childNodes = mappingItem.getChildNodes();
            //遍历childNodes获取每个节点的节点名和节点值
            String key = null;//servlet url(key)
            String value = null;//servlet名字(value)
            for (int j = 0; j < childNodes.getLength(); j++) {
                //区分出text类型的node以及element类型的node
                if (childNodes.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    if (childNodes.item(j).getNodeName().equals(keyName)){
                        key = childNodes.item(j).getTextContent();
                    }else if(childNodes.item(j).getNodeName().equals(valueName)){
                        value = childNodes.item(j).getTextContent();
                    }
                }
            }
            if ((key!=null)&&(value!=null)){
                map.put(key,value);
            }else {
                throw new Exception("xml格式错误");
            }
        }
        return map;
    }
}
