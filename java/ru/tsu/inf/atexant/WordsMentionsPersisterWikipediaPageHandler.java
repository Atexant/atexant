package ru.tsu.inf.atexant;
import ru.tsu.inf.atexant.dump.AbstractWikipediaPageHandler;
import ru.tsu.inf.atexant.nlp.CoreNLPAccess;
import ru.tsu.inf.atexant.text.WikiTextParser;
import ru.tsu.inf.atexant.search.AbstractWordsMentionsStorage;

public class WordsMentionsPersisterWikipediaPageHandler extends AbstractWikipediaPageHandler {
    public WikiTextParser parser = null;
    public CoreNLPAccess nlp = null;
    public AbstractWordsMentionsStorage storage = null;

    public WordsMentionsPersisterWikipediaPageHandler( AbstractWordsMentionsStorage st) {
        parser = WikiTextParser.getInstance();
        nlp = CoreNLPAccess.getInstance();
        storage = st;
    }
    
    
    
    @Override
    public void handle(WikipediaPage page) {
        if (page.isRedirect) {
            return;
        }
        for ( String word : nlp.getNormalizedWordsFromPiecesOfText(parser.getUsefullPiecesOfText(page))) {
            storage.persistMention(word, page.id);
        }
    }
    
}
