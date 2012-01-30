

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sufix
 */
public class WikipediaPageConsumer implements Runnable {
    BlockingQueue< WikipediaPage > queue = null;
    WikipediaPageStorage storage = null;
    
    public WikipediaPageConsumer(BlockingQueue< WikipediaPage > q, WikipediaPageStorage st) {
        queue = q;
        storage = st;
    }
    
    public void run() {
       
        while (true) {
            try {
                WikipediaPage page = queue.take();
                
                if (page.id == -1) {
                    break;
                }
                
                handlePage(page);
            } catch(InterruptedException e) {
                break;
            }         
            
        }
    }
    
    public void handlePage(WikipediaPage page) {
        try {
            storage.savePage(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
