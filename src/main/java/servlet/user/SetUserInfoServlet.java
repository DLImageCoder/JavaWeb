package servlet.user;

import bean.StatusBean;
import com.google.gson.Gson;
import connect.DataParcel;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.EncodeUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by VOYAGER on 2018/7/28.
 */
@WebServlet(urlPatterns = "/user/setInfo")
public class SetUserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();

        String userId = request.getParameter("userId");
        String name = request.getParameter("name");
        String age = request.getParameter("age");
        String sex = request.getParameter("sex");
        String head = request.getParameter("head");

        StatusBean status = new StatusBean();
        if (userId != null) {
            try {
                String sql;
                PreparedStatement statement;
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
                if(head!=null&&head.length()>0){
                    sql = " UPDATE user SET head=? WHERE uid=? ";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, head);
                    statement.setInt(2, Integer.parseInt(userId));
                    statement.executeUpdate();
                }
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            } catch (Exception e) {
                status.setStatus(BaseConsts.STATUS_FAILED);
            }
        } else {
            status.setStatus(BaseConsts.STATUS_FAILED);
        }
        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        pw.write(gson.toJson(status));
        pw.flush();
        pw.close();

        DatabaseConnection.closeConnection(parcel);
    }
}
