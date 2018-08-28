package connect;

import com.sun.istack.internal.Nullable;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by VOYAGER on 2018/8/25.
 */
public final class ConnectionPool {
    private final Deque<RealConnection> connections = new ArrayDeque<>();

    private static final Executor executor = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    private final int maxIdleConnections;
    private final int corePoolSize;
    private final long keepAliveDurationNs;
    private boolean cleanupRunning;

    public ConnectionPool() {
        this(5, 5, 10, TimeUnit.MINUTES);
    }

    public ConnectionPool(int corePoolSize, int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.corePoolSize = corePoolSize;
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);

        if (keepAliveDuration <= 0) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
        }
    }

    public synchronized int idleConnectionCount() {
        int total = 0;
        for (RealConnection connection : connections) {
            if (connection.getCount() <= 0) total++;
        }
        return total;
    }

    public synchronized int connectionCount() {
        return connections.size();
    }

    @Nullable
    RealConnection get() {
        if (connections.size() < corePoolSize || connections.isEmpty()) {
            RealConnection connection = new RealConnection();
            put(connection);
            return connection;
        }
        int minCount = Integer.MAX_VALUE;
        RealConnection minCountConnection = connections.getFirst();
        for (RealConnection connection : connections) {
            if (connection.getCount() < minCount) {
                minCount = connection.getCount();
                minCountConnection = connection;
            }
        }
        return minCountConnection;
    }

    void put(RealConnection connection) {
        if (!cleanupRunning) {
            cleanupRunning = true;
            //executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }

    boolean connectionBecameIdle(RealConnection connection) {
        if (connection.getCount() <= 0 || maxIdleConnections == 0) {
            connection.close();
            connections.remove(connection);
            return true;
        } else {
            notifyAll();
            return false;
        }
    }

    public void evictAll() {
        synchronized (this) {
            for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
                RealConnection connection = i.next();
                if (connection.getCount() <= 0) {
                    connection.close();
                    i.remove();
                }
            }
        }
    }

    long cleanup(long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = Long.MIN_VALUE;

        // Find either a connection to evict, or the time that the next eviction is due.
        synchronized (this) {
            for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
                RealConnection connection = i.next();

                // If the connection is in use, keep searching.
                if (connection.getCount() > 0) {
                    inUseConnectionCount++;
                    continue;
                }
                idleConnectionCount++;
                // If the connection is ready to be evicted, we're done.
                long idleDurationNs = now - connection.getIdleAtNanos();
                if (idleDurationNs > longestIdleDurationNs) {
                    longestIdleDurationNs = idleDurationNs;
                    longestIdleConnection = connection;
                }
            }

            if (longestIdleDurationNs >= this.keepAliveDurationNs
                    || idleConnectionCount > this.maxIdleConnections) {
                // We've found a connection to evict. Remove it from the list, then close it below (outside
                // of the synchronized block).
                connections.remove(longestIdleConnection);
            } else if (idleConnectionCount > 0) {
                // A connection will be ready to evict soon.
                return keepAliveDurationNs - longestIdleDurationNs;
            } else if (inUseConnectionCount > 0) {
                // All connections are in use. It'll be at least the keep alive duration 'til we run again.
                return keepAliveDurationNs;
            } else {
                // No connections, idle or in use.
                cleanupRunning = false;
                return -1;
            }
        }
        longestIdleConnection.close();
        return 0;
    }

    private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long waitNanos = cleanup(System.nanoTime());
                if (waitNanos == -1) return;
                if (waitNanos > 0) {
                    long waitMillis = waitNanos / 1000000L;
                    waitNanos -= (waitMillis * 1000000L);
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(waitMillis, (int) waitNanos);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    };
}
