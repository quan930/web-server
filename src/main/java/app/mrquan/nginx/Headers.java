package app.mrquan.nginx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 请求响应头
 */
public class Headers extends HashMap<String,List<String>> {
    void set (String key,String value){
//        System.out.println("key:"+key+"\t"+"value:"+value);
        List<String> list = new ArrayList<String>();
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

    /**
     * 格式化 方便输出
     * @return
     */
    public String format(){
        StringBuilder stringBuilder = new StringBuilder();
        int m = 0;
        for ( Entry<String,List<String>> entry:entrySet()){
            stringBuilder.append(entry.getKey());
            stringBuilder.append(": ");
            for (int i = 0; i < entry.getValue().size()-1; i++) {
                stringBuilder.append(entry.getValue().get(i));
                stringBuilder.append(", ");
            }
            stringBuilder.append(entry.getValue().get(entry.getValue().size()-1));
            m++;
            if (m<size()){
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
