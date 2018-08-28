package servlet.img;

import bean.ImgProcessBean;
import com.google.gson.Gson;
import constant.BaseConsts;
import thread.ImageRunnable;
import util.CmdUtil;
import util.ImageUtil;
import util.ToolUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/img/imgProcess")
public class ImgProcessServlet extends HttpServlet {

    private Random random = new Random();
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ToolUtil.setEncode(request, response);

        String url = request.getParameter("url");
        int type = Integer.parseInt(request.getParameter("type"));

        String imgName = System.currentTimeMillis() +
                (random.nextInt(8999) + 1000) +
                ".jpg";
        String imgPath = BaseConsts.IMG_INPUT_Path +
                BaseConsts.SPLASH + imgName;
        ImageUtil.clearImages();
        ImageUtil.downloadImage(url, imgPath);
        switch (type) {
            case BaseConsts.TYPE_MODEL_WAVE:
                execPython1("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_WAVE);
                break;
            case BaseConsts.TYPE_MODEL_UDNIE:
                execPython1("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_UDNIE);
                break;
            case BaseConsts.TYPE_MODEL_SCREAM:
                execPython1("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_SCREAM);
                break;
            case BaseConsts.TYPE_MODEL_RAIN_PRINCESS:
                execPython1("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_RAIN_PRINCESS);
                break;
            case BaseConsts.TYPE_MODEL_PAPRIKA:
                execLua("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_PAPRIKA);
                break;
            case BaseConsts.TYPE_MODEL_HAYAO:
                execLua("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_HAYAO);
                break;
            case BaseConsts.TYPE_MODEL_SPIRITED:
                execLua("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_SPIRITED);
                break;
            case BaseConsts.TYPE_MODEL_GENDER_MALE:
                execPython2("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_GENDER_MALE);
                break;
            case BaseConsts.TYPE_MODEL_GENDER_FEMALE:
                execPython2("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_GENDER_FEMALE);
                break;
            case BaseConsts.TYPE_MODEL_SMILE:
                execPython2("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_SMILE);
                break;
            case BaseConsts.TYPE_MODEL_HAIR_BLACK:
                execPython2("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_HAIR_BLACK);
                break;
            case BaseConsts.TYPE_MODEL_HAIR_BROWN:
                execPython2("cd " + BaseConsts.MODEL_PATH + BaseConsts.MODEL_HAIR_BROWN);
                break;
        }
        handleResult(imgName, response);
    }

    private void execPython1(String cmd) {
        CmdUtil.exec(cmd + ";python evaluate.py");
    }

    private void execPython2(String cmd) {
        CmdUtil.exec(cmd + ";python main.py");
    }

    private void execLua(String cmd) {
        CmdUtil.exec(cmd + ";th test.lua");
    }

    private void handleResult(String imgName, HttpServletResponse response) {
        List<String> output;
        String imgOutputPath = BaseConsts.IMG_OUTPUT_Path + BaseConsts.SPLASH + imgName;
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
