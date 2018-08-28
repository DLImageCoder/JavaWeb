package util;

import com.sun.istack.internal.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class ToolUtil {

    public static void closeQuietly(@NotNull AutoCloseable... closeable) {
        for (int i = 0; i < closeable.length; i++) {
            try {
                closeable.clone();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public static void responseJson(HttpServletResponse response, String str) {
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(str);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public static void setEncode(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
