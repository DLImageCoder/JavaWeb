package servlet.moment;

import bean.StatusBean;
import com.google.gson.Gson;
import connect.ConnectionManager;
import connect.RealConnection;
import connect.RealPreparedStatement;
import constant.BaseConsts;
import util.ToolUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by VOYAGER on 2018/8/26.
 */
@WebServlet(urlPatterns = "/moment/deleteMoment")
public class DeleteMomentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RealConnection conn = ConnectionManager.inst().getRealConnection();

        String uid = request.getParameter("userId");
        String mid = request.getParameter("momentId");
        StatusBean status = new StatusBean();

        if (mid != null && mid.length() > 0
                && uid != null && uid.length() > 0) {
            RealPreparedStatement statement=null;
            try{
                String sql="DELETE FROM user WHERE mid=? AND uid=?";
                statement=conn.prepareStatement(sql);
                statement.setInt(1,Integer.parseInt(mid));
                statement.setInt(2,Integer.parseInt(uid));
                statement.executeUpdate();
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            }catch (SQLException e){
            }finally {
                ToolUtil.closeQuietly(statement);
            }
        }
        ToolUtil.responseJson(response,new Gson().toJson(status));
    }
}
