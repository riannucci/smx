#!/Library/Frameworks/Python.framework/Versions/Current/bin/python

from grammar import parser
import pprint, sys
from sys import argv
import simpleparse

pp = pprint.PrettyPrinter(indent=2, width=120)

ex2 = open(argv[1]).read()

try:
    ex2p = parser.parse(ex2, processor=None)
    pp.pprint(ex2p)
except simpleparse.error.ParserSyntaxError, x:
    print x


