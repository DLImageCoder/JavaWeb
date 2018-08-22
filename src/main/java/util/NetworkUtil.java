package util;

import okhttp3.OkHttpClient;

/**
 * Created by VOYAGER on 2018/8/4.
 */
public class NetworkUtil {
    private static final OkHttpClient client=new OkHttpClient();

    public static OkHttpClient getOKHttpClient(){
        return client;
    }
}
