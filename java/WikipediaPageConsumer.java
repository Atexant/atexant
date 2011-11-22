

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sufix
 */
public class WikipediaPageConsumer implements Runnable {
    BlockingQueue< WikipediaPage > queue;
    MySQLAccess db;
    public WikipediaPageConsumer(BlockingQueue< WikipediaPage > q) {
        queue = q;
    }
    
    public void run() {
        try {
            db = MySQLAccess.createInstance();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        
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
        String[] params = new String[4];
        params[0] = page.id.toString();
        params[1] = page.title;
        params[2] = page.rawText;
        params[3] = page.isRedirect ? "1" : "0";
        
        try {
            db.executeSql("INSERT INTO wiki_pages (id, title, raw_text, is_redirect) VALUES(?, ?, ?, ?)", params, true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
