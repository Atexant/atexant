

import java.util.*;
import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AtexantApp
{

    public static void main(String[] args) throws Exception
    {
        
        saveAllPagesInDb(args[0]);
        
        return;

    }
    
    public static void saveAllPagesInDb(String filename) throws Exception {
        final BlockingQueue< WikipediaPage > q = new LinkedBlockingQueue<WikipediaPage>();
        
        WikipediaPageConsumer consumer = new WikipediaPageConsumer(q);
        
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        
        WikipediaParser.parse(filename, new WikipediaPageHandler() {

            @Override
            public void handle(WikipediaPage page) {
                try {
                    q.put(page);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        consumerThread.interrupt();
    }
}
