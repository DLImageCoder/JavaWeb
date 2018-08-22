package bean;

import constant.BaseConsts;

/**
 * Created by VOYAGER on 2018/8/18.
 */
public class ImgProcessBean {
    private int status = BaseConsts.STATUS_FAILED;
    private String url = null;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
