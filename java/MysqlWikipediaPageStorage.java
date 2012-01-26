
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class MysqlWikipediaPageStorage extends WikipediaPageStorage {
    
    private MySQLAccess db = null;
    public MysqlWikipediaPageStorage() throws Exception {
        db = MySQLAccess.createInstance();
    }
    
    @Override
    public void savePage(WikipediaPage page) {
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
    
    private List< WikipediaPage > getAllBySql(String sqlPart) throws Exception {
        List< WikipediaPage > result = new ArrayList<WikipediaPage>();
        
        String sql = "SELECT id, title, is_redirect, raw_text FROM wiki_pages " + sqlPart;
        
        ResultSet set = db.executeSql(sql);
        
        while (set.next()) {
            WikipediaPage page = buildNew();
            page.id = set.getInt(1);
            page.title = set.getString(2);
            page.isRedirect = set.getBoolean(3);
            page.rawText = set.getString(4);
            
            result.add(page);
        }
        
        return result;

    }
    
    private WikipediaPage getOneBySql(String sql) throws Exception {
        return getAllBySql(sql).get(0);
    }
    
    @Override
    public WikipediaPage findById(int id) throws Exception {
        return getOneBySql("WHERE id = " + new Integer(id).toString() );
    }
    
    @Override
    public WikipediaPage findByTitle(String title) throws Exception {
        return getOneBySql("WHERE title = '" + title + "'");
    }

    @Override
    public List<WikipediaPage> getAll() throws Exception {
        return getAllBySql("");
    }

    
    
}
