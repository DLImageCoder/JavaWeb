package servlet.moment;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/7.
 */
@WebServlet(urlPatterns = "/moment/like")
public class LikeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request, response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();
        StatusBean status = new StatusBean();

        String userId = request.getParameter("userId");
        String momentId = request.getParameter("momentId");
        String type = request.getParameter("type");

        try {
            String sql;
            PreparedStatement statement;
            ResultSet resultSet;
            sql = "SELECT likes FROM moment WHERE mid=?";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(momentId));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String likes = resultSet.getString("likes");
                if (Integer.parseInt(type) == BaseConsts.TYPE_APPEND) {
                    likes = appendLike(likes, userId);
                } else if (Integer.parseInt(type) == BaseConsts.TYPE_REMOVE) {
                    likes = removeLike(likes, userId);
                }
                sql = "UPDATE moment SET likes=? WHERE mid=?";
                statement = conn.prepareStatement(sql);
                statement.setString(1, likes);
                statement.setInt(2, Integer.parseInt(momentId));
                statement.executeUpdate();
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        pw.write(gson.toJson(status));
        pw.flush();
        pw.close();
        DatabaseConnection.closeConnection(parcel);
    }

    private String appendLike(String likes, String uid) {
        if (uid == null)
            return likes;
        List<String> list;
        if (likes == null || likes.length() == 0) {
            list = new ArrayList<>();
        } else {
            list = GsonUtil.jsonToStringList(likes);
        }
        if (list.isEmpty()) {
            list.add(uid);
        } else {
            for (String str : list) {
                if (uid.equals(str)) {
                    return likes;
                }
            }
            list.add(uid);
        }
        return new Gson().toJson(list);
    }

    private String removeLike(String likes, String uid) {
        List<String> list;
        if (likes == null || likes.length() == 0) {
            list = new ArrayList<>();
        } else {
            list = GsonUtil.jsonToStringList(likes);
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(uid)) {
                list.remove(i);
            }
        }
        return new Gson().toJson(list);
    }
}
