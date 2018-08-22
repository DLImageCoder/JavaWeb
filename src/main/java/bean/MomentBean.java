package bean;

/**
 * Created by VOYAGER on 2018/8/4.
 */
public class MomentBean {

    public int momentId;
    public int userId;
    public String time;
    public String text;
    public String imgs;
    public String comments;
    public String likes;

    public MomentBean() {
    }

    public MomentBean(int momentId, int userId, String time,
                      String text, String imgs, String comments, String likes) {
        this.comments = comments;
        this.imgs = imgs;
        this.likes = likes;
        this.momentId = momentId;
        this.text = text;
        this.time = time;
        this.userId = userId;
    }

    public MomentBean setMomentId(int momentId) {
        this.momentId = momentId;
        return this;
    }

    public MomentBean setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public MomentBean setTime(String time) {
        this.time = time;
        return this;
    }

    public MomentBean setText(String text) {
        this.text = text;
        return this;
    }

    public MomentBean setImgs(String imgs) {
        this.imgs = imgs;
        return this;
    }

    public MomentBean setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public MomentBean setLikes(String likes) {
        this.likes = likes;
        return this;
    }
}
