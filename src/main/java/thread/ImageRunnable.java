package thread;

import bean.ImgProcessBean;
import com.google.gson.Gson;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.CmdUtil;
import util.ImageUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/18.
 */
public class ImageRunnable implements Runnable {

    private final String imgName;
    private final String imgPath;
    private final WeakReference<HttpServletResponse> response;
    private List<String> output;

    public ImageRunnable(String imgName, HttpServletResponse response) {
        this.imgName = imgName;
        imgPath = BaseConsts.IMG_OUTPUT_Path + BaseConsts.SPLASH + imgName;
        this.response = new WeakReference<>(response);
    }

    @Override
    public void run() {
        while (true) {
            output = CmdUtil.execWithOutput("find " + BaseConsts.IMG_OUTPUT_Path + " -name \"" + imgName + "\"");
            if (!output.isEmpty() && output.get(0).equals(imgPath)) {
                response();
                deleteImg();
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void response() {
        String url = ImageUtil.uploadImage(imgName);
        if (response.get() != null) {
            PrintWriter pw = null;
            ImgProcessBean bean = new ImgProcessBean();
            bean.setStatus(BaseConsts.STATUS_SUCESSED);
            bean.setUrl(url);
            try {
                Gson gson = new Gson();
                pw = response.get().getWriter();
                pw.write(gson.toJson(bean));
                pw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    private void deleteImg(){
        CmdUtil.exec("rm -rf "+imgPath);
    }
}
