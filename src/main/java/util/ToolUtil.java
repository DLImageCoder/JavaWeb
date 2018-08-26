package util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class ToolUtil {

    public static void closeQuietly(AutoCloseable... closeable) {
        for (int i = 0; i < closeable.length; i++) {
            try {
                closeable.clone();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public static void responseJson(HttpServletResponse response,String str){
        response.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式
        response.setCharacterEncoding("UTF-8");//setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(str);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }
    }
}
