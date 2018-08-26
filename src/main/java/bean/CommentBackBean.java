package bean;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class CommentBackBean {
    private String text;
    private String time;
    private String name;

    public CommentBackBean(){}

    public CommentBackBean(String name,String text,String time){
        this.name=name;
        this.text=text;
        this.time=time;
    }
}
