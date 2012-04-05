package ru.tsu.inf.atexant;
import ru.tsu.inf.atexant.dump.WikipediaPageHandler;
import ru.tsu.inf.atexant.nlp.CoreNLPAccess;
import ru.tsu.inf.atexant.text.WikiTextParser;
import ru.tsu.inf.atexant.search.WordsMentionsStorage;

public class WordsMentionsPersisterWikipediaPageHandler extends WikipediaPageHandler {
    public WikiTextParser parser = null;
    public CoreNLPAccess nlp = null;
    public WordsMentionsStorage storage = null;

    public WordsMentionsPersisterWikipediaPageHandler( WordsMentionsStorage st) {
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
