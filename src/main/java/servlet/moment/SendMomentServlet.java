package servlet.moment;

import bean.StatusBean;
import com.google.gson.Gson;
import connect.DataParcel;
import connect.DatabaseConnection;
import constant.BaseConsts;
import util.EncodeUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/moment/sendMoment")
public class SendMomentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();

        String userId = request.getParameter("userId");
        String text = request.getParameter("text");
        String imgs = request.getParameter("imgs");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        StatusBean status=new StatusBean();

        if (userId != null && userId.length() > 0) {
            try {
                String sql = "INSERT INTO moment (uid,text,imgs,date) VALUES (?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setInt(1,Integer.parseInt(userId));
                statement.setString(2,text);
                statement.setString(3,imgs);
                statement.setString(4,dateStr);
                statement.executeUpdate();
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            } catch (SQLException e) {
                status.setStatus(BaseConsts.STATUS_FAILED);
            }
        }else{
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
