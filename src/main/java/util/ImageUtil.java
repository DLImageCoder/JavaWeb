package util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import constant.BaseConsts;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import sun.rmi.runtime.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by VOYAGER on 2018/8/4.
 */
public class ImageUtil {

    /**
     * 上传文件
     *
     * @param imgPath 图片的存储路径
     * @return 图片的url
     */
    public static String uploadImage(String imgPath) {
        Auth auth = Auth.create(BaseConsts.ACCESS_KEY, BaseConsts.SECRET_KEY);
        String token = auth.uploadToken(BaseConsts.BUCKET);

        Configuration configuration = new Configuration(Zone.autoZone());
        UploadManager manager = new UploadManager(configuration);
        try {
            Response response = manager.put(imgPath, null, token);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return BaseConsts.BASE_IMG_URL + putRet.hash;
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载图片
     *
     * @param url     图片的url
     * @param imgPath 指定图片的存储路径
     */
    public static void downloadImage(String url, String imgPath) {
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                OutputStream os = new FileOutputStream(imgPath);
                byte[] bytes = new byte[1024 * 128];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
            }
            System.out.println("responseCode is "+responseCode+"\n"
                +"url is "+url);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
}
