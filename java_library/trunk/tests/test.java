import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.*;
import org.apache.xml.serialize.*;

public class test {

    public static void main(String args[]) throws Exception {
        smxLexer lex = new smxLexer(new  ANTLRInputStream(System.in));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        smxParser g = new smxParser(tokens);
        Document d = null;

        try {
            d = g.smx(new DocumentBuilderFactoryImpl()).retdoc;
        } catch (RecognitionException e) {
            e.printStackTrace();
        }


      OutputFormat format = new OutputFormat(d);
      format.setIndenting(true);
      XMLSerializer serializer = new XMLSerializer(System.err, format);
      serializer.serialize(d);
    }
}

