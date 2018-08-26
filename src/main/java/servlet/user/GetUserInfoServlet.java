package servlet.user;

import bean.GetUserInfoBean;
import bean.StatusBean;
import bean.UserBean;
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
import java.sql.ResultSet;

/**
 * Created by VOYAGER on 2018/7/28.
 */
@WebServlet(urlPatterns = "/user/getInfo")
public class GetUserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EncodeUtil.setEncode(request,response);

        DataParcel parcel = DatabaseConnection.createConnection();
        Connection conn = parcel.getConnection();
        GetUserInfoBean infoBean=new GetUserInfoBean();
        String userId = request.getParameter("userId");

        if (userId != null) {
            try {
                String sql = " SELECT * FROM user WHERE uid=? ";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(userId));
                ResultSet resultSet=statement.executeQuery();

                if(resultSet.next()){
                    infoBean.userInfo=new UserBean();
                    infoBean.userInfo.userId=resultSet.getInt("uid");
                    infoBean.userInfo.name=resultSet.getString("name");
                    infoBean.userInfo.age=resultSet.getInt("age");
                    infoBean.userInfo.sex=resultSet.getString("sex");
                    infoBean.userInfo.head=resultSet.getString("head");
                    infoBean.status=BaseConsts.STATUS_SUCESSED;
                }else{
                    infoBean.status=BaseConsts.STATUS_FAILED;
                }
                resultSet.close();
            }catch (Exception e){
                infoBean.status=BaseConsts.STATUS_FAILED;
            }
        }
        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        pw.write(gson.toJson(infoBean));
        pw.flush();
        pw.close();

        DatabaseConnection.closeConnection(parcel);
    }
}
