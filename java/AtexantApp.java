import java.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;

public class AtexantApp
{
    public static void main(String[] args)
    {
	System.out.println("Just simple application!!!");
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
        // read some text in the text variable
        String text = "Stanford University is located in California. Beaches. It is a great university.";

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
          // traversing the words in the current sentence
          // a CoreLabel is a CoreMap with additional token-specific methods
          for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            // this is the text of the token
            String word = token.get(TextAnnotation.class);
            // this is the POS tag of the token
            // this is the NER label of the token
            String ne = token.get(LemmaAnnotation.class); 
            System.out.println(word + ' ' + ne);
          }
        }
  
    }
}
