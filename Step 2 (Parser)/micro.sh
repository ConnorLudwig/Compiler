#!/bin/sh
java org.antlr.v4.Tool LITTLE.g4
javac LITTLE*.java
javac Driver.java
java Driver $1
