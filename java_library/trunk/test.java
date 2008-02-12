import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.*;
import org.apache.xml.serialize.*;

public class test {

    public static void main(String args[]) throws Exception {
        System.out.println("Lexing");
        smxLexer lex = new smxLexer(new ANTLRFileStream("tst_input.txt"));
        System.out.println("Tokenizing");
        CommonTokenStream tokens = new CommonTokenStream(lex);

        System.out.println("Creating Parser");
        smxParser g = new smxParser(tokens);
        Document d = null;

        try {
            System.out.println("Parsing");
            d = g.smx(new DocumentBuilderFactoryImpl()).retdoc;
        } catch (RecognitionException e) {
            e.printStackTrace();
        }


      System.out.println("XML Ouput: \n\n\n<!--Begin-->");

      OutputFormat format = new OutputFormat(d);
      format.setIndenting(true);
      XMLSerializer serializer = new XMLSerializer(System.out, format);
      serializer.serialize(d);

      System.out.println("\n<!--End-->");
    }
}

