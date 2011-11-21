

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sufix
 */
public class WikipediaPageConsumer implements Runnable {
    BlockingQueue< WikipediaPage > queue;
    public WikipediaPageConsumer(BlockingQueue< WikipediaPage > q) {
        queue = q;
    }
    
    public void run() {
        while (true) {
            try {
                WikipediaPage page = queue.take();
                handlePage(page);
            } catch(InterruptedException e) {
                break;
            }         
            
        }
    }
    
    public void handlePage(WikipediaPage page) {
        String[] params = new String[4];
        params[0] = page.id.toString();
        params[1] = page.title;
        params[2] = page.rawText;
        params[3] = page.isRedirect ? "1" : "0";
        
        try {
            MySQLAccess.getInstance().executeSql("INSERT INTO wiki_pages (id, title, raw_text, is_redirect) VALUES(?, ?, ?, ?)", params, true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
