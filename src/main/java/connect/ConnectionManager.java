package connect;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public class ConnectionManager {

    private static ConnectionManager INSTANCE;
    private static ConnectionPool connectionPool;

    public static ConnectionManager inst() {
        if (INSTANCE == null) {
            synchronized (ConnectionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionManager();
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }

    public void init(){
        connectionPool=new ConnectionPool();
    }

    public RealConnection getRealConnection(){
        return connectionPool.get();
    }
}
