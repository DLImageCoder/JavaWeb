package servlet.user;

import bean.GetUserInfoBean;
import bean.UserBean;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by VOYAGER on 2018/7/28.
 */
@WebServlet(urlPatterns = "/user/getInfo")
public class GetUserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ToolUtil.setEncode(request, response);

        RealConnection conn = ConnectionManager.inst().getRealConnection();

        GetUserInfoBean infoBean = new GetUserInfoBean();
        String userId = request.getParameter("userId");

        if (userId != null && userId.length() > 0) {
            String sql;
            RealPreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                sql = " SELECT * FROM user WHERE uid=? ";
                statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(userId));
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    infoBean.userInfo = new UserBean();
                    infoBean.userInfo.userId = resultSet.getInt("uid");
                    infoBean.userInfo.name = resultSet.getString("name");
                    infoBean.userInfo.age = resultSet.getInt("age");
                    infoBean.userInfo.sex = resultSet.getString("sex");
                    infoBean.userInfo.head = resultSet.getString("head");
                    infoBean.status = BaseConsts.STATUS_SUCESSED;
                }
            } catch (Exception e) {
            } finally {
                ToolUtil.closeQuietly(resultSet, statement);
            }
        }
        ToolUtil.responseJson(response, new Gson().toJson(infoBean));
    }
}
