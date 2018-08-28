package servlet.moment;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/moment/sendMoment")
public class SendMomentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ToolUtil.setEncode(request, response);
        RealConnection conn = ConnectionManager.inst().getRealConnection();

        String userId = request.getParameter("userId");
        String text = request.getParameter("text");
        String imgs = request.getParameter("imgs");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        StatusBean status = new StatusBean();

        if (userId != null && userId.length() > 0) {
            String sql;
            RealPreparedStatement statement = null;
            try {
                sql = "INSERT INTO moment (uid,text,imgs,date) VALUES (?,?,?,?)";
                statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(userId));
                statement.setString(2, text);
                statement.setString(3, imgs);
                statement.setString(4, dateStr);
                statement.executeUpdate();
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            } catch (SQLException e) {
            }finally {
                ToolUtil.closeQuietly(statement);
            }
        }
       ToolUtil.responseJson(response,new Gson().toJson(status));
    }
}
