//CSCI-468 Compilers Part 1
//LCK


//Imports for antlr and java libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;
import java.nio.*;

public class Driver {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {

            //gets file name from user
            FileInputStream file = new FileInputStream(args[0]);

            //creates antlr instances.
            //.jar antlr file must be in the classpath for this to compile
            CharStream stream = new ANTLRInputStream(file);
            LITTLELexer lexer = new LITTLELexer(stream);
            Vocabulary vocab = lexer.getVocabulary();
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {

              //prints in the format described by instructions
              System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
              System.out.println("Value: " + token.getText());

                //goes through tokens until there are no more
                token = lexer.nextToken();
            }
        } catch (Exception e) {
            // catch to handle file not found or lexing error
            System.out.println("Lexing failed: " + e);
        }
    }

}
