
import java.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;

class Lemmatisation
{
    private StanfordCoreNLP pipeline ;

    public void init()
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public void process(String text, Map res)
    {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) 
	{
	    // traversing the words in the current sentence
	    // a CoreLabel is a CoreMap with additional token-specific methods
	    for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
		res.put(token.get(TextAnnotation.class), token.get(LemmaAnnotation.class));
        }
    }
}
