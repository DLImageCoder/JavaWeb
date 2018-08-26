package connect;

import java.lang.ref.Reference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class RealConnection implements IStatementCallback {
    private Connection connection;
    private AtomicInteger statementCount=new AtomicInteger(0);
    private long idleStartNanoTime = Long.MAX_VALUE;

    public RealConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("org.git.mm.mysql.Driver");211.87.227.230:
            connection = DriverManager.getConnection("jdbc:mysql://47.106.140.199:3306/web", "root", "root321");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RealPreparedStatement prepareStatement(String sql) {
        RealPreparedStatement realStatement = null;
        try {
            realStatement = new RealPreparedStatement(
                    connection.prepareStatement(sql), this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return realStatement;
    }

    public int getCount() {
        return statementCount.get();
    }

    public long getIdleAtNanos() {
        if (idleStartNanoTime < 0)
            return idleStartNanoTime;
        return System.nanoTime() - idleStartNanoTime;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newStatement() {
        statementCount.incrementAndGet();
        idleStartNanoTime = -1L;
    }

    @Override
    public void closeStatement() {
        if (statementCount.decrementAndGet() <= 0) {
            idleStartNanoTime = System.nanoTime();
        }
    }
}
