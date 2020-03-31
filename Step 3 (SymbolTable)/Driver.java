//CSCI-468 Compilers Part 3
//LCK

//Imports for antlr and java libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.*;
import java.util.*;
import java.nio.*;
import java.util.Scanner;

public class Driver {

    static Stack<SymbolTable> symbolTables; //for printing a valid set of tables

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

        /*
        Code from step 2
        //stops errors from printing to the console
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.program();

        boolean accepted = false;
        if (parser.getNumberOfSyntaxErrors() == 0) {
            accepted = true;
        }
        //System.out.println(errors); //test print to see if it is printing correct # of errors
        //Prints for Step 2
        if (accepted) {
            System.out.println("Accepted");
        } else {
            System.out.println("Not accepted");
        }
        */

        Listener listener = new Listener(); //creates new listener to be used.
        ParseTreeWalker walker = new ParseTreeWalker(); //creates walker
        walker.walk(listener, parser.program()); //walker walks the program to
        symbolTables = new Stack<>(); //new stack of symbol tables
        symbolTables = listener.getSymbolTables();
        SymbolTable.prettyPrint(symbolTables); //prints table
    }

}
