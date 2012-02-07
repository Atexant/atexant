package ru.tsu.inf.atexant.text;

import java.util.ArrayList;

public class WikiTextParser
{
    public static ArrayList< String > extractLinks(String text)
    {
	ArrayList< String > result = new ArrayList<String>();
	int curIndex = 0;
        while (curIndex < text.length()) {
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
}