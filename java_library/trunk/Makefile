HOME = /Users/robertiannucci
CP = .:classes:$(HOME)/antlr/src/antlr-3.0.1/lib/antlr-runtime-3.0.1.jar:$(HOME)/xerces/xerces-1_4_4/xerces.jar

all: build run

build:
	javac -cp $(CP) test.java smx*.java -d classes

run:
	java -cp $(CP) test 
