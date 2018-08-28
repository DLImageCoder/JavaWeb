package thread;

import bean.ImgProcessBean;
import com.google.gson.Gson;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.CmdUtil;
import util.ImageUtil;
import util.ToolUtil;

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
    private final String imgOutputPath;
    private final HttpServletResponse response;
    private List<String> output;

    public ImageRunnable(String imgName, HttpServletResponse response) {
        this.imgName = imgName;
        imgOutputPath = BaseConsts.IMG_OUTPUT_Path + BaseConsts.SPLASH + imgName;
        this.response = response;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= 1000 * 60) {
            output = CmdUtil.execWithOutput("find " + BaseConsts.IMG_OUTPUT_Path + " -name " + imgName);
            if (output != null && !output.isEmpty() && output.get(0).equals(imgOutputPath)) {
                response(response, imgOutputPath);
                ImageUtil.clearImages();
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void response(HttpServletResponse response, String imgOutputPath) {
        String url = ImageUtil.uploadImage(imgOutputPath);
        if (response != null) {
            ImgProcessBean bean = new ImgProcessBean();
            bean.setStatus(BaseConsts.STATUS_SUCESSED);
            bean.setUrl(url);
            ToolUtil.responseJson(response, new Gson().toJson(bean));
        }
    }
}
