#!/bin/bash
java -classpath antlr-4.7.1-complete.jar org.antlr.v4.Tool LITTLE.g4
javac -classpath .:./antlr-4.7.1-complete.jar *.java
java -classpath .:antlr-4.7.1-complete.jar Driver $1 > output.txt
