package connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VOYAGER on 2018/7/28.
 */
public class DatabaseConnection {
    private static List<Map.Entry<Connection, Integer>> list=null;
    private static final int maxCount=5;
    static {
        Map<Connection, Integer> map = new HashMap<Connection, Integer>();
        for (int i = 0; i < maxCount; i++) {
            Connection conn;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                //Class.forName("org.git.mm.mysql.Driver");211.87.227.230:
                conn = DriverManager.getConnection("jdbc:mysql://47.106.140.199:3306/web","root","root321");
                map.put(conn, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        list = new ArrayList<Map.Entry<Connection, Integer>>(map.entrySet());
    }


    public static DataParcel createConnection() {
        int pos=0, count=0;
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Connection, Integer> entry = list.get(i);
            if (entry.getValue()<=count){
                pos=i;
                count=entry.getValue();
            }
        }
        list.get(pos).setValue(list.get(pos).getValue()+1);
        return new DataParcel(list.get(pos).getKey(),pos);
    }

    public static void closeConnection(DataParcel item){
        int pos=item.getPos();
        list.get(pos).setValue(list.get(pos).getValue()-1);
    }
}
