package servlet.moment;

import bean.StatusBean;
import com.google.gson.Gson;
import connect.*;
import constant.BaseConsts;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/7.
 */
@WebServlet(urlPatterns = "/moment/like")
public class LikeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ToolUtil.setEncode(request, response);
        RealConnection conn = ConnectionManager.inst().getRealConnection();

        StatusBean status = new StatusBean();

        String uid = request.getParameter("userId");
        String mid = request.getParameter("momentId");
        String type = request.getParameter("type");

        if (mid != null && mid.length() > 0
                && uid != null && uid.length() > 0) {
            String sql;
            RealPreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                sql = "SELECT likes FROM moment WHERE mid=?";
                statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(mid));
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String likes = resultSet.getString("likes");
                    if (Integer.parseInt(type) == BaseConsts.TYPE_APPEND) {
                        likes = appendLike(likes, uid);
                    } else if (Integer.parseInt(type) == BaseConsts.TYPE_REMOVE) {
                        likes = removeLike(likes, uid);
                    }
                    sql = "UPDATE moment SET likes=? WHERE mid=?";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, likes);
                    statement.setInt(2, Integer.parseInt(mid));
                    statement.executeUpdate();
                    status.setStatus(BaseConsts.STATUS_SUCESSED);
                }
            } catch (Exception e) {
            } finally {
                ToolUtil.closeQuietly(resultSet, statement);
            }
        }
        ToolUtil.responseJson(response,new Gson().toJson(status));
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
