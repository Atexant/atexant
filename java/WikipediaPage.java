
import java.util.ArrayList;


public class WikipediaPage {
    public Integer id = null;
    public String title = "";
    public String rawText = "";
    public boolean isRedirect = false;

    public String toString()
    {
	if (id == null)
	    return "#Empty page#";
	String s = new String();
	s += "Title: ";
	s += title;
	s += "\n";
	s += "Redirect: ";
	s += isRedirect?"true\n":"false\n";
	s += "ID: ";
	s += id;
	s += "\n";
	s += "Content:\n";
	s +=rawText;
	s += "\n";
	return s;
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
