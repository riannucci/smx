CP = -cp .:classes:libs/antlr-runtime-3.0.1.jar:libs/xerces.jar:libs/antlr-3.0.1.jar:libs/stringtemplate-3.1b1.jar:libs/antlr-2.7.7.jar

all: build run

build:
	java $(CP) org.antlr.Tool smx.g
	javac $(CP) tests/test.java smx*.java -d classes

run:
	java $(CP) test 
