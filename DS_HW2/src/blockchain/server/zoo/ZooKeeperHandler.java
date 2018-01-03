package blockchain.server.zoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
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

	/**
	 * add new block to chain
	 * - verify that the path is to the end of the chain and that it is the smallest son added
	 *
	 *
	 * @param path - path to the last znode in the block chain.
	 * @param data - json string of Block Header (Server Id, Block id).
	 * @param depth - current block chain length
	 *
	 * @return the actual path of the created node
	 */
    public String addBlockToBlockChain(String path, String data, int depth)throws KeeperException, InterruptedException
	{
		/*If has node in current path -> the chain is already longer*/
		if(ZookeeperUtils.hasNextBlock(zk,path))
		{
			return null;
		}

		/*Add the new Znode to chain block*/
		String znodePath = ZookeeperUtils.addPersistentSequentialZNode(zk, path.concat("/" + Integer.toString(depth)), data);

		/*Get all the Znodes sones*/
		String smallestZnodePath = ZookeeperUtils.returnSmallestSonOfZnode(zk, path);

		/*Check if the node added was the smallest one*/
		if (znodePath.equals(smallestZnodePath))
		{
			return znodePath;
		}
		/*Was not the smalles -> remove the son*/
		ZookeeperUtils.removeZNode(zk, znodePath);
		return null;
	}

	/**
	 * add new server zNode
	 * - create EPHEMERAL znode with servers name in "Server" subTree
	 * - this way other servers can now is some server still exists.
	 *
	 *
	 * @param serversName - server name
	 */
	public void addServer(String serversName)throws KeeperException, InterruptedException
	{
			String path = "/Servers";
			String serversPath = path.concat("/" + serversName);
			if (ZookeeperUtils.getAllChildrens(zk, path).contains(serversPath))
			{
				/*Server with same name already in the system - not allowed*/
				assert(false);
			}

			ZookeeperUtils.addEephemeralZNode(zk,serversPath,serversName);
	}

	/**
	 * the function receive apth to znode in the block chain and return the delta from
	 * this path to the end of the block chain
	 *
	 * @param path - server name
	 */
	public String getCahinSuffix(String path, int depth)throws KeeperException, InterruptedException
	{
		String currentPath = path;
		int count=0;
		while(ZookeeperUtils.hasNextBlock(zk, currentPath))
		{
			currentPath = getSmallestZnodeName(ZookeeperUtils.getAllChildrens(zk,currentPath));
			count ++;
		}

		currentPath.replaceFirst(path, "");
		return currentPath;
	}

	/**
	 * the function receive a path to znode in the block chain and return in a list all the
	 * data contained in the next Znode
	 * (Need it in order to have the missing blocks and the servers created them and the depth)
	 *
	 * @param path - server name
	 */
	public List<String> getAllTheNextBlocks(String path, int depth)throws KeeperException, InterruptedException
	{
		List<String> blockList = new ArrayList<>();

		////Continue here

		return blockList;
	}

	private String getSmallestZnodeName(List<String> list)
	{
		assert(!list.isEmpty());
		String small = list.get(0);
		for (String child : list)
		{
			if(small.compareTo(child) > 0)
			{
				small = child;
			}
		}
		return small;

	}


}
