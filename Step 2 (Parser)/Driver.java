//CSCI-468 Compilers Part 1
//LCK


//Imports for antlr and java libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.*;
import java.util.*;
import java.nio.*;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);

            //gets file name from user
            FileInputStream file = new FileInputStream(args[0]);

            /* Code from step 1
            Vocabulary vocab = lexer.getVocabulary();
            Token token = lexer.nextToken();

            while (token.getType() != Token.EOF) {

              //prints in the format described by instructions
              System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
              System.out.println("Value: " + token.getText());

                //goes through tokens until there are no more
                token = lexer.nextToken();
            }
            */

            //creates antlr instances.
            //.jar antlr file must be in the classpath for this to compile
            CharStream stream = new ANTLRInputStream(file);
            LITTLELexer lexer = new LITTLELexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LITTLEParser parser = new LITTLEParser(tokens);
            parser.removeErrorListeners();
            parser.program();
            int errors = parser.getNumberOfSyntaxErrors();
            //System.out.println(errors); //test print to see if it is printing correct # of errors
            //Prints for Step 2
            if(errors == 0) {
                System.out.println("Accepted");
            }
            else {
                System.out.println("Not accepted");
            }
        }
}
