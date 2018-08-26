package servlet.moment;

import bean.CommentBackBean;
import bean.CommentBean;
import bean.GetMomentBean;
import bean.MomentBean;
import com.google.gson.Gson;
import com.sun.org.apache.regexp.internal.RE;
import connect.DataParcel;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.EncodeUtil;
import util.GsonUtil;
import util.ToolUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/moment/getMoment")
public class GetMomentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request, response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();

        int mid = Integer.parseInt(request.getParameter("mid"));
        String uid = request.getParameter("uid");
        GetMomentBean momentBean = new GetMomentBean();
        momentBean.moments = new ArrayList<>(5);
        try {
            String sql;
            PreparedStatement statement;
            ResultSet resultSet;
            if (mid < 0) {
                sql = "SELECT MAX(mid) AS maxid FROM moment";
                statement = conn.prepareStatement(sql);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    mid = resultSet.getInt("maxid");
                }
            }
            sql = "SELECT * FROM moment WHERE mid<=? AND mid>? ORDER BY mid DESC ";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, mid);
            statement.setInt(2, mid - 5);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String comments = resultSet.getString("comments");
                String likes = resultSet.getString("likes");
                momentBean.moments.add(new MomentBean()
                        .setComments(getCommentBack(comments,conn))
                        .setImgs(resultSet.getString("imgs"))
                        .setText(resultSet.getString("text"))
                        .setMomentId(resultSet.getInt("mid"))
                        .setTime(resultSet.getString("date"))
                        .setUserId(Integer.parseInt(resultSet.getString("uid")))
                        .setLikes(getLikesNum(likes))
                        .setHasLike(getHasLike(likes, uid)));
            }
            momentBean.status = BaseConsts.STATUS_SUCESSED;
        } catch (SQLException e) {
            momentBean.status = BaseConsts.STATUS_FAILED;
        }
        ToolUtil.responseJson(response,new Gson().toJson(momentBean));
    }

    private String getCommentBack(String comments, Connection conn) {
        if (comments == null || comments.length() == 0)
            return null;
        List<CommentBean> list = GsonUtil.jsonToArrayList(comments, CommentBean.class);
        List<CommentBackBean> listBack = new ArrayList<>();
        for (CommentBean comment : list) {
            if (comment != null && comment.getUid() != null) {
                String name=getNameByUid(comment.getUid(),conn);
                listBack.add(new CommentBackBean(name,comment.getText(),comment.getTime()));
            }
        }
        return new Gson().toJson(listBack);
    }

    private String getNameByUid(String uid,Connection conn){
        try {
            String sql = "SELECT name FROM user WHERE uid=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(uid));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getLikesNum(String likes) {
        if (likes == null || likes.length() == 0)
            return 0;
        List<String> list = GsonUtil.jsonToStringList(likes);
        return list.size();
    }

    private int getHasLike(String likes, String uid) {
        if (likes == null || likes.length() == 0)
            return 0;
        if (uid == null || uid.length() == 0)
            return 0;
        List<String> list = GsonUtil.jsonToStringList(likes);
        if (list.isEmpty()) {
            return 0;
        }
        for (String str : list) {
            if (uid.equals(str)) {
                return 1;
            }
        }
        return 0;
    }
}
