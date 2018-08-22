<%--
  Created by IntelliJ IDEA.
  User: VOYAGER
  Date: 2018/7/28
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*,connect.DatabaseConnection,connect.DataParcel" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="bean.StatusBean" %>
<%@ page import="constant.BaseConsts" %>
<% DataParcel parcel = DatabaseConnection.createConnection();
    Connection conn = parcel.getConnection();
    String userId = request.getParameter("userId");
    String userPwd = request.getParameter("userPwd");

    StatusBean status = new StatusBean();
    if (userId != null && userPwd != null
            && userId.length() > 0 && userId.length() <= 11
            && userPwd.length() > 0 && userPwd.length() <= 20) {
        try {
            String sql = "SELECT * FROM user WHERE uid=? ";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userId));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                status.setStatus(BaseConsts.STATUS_FAILED);
            } else {
                sql = "INSERT INTO user (uid,pwd) " +
                        "VALUES (?,?) ";
                statement = conn.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(userId));
                statement.setString(2, userPwd);
                statement.executeUpdate();
                status.setStatus(BaseConsts.STATUS_SUCESSED);
            }
        } catch (Exception e) {

        }
    } else {
        status.setStatus(BaseConsts.STATUS_FAILED);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式
    response.setCharacterEncoding("UTF-8");//setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题
    PrintWriter pw = response.getWriter();
    pw.write(gson.toJson(status));
    pw.flush();
    pw.close();
    DatabaseConnection.closeConnection(parcel);
%>



