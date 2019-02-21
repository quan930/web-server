package app.mrquan.tomcat.util;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Arrays;
import java.util.Stack;

public class JSPParsing {
    /**
     * 通过jsp生成java文件
     * @param fileName 文件名
     * @return .java 文件相对路径
     * @throws IOException io
     */
    public static String jspToJAVAFile(String fileName) throws IOException {
        String jspName = StringHandling.captureName(fileName.substring(1,fileName.indexOf("."))+"JSP");//jsp文件
        String javaFileName = "src/main/java/app/mrquan/tomcat/jsp/"+jspName+".java";
        File file = new File(javaFileName);//java文件
        File file1 = new File("web"+fileName);
        Stack<Boolean> stack = new Stack<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
        String m;
        if (file.exists()){
            file.delete();
        }
        if (file.createNewFile()){
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
            //package
            writer.println("package app.mrquan.tomcat.jsp;");
            writer.println("");
            //import
            writer.println("import app.mrquan.tomcat.http.*;");
            writer.println("import java.io.*;");
            while ((m = bufferedReader.readLine()).matches("^<%@.*?page import.*?%>$")){
                //引类
                writer.println("import "+m.substring(m.indexOf("\"")+1,m.lastIndexOf("\""))+";");
            }
            writer.println("");
            //模版
            writer.println("public class "+jspName+" extends HttpServlet {");
            writer.println("");
            writer.println("\t@Override");
            writer.println("\tpublic void doGet(HttpServletRequest request, HttpServletResponse response) {");
            writer.println("\t\tPrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));");
            //......
            while ((m.matches("^<%@.*?page contentType.*?%>$"))){
                //设置响应格式
                String contextType = m.substring(m.indexOf("contentType=\"")+"contentType=\"".length(),m.indexOf("\"",m.indexOf("contentType=\"")+"contentType=\"".length()+1));
                writer.println("\t\tresponse.setContentType(\""+contextType+"\");");
                m = bufferedReader.readLine();
            }
            while (m != null) {
                if (m.contains("<%")){
                    stack.push(true);
                    m = bufferedReader.readLine();
                    continue;
                }
                if (m.contains("%>")){
                    stack.pop();
                    m = bufferedReader.readLine();
                    continue;
                }
                if (!stack.empty()){
                    //java 代码
                    writer.println("\t"+m);
                }else {
                    writer.println("\t\tout.println(\""+m+"\");");
                }
                m = bufferedReader.readLine();
            }
            writer.println("\t\tout.flush();");
            writer.println("\t}");
            writer.println("}");
            writer.flush();
            writer.close();
            System.out.println("..................java文件生成");
        }
        return javaFileName;
    }

    /**
     * 编译java 文件
     * @param sourceFileInputPath java 文件路径
     * @param classFileOutputPath 编译目标位置
     * @return 成功与否
     */
    public static boolean compilerJavaFile(String sourceFileInputPath, String classFileOutputPath) {
        // 设置编译选项，配置class文件输出路径
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        Iterable<String> options = Arrays.asList("-d", classFileOutputPath);
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File(sourceFileInputPath)));
        return javaCompiler.getTask(null, fileManager, null, options, null, compilationUnits).call();
    }
}
