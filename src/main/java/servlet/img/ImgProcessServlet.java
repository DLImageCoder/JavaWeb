package servlet.img;

import constant.BaseConsts;
import thread.ImageRunnable;
import util.CmdUtil;
import util.EncodeUtil;
import util.ImageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/img/imgProcess")
public class ImgProcessServlet extends HttpServlet{

    private Random random=new Random();
    private ExecutorService executor= Executors.newCachedThreadPool();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        String url=request.getParameter("url");
        int type= Integer.parseInt(request.getParameter("type"));

        String imgName=System.currentTimeMillis() +
                (random.nextInt(8999) + 1000) +
                ".jpg";
        String imgPath = BaseConsts.IMG_INPUT_Path +
                BaseConsts.SPLASH + imgName;
        ImageUtil.downloadImage(url,imgPath);
        if(type==0){
            CmdUtil.exec("th "+BaseConsts.MODEL_PATH+BaseConsts.MODEL_CARTOON+"/test.lua");
            executor.execute(new ImageRunnable(imgName,response));
        }

    }
}
