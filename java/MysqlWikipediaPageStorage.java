
import java.sql.ResultSet;
import java.lang.Iterable;
import java.util.Iterator;


public class MysqlWikipediaPageStorage extends WikipediaPageStorage {
    
    private MySQLAccess db = null;
    public MysqlWikipediaPageStorage() throws Exception {
        db = MySQLAccess.createInstance();
    }
    
    @Override
    public void savePage(WikipediaPage page) {
        
        try {
            if (!page.isLoadedFromDb) {
                String[] params = new String[5];
                params[0] = page.id.toString();
                params[1] = page.title;
                params[2] = page.redirectPageTitle;
                if (page.redirectPageId != null) {
                    params[3] = page.redirectPageId.toString();
                } else {
                    params[3] = null;
                }
                params[4] = Long.toString(page.offset);
                
                db.executeSql("INSERT INTO wiki_pages (id, title, redirect_page_title, redirect_page_id, file_offset) VALUES(?, ?, ?, ?, ?)", params, true);
            } else {
                String[] params = new String[5];
                params[0] = page.title;
                params[1] = page.redirectPageTitle;
                params[2] = page.redirectPageId.toString();
                params[3] = Long.toString(page.offset);
                params[4] = page.id.toString();
                
                db.executeSql("UPDATE wiki_pages SET title = ?, redirect_page_title = ?, redirect_page_id = ?, file_offset = ? WHERE id = ?", params, true);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }
    
    public static WikipediaPage buildPageByResultSet(ResultSet set) throws Exception {
        WikipediaPage page = buildNew();
        page.id = set.getInt("id");
        page.title = set.getString("title");
        page.redirectPageTitle = set.getString("redirect_page_title");
        page.redirectPageId = set.getInt("redirect_page_id");
        page.offset = set.getLong("file_offset");
        
        page.isRedirect = page.redirectPageTitle != null;
        page.isLoadedFromDb = true;

        return page;
    } 
    
    private class MysqlResultIterable implements Iterable {
        private ResultSet result = null;
        
        public MysqlResultIterable(ResultSet res) {
            result = res;
        }
        
        @Override
        public Iterator<WikipediaPage> iterator() {
            final ResultSet res = result;
            return new Iterator<WikipediaPage>() {
                
                @Override
                public boolean hasNext() {
                    try {
                        return !res.isLast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public WikipediaPage next() {
                    try {
                        res.next();
                        return buildPageByResultSet(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return buildNew();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }       
        
    }
    
    private Iterable< WikipediaPage > getAllBySql(String sqlPart) throws Exception {
        
        String sql = "SELECT id, title, redirect_page_title, redirect_page_id, file_offset  FROM wiki_pages " + sqlPart;
        
        ResultSet set = db.executeSql(sql);
        
        return new MysqlResultIterable(set);

    }
    
    private WikipediaPage getOneBySql(String sql) throws Exception {
        return getAllBySql(sql).iterator().next();
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
    public Iterable<WikipediaPage> getAll() throws Exception {
        return getAllBySql("");
    }

        
}
