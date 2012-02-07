package ru.tsu.inf.atexant.storages;

import ru.tsu.inf.atexant.WikipediaPage;

import java.sql.ResultSet;
import java.lang.Iterable;
import java.sql.SQLException;
import java.util.Iterator;


public class MysqlWikipediaPageStorage extends WikipediaPageStorage {
    
    private MySQLAccess db = null;
    public MysqlWikipediaPageStorage() throws Exception {
        db = MySQLAccess.createInstance();
    }
    
    @Override
    public void savePage(WikipediaPage page) throws Exception {
        
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
        } catch (SQLException e) {
            throw new WikipediaPageStorageException(e.getMessage()); 
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
            try {
                result.first();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            final ResultSet res = result;
            return new Iterator<WikipediaPage>() {
                private Boolean isFirst = true;
                @Override
                public boolean hasNext() {
                    try {
                        if (isFirst) {
                            isFirst = false;
                            return res.next();
                        }
                        return !res.isLast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public WikipediaPage next() {
                    try {
                        WikipediaPage ret = buildPageByResultSet(res);
                        res.next();
                        
                        return ret;
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
    
    private Iterable< WikipediaPage > getAllBySql(String sqlPart, String[] params) throws Exception {
        
        String sql = "SELECT id, title, redirect_page_title, redirect_page_id, file_offset  FROM wiki_pages " + sqlPart;
        
        ResultSet set = null;
        
        try {
        
            set = db.executeSql(sql, params);
            
        } catch (SQLException e) {
            throw new WikipediaPageStorageException(e.getMessage());
        }
        
        return new MysqlResultIterable(set);

    }
    
    private WikipediaPage getOneBySql(String sql, String[] params) throws Exception {
        Iterator< WikipediaPage > it = getAllBySql(sql, params).iterator();
        
        if (!it.hasNext()) {
            throw new WikipediaPageStorageException("");
        }
        
        return it.next();
    }
    
    @Override
    public WikipediaPage findById(int id) throws Exception {
        return getOneBySql("WHERE id = ?", new String[]{ new Integer(id).toString() });
    }
    
    @Override
    public WikipediaPage findByTitle(String title) throws Exception {
        return getOneBySql("WHERE title = ?", new String[]{ title });
    }

    @Override
    public Iterable<WikipediaPage> getAll() throws Exception {
        return getAllBySql("", null);
    }
}
