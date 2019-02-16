package app.mrquan.tomcat.http.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 请求响应头
 */
public class Headers extends HashMap<String,List<String>> {
    public void set (String key,String value){
//        System.out.println("key:"+key+"\t"+"value:"+value);
        List<String> list = new ArrayList<>();
        Collections.addAll(list, value.split(",[^ ]"));
//        System.out.println(list);
        put(key,list);
    }
    public void add(String key, String value){
        if (get(key)==null){
            put(key,new ArrayList<String>());
        }
        get(key).add(value);
    }
    public String format(){
        StringBuilder stringBuilder = new StringBuilder();
        for ( Entry<String,List<String>> entry:entrySet()){
            stringBuilder.append(entry.getKey());
            stringBuilder.append(": ");
            for (int i = 0; i < entry.getValue().size()-1; i++) {
                stringBuilder.append(entry.getValue().get(i));
                stringBuilder.append(", ");
            }
            stringBuilder.append(entry.getValue().get(entry.getValue().size()-1));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}