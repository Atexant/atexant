package ru.tsu.inf.atexant.dump;

import ru.tsu.inf.atexant.WikipediaPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

public class WikipediaParser extends DefaultHandler 
{
    private boolean isInPage;
    private boolean isInTitle;
    private boolean isInId;
    private boolean isInText;
    private String nodeBuffer;
    private Stack< String > elementsStack;
    private AbstractWikipediaPageHandler pageHandler;

    private WikipediaPage currentPage;
    private FileChannel currentChannel =  null;

    public WikipediaParser(AbstractWikipediaPageHandler handler, FileChannel channel) 
    {
        isInPage = false;
        isInTitle = false;
        isInId = false;
        isInText = false;
        nodeBuffer = null;
        elementsStack = new Stack< String >();
        pageHandler = handler;
        currentChannel = channel;
    }

    private long getCurrentOffset() 
    {
        try {
            //current real position minus approximate buffer length
            return Math.max(currentChannel.position() - (long)(1<<14), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
    {
        raw_name = raw_name.toLowerCase();
        elementsStack.push(raw_name.toLowerCase());
        nodeBuffer = "";
        if (raw_name.equals("page")) 
	{
            isInPage = true;
            currentPage = new WikipediaPage();
            currentPage.offset = getCurrentOffset() - "<page>".length();
        }
        if (raw_name.equals("title")) 
	{
            isInTitle = true;
        }
        if (raw_name.equals("id")) 
	{
            isInId = true;
        }
        if (raw_name.equals("text")) 
	{
            isInText = true;
        }
        if (raw_name.equals("redirect")) 
	{
	    currentPage.isRedirect = true;
        }
    }

    @Override
    public void endElement(String uri, String local_name, String raw_name) throws SAXException 
    {
        elementsStack.pop();
        raw_name = raw_name.toLowerCase();
        if (raw_name.equals("page")) 
	{
            isInPage = false;
            handlePage();
        }
        if (raw_name.equals("title")) 
	{
            isInTitle = false;
            if (isParentNodePage()) 
	    {
                currentPage.title = nodeBuffer;
            }
        }
        if (raw_name.equals("id")) 
	{
            isInId = false;
            if (isParentNodePage()) {
                currentPage.id = Integer.parseInt(nodeBuffer);
            }
        }
        if (raw_name.equals("text")) 
	{
            isInText = false;
            if (isParentNodeEqual("revision")) {
                currentPage.rawText = nodeBuffer;
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) 
    {
        if (needHandleCharacters()) 
	{
            nodeBuffer = nodeBuffer.concat(new String(ch, start, length));
        }
    }

    private boolean needHandleCharacters() 
    {
        return isInTitle || isInId  || isInText;
    }

    private boolean isParentNodeEqual(String tag) 
    {
        return !elementsStack.empty() && elementsStack.peek().equals(tag);
    }

    private boolean isParentNodePage() 
    {
        return isParentNodeEqual("page");
    }

    public void handlePage() 
    {
        pageHandler.handle(currentPage);
    }

    public static org.xml.sax.XMLReader makeXMLReader() throws Exception 
    { 
        final javax.xml.parsers.SAXParserFactory saxParserFactory   =  
        javax.xml.parsers.SAXParserFactory.newInstance(); 
        final javax.xml.parsers.SAXParser        saxParser = saxParserFactory.newSAXParser(); 
        final org.xml.sax.XMLReader              parser    = saxParser.getXMLReader(); 
        return parser; 
    }

    public static DefaultHandler newInstance(AbstractWikipediaPageHandler handler, FileChannel fc) 
    {
        return new WikipediaParser(handler, fc);
    }

    private static class FakeRootInputStream extends InputStream 
    {
        private InputStream stream;
        int readBytes = 0;
        String tag = "<root>";
        public FakeRootInputStream(InputStream is) {
            stream = is;
        }
        @Override
	public int read() throws IOException {
            if (readBytes < tag.length()) {
                return tag.charAt(readBytes++);
            }

            if (readBytes == tag.length() && tag.equalsIgnoreCase("</root>")) {
                return -1;
            }

            int ch = stream.read();
            if (ch == -1) 
	    {
                tag = "</root>";
                readBytes = 0;
                return read();
            }
            return ch;
        }
    }

    public static void parse(String filename, AbstractWikipediaPageHandler handler, long offset) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        InputStream is = fis;
        
        if (offset > 0) {
            fis.skip(offset);

            //reading until closest page-tag closing
            while (true) {
                int justRead = fis.read();

                if (justRead == -1) {
                    return;
                }

                if (justRead == '<' && fis.read() == '/') {
                    byte[] tag = new byte[5];
                    fis.read(tag);
                    if ("page>".equalsIgnoreCase(new String(tag))) {
                        break;
                    }
                }
            }
            
            is = new FakeRootInputStream(is);
        }
        
        XMLReader reader = makeXMLReader();
        
        reader.setContentHandler(newInstance(handler, fis.getChannel()));
       
        reader.parse(new InputSource(is));
    }
    
    public static void parse(String filename, AbstractWikipediaPageHandler handler) throws Exception {
        WikipediaParser.parse(filename, handler, 0);
    }

}
