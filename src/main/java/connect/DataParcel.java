package connect;

import java.sql.Connection;

/**
 * Created by VOYAGER on 2018/7/28.
 */
public class DataParcel {
    private Connection conn;
    private int pos;

    public DataParcel(Connection conn,int pos){
        this.conn=conn;
        this.pos=pos;
    }

    public Connection getConnection(){
        return conn;
    }

    public int getPos(){
        return pos;
    }
}
