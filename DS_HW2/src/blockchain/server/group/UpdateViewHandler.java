package blockchain.server.group;

import blockchain.server.model.SupplyChainMessage;
import blockchain.server.model.SupplyChainView;

public class UpdateViewHandler extends Thread {
	private SupplyChainView view;
	private SupplyChainMessage message;
	
	public UpdateViewHandler(SupplyChainView view, SupplyChainMessage message) {
		this.view = view;
		this.message = message;
	}
	
    public void run() {
    	while (message.getBlock().getDepth() != view.getKnownBlocksDepth() + 1) {
			try {
				view.wait();
			} catch (InterruptedException e) {}
    	}
    	
    	message.getBlock().applyTransactions(view);
    	view.addToBlockChain(message.getBlock());
    	view.notifyAll();
    }
}
