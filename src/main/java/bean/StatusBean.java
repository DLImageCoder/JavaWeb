package bean;

import constant.BaseConsts;

/**
 * Created by VOYAGER on 2018/7/28.
 */
public class StatusBean {
    public int status= BaseConsts.STATUS_FAILED;
    public StatusBean(){}
    public StatusBean(int status){
        this.status=status;
    }

    public void setStatus(int status){
        this.status=status;
    }
}
