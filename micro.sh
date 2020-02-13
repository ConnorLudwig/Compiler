#!/bin/bash
java -cp antlr-4.7.1-complete.jar org.antlr.v4.Tool LITTLE.g4
javac -cp .:./antlr-4.7.1-complete.jar *.java
java -cp .:antlr-4.7.1-complete.jar Driver
