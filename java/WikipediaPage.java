
import java.util.ArrayList;


public class WikipediaPage {
    public Integer id;
    public String title;
    public String rawText;
    public boolean isRedirect;
    
    public WikipediaPage() {
        id = null;
        title = null;
        rawText = null;
        isRedirect = false;
    }
    
    public ArrayList< String > getLinks() {
        ArrayList< String > result = new ArrayList<String>();
        
        int curIndex = 0;

        while (curIndex < rawText.length()) {
            curIndex = rawText.indexOf("[[", curIndex);

            if (curIndex < 0) {
                break;
            }

            int nextIndex = Math.min(rawText.indexOf("|", curIndex), rawText.indexOf("]]", curIndex));

            if (nextIndex < 0) {
                break;
            }

            String link = rawText.substring(curIndex+2, nextIndex);
            
            result.add(link);

            curIndex = nextIndex;
        }        
        
        return result;
    }
}
