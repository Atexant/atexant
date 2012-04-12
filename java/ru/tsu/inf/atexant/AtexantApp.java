package ru.tsu.inf.atexant;

import java.io.*;
import ru.tsu.inf.atexant.storages.*;
import ru.tsu.inf.atexant.dump.*;
import ru.tsu.inf.atexant.nlp.*;
import ru.tsu.inf.atexant.search.*;

import java.util.*;
import ru.tsu.inf.atexant.nlp.sentences.*;
import ru.tsu.inf.atexant.text.WikiTextParser;


public class AtexantApp
{
    
    public static Properties localProps = new Properties();
    
    private static class WikipediaPageSaver extends WikipediaPageHandler {
        private WikipediaPageStorage storage = null;
        
        public WikipediaPageSaver(WikipediaPageStorage a) {
            storage = a;
        }
        
        @Override
        public void handle(WikipediaPage page) {

            //redirects preprocessing
            if (page.isRedirect) {
                int baseNameStarts = page.rawText.indexOf("[[");
                int baseNameEnds = page.rawText.indexOf("]]");

                if (baseNameStarts != -1 && baseNameEnds != -1) {
                    String baseName = page.rawText.substring(baseNameStarts+2, baseNameEnds);
                    page.redirectPageTitle = baseName;
                }
            }

            try {
                storage.savePage(page);
            } catch (WikipediaPageStorageException e) {
                AtexantApp.log("page "+ page.id.toString() +" was not saved successfully probably because it's already in storage");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static class WikipediaPageSimilaritySentencesAnalyzer extends WikipediaPageHandler {

        @Override
        public void handle(WikipediaPage page) {

            Properties props = new Properties();
            props.setProperty("node_top_weight", "0.65");
            props.setProperty("noun_node_weight", "0.85");
            props.setProperty("semantic_weight", "0.9");
            props.setProperty("e", "7");
            SentencesSimilarityAnalyzer ssa = new SentencesSimilarityAnalyzer();
            ssa.searchSimilarToSampleSentence(WikiTextParser.getInstance().getClearTextOf(page));
        }
        
    }
    
    public static void main(String[] args) throws Exception
    {
              
        try {
            localProps.load(new FileInputStream("local.properties"));
        } catch(IOException e) {          
            System.out.println("File local.properties should be created. See in _local.properties");
            e.printStackTrace();
            System.exit(1);
        }
        
        if (args.length < 1) {
            return;
        }
        
        final AtexantApp app = new AtexantApp();
        
        if (args[0].equalsIgnoreCase("processFile")) {
            if (args.length < 3) {
                return;
            }
            
            WikipediaPageHandler handler = null;
            
            String handlerCmd = args[2];
            
            if (handlerCmd.equalsIgnoreCase("saveAllPagesInDb")) {
                handler = new WikipediaPageSaver(app.getStorage());
            } else if(handlerCmd.equalsIgnoreCase("wordMentions")) {
                handler = new WordsMentionsPersisterWikipediaPageHandler(new FileSystemWordsMentionsStorage());
            } else if (handlerCmd.equalsIgnoreCase("sentenceSimilarity")) {
                
                System.out.print("Page id: ");
                Integer pageId = new Scanner(System.in).nextInt();
                
                WikipediaPageFinder finder = new WikipediaPageFinder(new WikipediaPageSimilaritySentencesAnalyzer());
                finder.setId(pageId);
                handler = finder;
                
            } else {
                System.out.println("wrong wikipedia page handler (3 parameter)");
                return;
            }
            
            long offset = 0;
            
            if (args.length > 3) {
                offset = Long.parseLong(args[3]);
            }
            
            app.proccessXmlFile(args[1], handler, offset);
            return;
        }
        
        if (args[0].equalsIgnoreCase("processRedirects")) {
            app.processRedirectsInDb();
            return;
        }
        
        if (args[0].equalsIgnoreCase("debug")) {
	    app.wikiDebug(args[1]);
	    return;
	}
        
        if (args[0].equalsIgnoreCase("testWordsSimilarity")) {
            app.testWordsSimilarity(args[1]);
            return;
        }
        
        if (args[0].equalsIgnoreCase("testSentencesSimilarity")) {
            app.testWSentencesSimilarity(args[1]);
            return;
        }

        return;
    }
    
    public WikipediaPageStorage getStorage() throws Exception {
        return new MysqlWikipediaPageStorage();
    }
    
    public void processRedirectsInDb() throws Exception {
        WikipediaPageStorage st = getStorage();
        for (WikipediaPage p : st.getAll()) {
            if (!p.isRedirect) {
                continue;
            }
            
            try {
                WikipediaPage redirect = st.findByTitle(p.redirectPageTitle);
                p.redirectPageId = redirect.id;
            } catch (WikipediaPageStorageException e) {
                log("redirected page was not found for " + p.id.toString());
                continue;
            } catch (Exception e) {
                int a = 0;
                e.printStackTrace();
            }
            
            try {
                st.savePage(p);
            } catch (WikipediaPageStorageException e) {
                log("redirecting page" + p.id.toString() + " was not saved successfully");
                continue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void proccessXmlFile(String filename, WikipediaPageHandler handler, long offset) throws Exception {
        WikipediaParser.parse(filename, handler, offset);
    }
    
    public static void log(String message) {
        System.out.println(message);
    }
    
    private void wikiDebug(String fileName) throws Exception
    {
	WikipediaParser.parse(fileName, new WikipediaPageHandler(){
		private int count = 0;

		public void handle(WikipediaPage page)
		{
		    System.out.println("" + page);
		    count++;
		    if (count > 100)
			System.exit(0);
		}
	    });
    }
    
    private class MeasureToken {
        public String first;
        public String second;
        public Double humanSimilarity;
        public Double ourSimilarity = null;

        public MeasureToken(String first, String second, Double humanSimilarity) {
            this.first = first;
            this.second = second;
            this.humanSimilarity = humanSimilarity;
        }
        
        public Double getErr() {
            return Math.abs(this.humanSimilarity - this.ourSimilarity);
        }
    }
    
    private ArrayList< MeasureToken > getMeasureTokens(String tokensFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tokensFileName)));
        ArrayList< MeasureToken > result = new ArrayList< MeasureToken >();
        
        String line;
        while ((line = reader.readLine()) != null) {
            String[] splitted = line.split("\\|");
            MeasureToken token = new MeasureToken(splitted[0], splitted[1], new Double(splitted[2]));
            token.humanSimilarity = token.humanSimilarity/4.0;
            result.add(token);
        }
        
        return result;
    }
    
    public void testWordsSimilarity(String sampleFileName) throws Exception {
        WordSimilarityMeasurer wsm = WordNetSimilarityMeasurer.getInstance();
        
        ArrayList< MeasureToken > testSet = getMeasureTokens(sampleFileName);
        
        System.out.println("              humanSim ourSim");
        
        for (MeasureToken token : testSet) {
            token.ourSimilarity = wsm.getSimilarity(new WordToken(token.first, "NN"), new WordToken(token.second, "NN"));
            System.out.println(token.first + " " + token.second  + " ("+ token.humanSimilarity.toString() + ") (" + token.ourSimilarity.toString() + ") err=" + token.getErr().toString() + "");
        }
        
    }
    
    public void testWSentencesSimilarity(String sampleFileName) throws Exception {
        Properties props = new Properties();
        //α1 , β 1 , λ1 , and e are assigned with 0.65, 0.85, 0.90, 7 respectively.

        props.setProperty("node_top_weight", "0.65");
        props.setProperty("noun_node_weight", "0.85");
        props.setProperty("semantic_weight", "0.9");
        props.setProperty("e", "7");
        SentenceSimilarityMeasurer ssm = new SentenceComplexSimilarityMeasurer(props);
        
        ArrayList< MeasureToken > testSet = getMeasureTokens(sampleFileName);
        
        for (MeasureToken token : testSet) {
            token.ourSimilarity = ssm.getSimilarityOfSentences(token.first, token.second);
            System.out.println(token.first);
            System.out.println(token.second);
            System.out.println("hSim="+token.humanSimilarity.toString() + " ourSim=" +token.ourSimilarity.toString() + " err=" + token.getErr().toString());
            
            System.out.println();
        }
        
    }
}
