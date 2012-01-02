

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AtexantApp
{
    
    public static Properties localProps;
    
    public static void main(String[] args) throws Exception
    {
        localProps = new Properties();
        
        try {
            localProps.load(new FileInputStream("local.properties"));
        } catch(IOException e) {          
            System.out.println("File local.properties should be created. See in _local.properties");
            e.printStackTrace();
            System.exit(1);
        }
        
        if (args.length < 1) {
            return;
        }
        
        if (args[0].equalsIgnoreCase("saveInDb")) {
            saveAllPagesInDb(args[1]);
            return;
        }
        
        if (args[0].equalsIgnoreCase("processRedirects")) {
            processRedirectsInDb();
            return;
        }
        
        if (args[0].equalsIgnoreCase("processLinks")) {
            processLinksInDb();
            return;
        }

        if (args[0].equalsIgnoreCase("debug")) {
	    wikiDebug(args[1]);
	    return;
	}

	return;
    }
    
    public static void processRedirectsInDb() throws Exception {
        int threadsNum = 10;
        Thread[] processors = new Thread[threadsNum];        
        final BlockingQueue< WikipediaPage > q = new LinkedBlockingQueue<WikipediaPage>();
        
        for (int i = 0; i < threadsNum; i++) {
            processors[i] = new Thread(new Runnable() {

                @Override
                public void run()  {
                    MySQLAccess db = null;                   
                    try {
                        db = MySQLAccess.createInstance();
                        
                        while (true) {
                            WikipediaPage page = q.take();
                            if (page.id == -1) {
                                break;
                            }
                            
                            int baseNameStarts = page.rawText.indexOf("[[");
                            int baseNameEnds = page.rawText.indexOf("]]");
                            
                            if (baseNameStarts == -1 || baseNameEnds == -1) {
                                continue;
                            }
                            
                            String baseName = page.rawText.substring(baseNameStarts+2, baseNameEnds);
                            
                            String params[] = new String[1];
                            params[0] = baseName;
                            ResultSet res = null;
                            try {
                                res = db.executeSql("SELECT id FROM wiki_pages WHERE title = ? and is_redirect = 0", params);
                                
                                if (res.next()) {
                                    Integer id = res.getInt(1);
                                    
                                    params = new String[2];
                                    params[0] = page.id.toString();
                                    params[1] = id.toString();
                                    db.executeSql("INSERT INTO wiki_redirects (id, page_id) VALUES(?, ?)", params, true);
                                }                            
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    } finally {
                        if (db!= null) {
                            db.close();
                        }
                    }
                    
                }
            });
            
            processors[i].start();
        }
       
        
        ResultSet redirects = MySQLAccess.getInstance().executeSql("SELECT id, raw_text FROM wiki_pages WHERE is_redirect = 1");
        
        while (redirects.next()) {
            WikipediaPage p = new WikipediaPage();
            
            p.id = redirects.getInt(1);
            p.rawText = redirects.getString(2);
            
            q.put(p);
        }
        
        for (int i = 0; i < threadsNum; i++) {
            WikipediaPage p = new WikipediaPage();
            p.id = -1;
            q.put(p);
        }
    }
    
    public static void saveAllPagesInDb(String filename) throws Exception {
        final BlockingQueue< WikipediaPage > q = new LinkedBlockingQueue<WikipediaPage>();
        
        int threadsNum = 5;
        
        for (int i = 0; i < threadsNum; i++) {
            new Thread(new WikipediaPageConsumer(q)).start();
        }
        
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

        for (int i = 0; i < threadsNum; i++) {
            WikipediaPage p = new WikipediaPage();
            p.id = -1;
            q.put(p);
        }
        
    }
    
    public static void processLinksInDb() throws Exception {
        final BlockingQueue< WikipediaPage > q = new LinkedBlockingQueue<WikipediaPage>();
        
        int threadsNum = 5;
        
        for (int i = 0; i < threadsNum; i++) {
            new Thread(new Runnable() {

                @Override
                public void run()  {
                    MySQLAccess db = null;                   
                    try {
                        db = MySQLAccess.createInstance();
                        
                        while (true) {
                            WikipediaPage page = q.take();
                            
                            if (page.id == -1) {
                                break;
                            }
                            
                            ArrayList< String > links = WikiTextParser.extractLinks(page.rawText);
                            
                           
                            for(String link : links) {                     
                                try {
                                    String[] params = new String[1];
                                    params[0] = link;
                                    
                                    ResultSet res = db.executeSql("SELECT id FROM wiki_pages WHERE title = ? and is_redirect = 0", params);

                                    if (res.next()) {
                                        Integer id = res.getInt(1);

                                        params = new String[2];
                                        params[0] = page.id.toString();
                                        params[1] = id.toString();
                                        db.executeSql("INSERT INTO wiki_links (id, page_id) VALUES(?, ?)", params, true);
                                    }  
                                } catch (Exception e) {
                                    if (!e.getClass().toString().contains("MySQLIntegrityConstraintViolationException")) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                                
                        }
                    } catch (InterruptedException e) {
                        
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        
                    } finally {
                        if (db!= null) {
                            db.close();
                        }
                    }
                    
                }
            }).start();
        }
        
        ResultSet pages = MySQLAccess.getInstance().executeSql("SELECT id, raw_text FROM wiki_pages WHERE is_redirect = 0");
        
        while (pages.next()) {
            WikipediaPage p = new WikipediaPage();
            
            p.id = pages.getInt(1);
            p.rawText = pages.getString(2);
            
            q.put(p);
        }      
        
        for (int i = 0; i < threadsNum; i++) {
            WikipediaPage p = new WikipediaPage();
            p.id = -1;
            q.put(p);
        }
    }

    private static void wikiDebug(String fileName) throws Exception
    {
	WikipediaParser.parse(fileName, new WikipediaPageHandler(){
		private int count = 0;

		public void handle(WikipediaPage page)
		{
		    System.out.println("" + page);
		    count++;
		    if (count > 100)
			System.exit(0);
		}
	    });
    }
}
