package ru.tsu.inf.atexant;

public class WikipediaPage {
    public Integer id = null;
    public String title = "";
    public String rawText = "";
    public boolean isRedirect = false;
    public String redirectPageTitle = null;
    public Integer redirectPageId = null;
    public long offset = -1;
    public boolean isLoadedFromDb = false;

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
}
