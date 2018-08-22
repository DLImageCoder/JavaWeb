package servlet.moment;

import bean.GetMomentBean;
import bean.MomentBean;
import com.google.gson.Gson;
import com.sun.org.apache.regexp.internal.RE;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by VOYAGER on 2018/8/4.
 */
@WebServlet(urlPatterns = "/moment/getMoment")
public class GetMomentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();

        int mid = Integer.parseInt(request.getParameter("mid"));
        GetMomentBean momentBean=new GetMomentBean();
        momentBean.moments=new ArrayList<>(5);
        try {
            String sql;
            PreparedStatement statement;
            ResultSet resultSet;
            if (mid < 0) {
                sql = "SELECT MAX(mid) AS maxid FROM moment";
                statement = conn.prepareStatement(sql);
                resultSet=statement.executeQuery();
                if(resultSet.next()){
                    mid=resultSet.getInt("maxid");
                }
            }
            sql="SELECT * FROM moment WHERE mid<=? AND mid>? ORDER BY mid DESC ";
            statement = conn.prepareStatement(sql);
            statement.setInt(1,mid);
            statement.setInt(2,mid-5);
            resultSet=statement.executeQuery();
            while (resultSet.next()){
                momentBean.moments.add(new MomentBean()
                .setComments(resultSet.getString("comments"))
                .setImgs(resultSet.getString("imgs"))
                .setLikes(resultSet.getString("likes"))
                .setText(resultSet.getString("text"))
                .setMomentId(resultSet.getInt("mid"))
                .setTime(resultSet.getString("date"))
                .setUserId(resultSet.getInt("uid")));
            }
            momentBean.status= BaseConsts.STATUS_SUCESSED;
        } catch (SQLException e) {
            momentBean.status= BaseConsts.STATUS_FAILED;
        }
        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        pw.write(gson.toJson(momentBean));
        pw.flush();
        pw.close();
        DatabaseConnection.closeConnection(parcel);
    }
}
