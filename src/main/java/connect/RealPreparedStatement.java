package connect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class RealPreparedStatement implements AutoCloseable{
    private PreparedStatement statement;
    private IStatementCallback callback;

    public RealPreparedStatement(PreparedStatement statement, IStatementCallback callback) {
        this.statement = statement;
        this.callback = callback;
        callback.newStatement();
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        statement.setInt(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        statement.setString(parameterIndex, x);
    }

    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    public void close() throws SQLException {
        if (statement != null) {
            statement.close();
            callback.closeStatement();
        }
    }
}
