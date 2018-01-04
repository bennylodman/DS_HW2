package blockchain.server;

import blockchain.server.group.BlockHandler;
import blockchain.server.model.BlockHeader;
import blockchain.server.model.SupplyChainView;
import com.google.gson.Gson;
import org.apache.zookeeper.KeeperException;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by benny on 04/01/2018.
 */
public class ServerThread extends Thread {

    public run(){
        Gson gson = new Gson();
        BlockHandler blockToAddTheChain;

        while(true)
        {

            /*Close block and open new*/
            synchronized (DsTechShipping.blockHandlerLock)
            {
                blockToAddTheChain = DsTechShipping.blocksHandler;
                DsTechShipping.blocksHandler = new BlockHandler();
            }

            /*If block is empty no job to do*/
            if(blockToAddTheChain.size() == 0)
            {
                continue;
            }


            /*Sleep for 1 second*/
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                assert(false);
            }



        }
    }

    public private all(){

        String path = new String();
        while(true)
        {
            BlockHandler blockToAddTheChain;
            List<String> missingBlockList;


            /*Lock Global view for read - does not change during build of current view*/
            DsTechShipping.view.getRWLock().acquireRead();

            /*Get current system view as this server knows it*/
            SupplyChainView currentView = DsTechShipping.view.getCurrentView();

            /*Release Global view for read - */
            DsTechShipping.view.getRWLock().releaseRead();

            /*Verify that block is legal - after this function need to check that it is not empty*/
            blockToAddTheChain.verifyBlock(currentView);

            /*Check if block empty (All transactions were illegal) -> finish loop and wait for next cycle*/
            if(blockToAddTheChain.size() == 0)
            {
                continue;
            }
            /*Create block header to insert to Znode*/
            BlockHeader blckToZnode = new BlockHeader(currentView.getKnownBlocksDepth(),DsTechShipping.groupServers.getServerName());

            /*Try to add block to the block chain*/
            try {
                path = DsTechShipping.zkHandler.addBlockToBlockChain(currentView.getKnownBlocksPath(), gson.toJson(blckToZnode), currentView.getKnownBlocksDepth());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                assert(false);
            }

            if(path != null)
            {
                /*BlockHeader was added to chain*/

                /*Send to all servers the new block and wait to MaxServersCrushSupport + update yourself*/
                /*TODO: Need to create functions - function returns if got the needed acks - kill all if note */

                /*Wakeup all REST threads and return that trnsactions happens*/
                blockToAddTheChain.notifySuccessToAll();

                /*TODO: Make sure that there is nothing left to do*/
            }else
            {
                /*BlockHeader was not added to chain*/

                /*Need find out what are the missing blocks*/
                try {
                    missingBlockList = DsTechShipping.zkHandler.getAllTheNextBlocks(currentView.getKnownBlocksPath());
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    assert(false);
                }

                /*Request all blocks*/
                /*TODO: create this function - will recieve missingBlockList and
                * loop over every block and ask for it in the network:
                *   if go it will update the view
                *   else (no one has it) will delete it and finish (no reson to check the rest)
                *   - when finished has updated view (for the moment)
                * */

                /*Try again with new depth*/


            }


        }
    }

}
