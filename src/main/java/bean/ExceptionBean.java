package bean;

/**
 * Created by VOYAGER on 2018/8/12.
 */
public class ExceptionBean {
    private String exception;

    public ExceptionBean(){}

    public ExceptionBean(String exception){
        this.exception=exception;
    }

    public void setException(String exception){
        this.exception=exception;
    }

    public String getException(){
        return exception;
    }
}
