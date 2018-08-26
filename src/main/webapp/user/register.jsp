<%--
  Created by IntelliJ IDEA.
  User: VOYAGER
  Date: 2018/7/28
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="bean.StatusBean" %>
<%@ page import="constant.BaseConsts" %>
<%@ page import="connect.*" %>
<%@ page import="util.ToolUtil" %>
<%
    RealConnection conn = ConnectionManager.inst().getRealConnection();
    String userId = request.getParameter("userId");
    String userPwd = request.getParameter("userPwd");

    StatusBean status = new StatusBean();
    if (userId != null && userPwd != null
            && userId.length() > 0 && userId.length() <= 11
            && userPwd.length() > 0 && userPwd.length() <= 20) {
        RealPreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM user WHERE uid=? ";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userId));
            resultSet = statement.executeQuery();
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
        } finally {
            ToolUtil.closeQuietly(resultSet, statement);
        }
    }
    ToolUtil.responseJson(response,new Gson().toJson(status));
%>



