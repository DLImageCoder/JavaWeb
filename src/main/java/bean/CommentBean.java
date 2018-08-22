package bean;

/**
 * Created by VOYAGER on 2018/8/6.
 */
public class CommentBean {
    private String text;
    private String time;
    private String uid;

    public CommentBean(){}

    public CommentBean(String uid,String text,String time){
        this.uid=uid;
        this.text=text;
        this.time=time;
    }


}
