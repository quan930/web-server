package app.mrquan.tomcat.util;

import java.util.HashMap;
import java.util.Map;

public class StringHandling {
    /**
     * 查找字符串 转map
     * @param queryString 查找字符串
     * @return map
     */
    public static Map<String,String> queryStringToMap(String queryString){
        Map<String,String> map = new HashMap<>();
        for (String s:queryString.split("&")){
            if (!s.contains("=")){
                break;
            }
            map.put(s.substring(0,s.indexOf("=")),s.substring(s.indexOf("=")+1));
        }
        return map;
    }

    /**
     * 方法名称格式化 xxx 转 doXxx
     * @param s 方法名
     * @return string
     */
    public static String methodFormat(String s){
        if (s==null)
            return null;
        s = s.toLowerCase();
        char[] cs=s.toCharArray();
        cs[0]-=32;
        return "do"+String.valueOf(cs);
    }

    /**
     * 首字母大写
     * @param s 需要格式化的字符串
     * @return string
     */
    public static String captureName(String s) {
        char[] cs=s.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }
}
