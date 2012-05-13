package ru.tsu.inf.atexant.text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.*;
import de.tudarmstadt.ukp.wikipedia.parser.*;
import ru.tsu.inf.atexant.WikipediaPage;

public class WikiTextParser
{
    public static ArrayList< String > extractLinks(String text)
    {
	ArrayList< String > result = new ArrayList<String>();
	int curIndex = 0;
        while (curIndex < text.length()) 
	{
            curIndex = text.indexOf("[[", curIndex);
            if (curIndex < 0)
                break;
            int nextIndex = Math.min(text.indexOf("|", curIndex), text.indexOf("]]", curIndex));
            if (nextIndex < 0) 
                break;
            String link = text.substring(curIndex+2, nextIndex);
            result.add(link);
            curIndex = nextIndex;
        }        
        return result;
    }

    private String cleanText(String text) 
    {
        //sometimes there are left some markup pieces 
        StringBuilder sbMain = new StringBuilder();
        char[] a = text.toCharArray();
        int counter = 0;
        while (counter < text.length() && (a[counter] == '\n' || a[counter] == ' ' || a[counter] == '=' || a[counter] == '|' || (a[counter] >= 'a' && a[counter] <= 'z'))) 
	{
            if (a[counter] >= 'a' && a[counter] <= 'z') 
	    {
                sbMain.append(a[counter]);
            } else {
                sbMain.delete(0, sbMain.length());
            }
            counter++;
        }

        //optimized cleaning from \n
        StringBuilder bd = sbMain;
        for (int i = counter; i < text.length(); i++) 
	{
            if (a[i] != '\n') {
                bd.append(a[i]);
            }
        }
        return bd.toString();
    }

    private ParsedPage buildParsedPage(String wikimediaText) 
    {
        MediaWikiParserFactory pf = new MediaWikiParserFactory();
        pf.setTemplateParserClass(FlushTemplates.class);

        MediaWikiParser parser = pf.createParser();
        ParsedPage p = parser.parse(wikimediaText); 
        return p;
    }

    public String getClearTextOf(WikipediaPage p) 
    {
	return cleanText(buildParsedPage(p.rawText).getText());
    }
    
    public List< String> getUsefullPiecesOfText(String wikimediaText) 
    {
        LinkedList< String > result = new LinkedList<String>();
        ParsedPage p = buildParsedPage(wikimediaText);
        for (Section s : p.getSections() ) 
	{
            String text = s.getText();
            if (s.getTitle() != null) {
                if (s.getTitle().endsWith("eferences") || s.getTitle().endsWith("inks")) {
                    continue;
                }
                result.add(s.getTitle());
                //remove section name from text
                text = text.substring(Math.min(s.getTitle().length() + 1, text.length()));
            }
            result.add(cleanText(text));
        }
        return result;
    }

    public List< String> getUsefullPiecesOfText(WikipediaPage p) {
        List< String > result = getUsefullPiecesOfText(p.rawText);
        result.add(p.title);
        return result;
    }

    public static WikiTextParser getInstance() 
    {
        return new WikiTextParser();
    }
}
