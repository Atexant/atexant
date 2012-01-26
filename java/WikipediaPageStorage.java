import java.util.List;

public abstract class WikipediaPageStorage {
    public abstract void savePage(WikipediaPage p) throws Exception;
    public abstract WikipediaPage findById(int id) throws Exception;    
    public abstract List< WikipediaPage > getAll() throws Exception;
    
    public WikipediaPage findByTitle(String title) throws Exception {
        throw new Exception("undefined behavior");
    }
    
    protected WikipediaPage buildNew() {
        return new WikipediaPage();
    }
}
