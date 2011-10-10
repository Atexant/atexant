
import java.util.*;

public class AtexantApp
{
    public static void main(String[] args)
    {
	if (args.length < 1)
	    return;
	Lemmatisation l = new Lemmatisation();
	l.init();
	System.out.println("Initialization completed!!!");
	HashMap m = new HashMap();
	l.process(args[0], m);
	Iterator it = m.entrySet().iterator();
	while (it.hasNext())
	{
	    Map.Entry e = (Map.Entry)it.next();
	    System.out.println("" + e.getKey() + " " + e.getValue());
	}
    }
}
