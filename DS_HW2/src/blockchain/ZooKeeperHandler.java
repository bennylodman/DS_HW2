package blockchain;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;


// this class will handle all work with the zookeeper server
public class ZooKeeperHandler implements Watcher {
	public static String ZK_ADDR = "??";
	public static int ZK_PORT = 000;
	
	private static ZooKeeper zk;
	private static Object mutex;
	
	public ZooKeeperHandler() throws IOException {
		zk = new ZooKeeper(ZK_ADDR, ZK_PORT, this);
		mutex = new Object();
	}
	
    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            mutex.notify();
        }
    }
}
