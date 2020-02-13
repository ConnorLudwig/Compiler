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
            System.out.print("Enter the name of the file you want to test: ");
            String file_name = input.next();
            System.out.println("The file to test is:  " + file_name);

            FileInputStream file = new FileInputStream(file_name);
            CharStream stream = new ANTLRInputStream(file);
            LITTLELexer lexer = new LITTLELexer(stream);
            Vocabulary vocab = lexer.getVocabulary();
            Token token = lexer.nextToken();
            while (token.getType() != Token.EOF) {
              System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
              System.out.println("Value: " + token.getText());
                //System.out.println("Value: " + token.getText());
                token = lexer.nextToken();
            }
        } catch (Exception e) {
            // General catch to handle any faulty lexing
            System.out.println("Lexing failed: " + e);
        }
    }

}
