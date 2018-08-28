package servlet.user;

import bean.StatusBean;
import com.google.gson.Gson;
import connect.*;
import constant.BaseConsts;
import util.ToolUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by VOYAGER on 2018/7/28.
 */
@WebServlet(urlPatterns = "/user/setInfo")
public class SetUserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ToolUtil.setEncode(request, response);

        RealConnection conn = ConnectionManager.inst().getRealConnection();

        String userId = request.getParameter("userId");
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        String sex = request.getParameter("sex");
        String head = request.getParameter("head");

        StatusBean status = new StatusBean();
        if (userId != null) {
            String sql;
            RealPreparedStatement statement = null;
            try {
                if (name != null && name.length() > 0 && name.length() <= 40) {
                    sql = " UPDATE user SET name=? WHERE uid=? ";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, name);
                    statement.setInt(2, Integer.parseInt(userId));
                    statement.executeUpdate();
                }
                if (age != null && age.length() > 0 && age.length() <= 3) {
                    sql = " UPDATE user SET age=? WHERE uid=? ";
                    statement = conn.prepareStatement(sql);
                    statement.setInt(1, Integer.parseInt(age));
                    statement.setInt(2, Integer.parseInt(userId));
                    statement.executeUpdate();
                }
                if (sex != null && sex.length() > 0 && sex.length() <= 10) {
                    sql = " UPDATE user SET sex=? WHERE uid=? ";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, sex);
                    statement.setInt(2, Integer.parseInt(userId));
                    statement.executeUpdate();
                }
                if (head != null && head.length() > 0) {
                    sql = " UPDATE user SET head=? WHERE uid=? ";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, head);
                    statement.setInt(2, Integer.parseInt(userId));
                    statement.executeUpdate();
                }
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            } catch (Exception e) {
                status.setStatus(BaseConsts.STATUS_FAILED);
            } finally {
                ToolUtil.closeQuietly(statement);
            }
        } else {
            status.setStatus(BaseConsts.STATUS_FAILED);
        }
        ToolUtil.responseJson(response, new Gson().toJson(status));
    }
}
