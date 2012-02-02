

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
        
        final AtexantApp app = new AtexantApp();
        
        if (args[0].equalsIgnoreCase("proccessFile")) {
            if (args.length < 3) {
                return;
            }
            
            WikipediaPageHandler handler = null;
            
            String handlerCmd = args[2];
            
            if (handlerCmd.equalsIgnoreCase("saveAllPagesInDb")) {
                handler = new WikipediaPageHandler() {

                    @Override
                    public void handle(WikipediaPage page) {
                        try {
                            if (page.isRedirect) {
                                int baseNameStarts = page.rawText.indexOf("[[");
                                int baseNameEnds = page.rawText.indexOf("]]");

                                if (baseNameStarts != -1 && baseNameEnds != -1) {
                                    String baseName = page.rawText.substring(baseNameStarts+2, baseNameEnds);
                                    page.redirectPageTitle = baseName;
                                }
                            }
                            app.getStorage().savePage(page);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            } else {
                System.out.println("wrong wikipedia page handler (3 parameter)");
                return;
            }
            
            long offset = 0;
            
            if (args.length > 3) {
                offset = Long.parseLong(args[3]);
            }
            
            app.proccessXmlFile(args[1], handler, offset);
            return;
        }
        
        if (args[0].equalsIgnoreCase("processRedirects")) {
            app.processRedirectsInDb();
            return;
        }
        
        if (args[0].equalsIgnoreCase("debug")) {
	    app.wikiDebug(args[1]);
	    return;
	}

	return;
    }
    
    public WikipediaPageStorage getStorage() throws Exception {
        return new MysqlWikipediaPageStorage();
    }
    
    public void processRedirectsInDb() throws Exception {
        WikipediaPageStorage st = getStorage();
        for (WikipediaPage p : st.getAll()) {
            if (!p.isRedirect) {
                continue;
            }
            
            WikipediaPage redirect = st.findByTitle(p.redirectPageTitle);
            
            p.redirectPageId = redirect.id;
            
            st.savePage(p);
        }
    }
    
    public void proccessXmlFile(String filename, WikipediaPageHandler handler, long offset) throws Exception {
        WikipediaParser.parse(filename, handler, offset);
    }
    
    private void wikiDebug(String fileName) throws Exception
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
