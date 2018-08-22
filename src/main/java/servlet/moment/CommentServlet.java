package servlet.moment;

import bean.CommentBean;
import bean.ExceptionBean;
import bean.StatusBean;
import com.google.gson.Gson;
import connect.DataParcel;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.EncodeUtil;
import util.GsonUtil;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/moment/comment")
public class CommentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();
        StatusBean status = new StatusBean();


        String mid = request.getParameter("momentId");
        String text = request.getParameter("text");
        String uid = request.getParameter("userId");
        String type=request.getParameter("type");

        if (Integer.parseInt(mid) >= 0 && Integer.parseInt(uid) > 0
                && text != null && text.length() > 0) {
            try {
                String sql;
                PreparedStatement statement;
                ResultSet resultSet;
                sql = "SELECT comments FROM moment WHERE mid=?";
                statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(mid));
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String comments = resultSet.getString("comments");
                    if (Integer.parseInt(type) == BaseConsts.TYPE_APPEND) {
                        comments = appendComment(comments, uid, text);
                    } else if (Integer.parseInt(type) == BaseConsts.TYPE_REMOVE) {
                        comments = removeComment(comments, uid, text);
                    }
                    sql = "UPDATE moment SET comments=? WHERE mid=?";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, comments);
                    statement.setInt(2, Integer.parseInt(mid));
                    statement.executeUpdate();
                    status.setStatus(BaseConsts.STATUS_SUCESSED);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        pw.write(gson.toJson(status));
        pw.flush();
        pw.close();
        DatabaseConnection.closeConnection(parcel);
    }

    private String appendComment(String comments, String uid, String text) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);

        List<CommentBean> list;
        if (comments == null || comments.length() == 0) {
            list = new ArrayList<>();
        } else {
            list = GsonUtil.jsonToArrayList(comments, CommentBean.class);
        }
        list.add(new CommentBean(uid, text, time));
        return new Gson().toJson(list);
    }

    private String removeComment(String comments, String uid, String text) {
        return comments;
    }
}
