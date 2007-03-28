#!/usr/bin/python

from grammar import parser
from sys import argv
from sys import exit
import simpleparse

level_stack = [(0, 0)]

line = 0


def p_star_tag(s, f, children, str):
    print "starTag!", str[s:f],

def p_level(lvl):
    global level_stack
    global line
    (n, s, f, c) = lvl
    dpth = f-s;
    if dpth > level_stack[-1][0]:
        level_stack += [(dpth, level_stack[-1][1]+1)]
    elif dpth < level_stack[-1][0]:
        tmp = level_stack
        while len(tmp) > 0 and dpth < tmp[-1][0]:
            del tmp[-1]
        if len(tmp) == 0 or dpth <> tmp[-1][0]:
            print "\nIndentation error in", argv[1]+":"+str(line)
            print "unindent does not match any outer indentation level"
            exit()
        else:
            level_stack = tmp

    print level_stack[-1][1], " ",
    return f

def p_smx_node(s, f, children, str):
    print "smxNode!", " ",
    e = p_level(children[0])
    print str[e:f],


def p_eof(*a) :
    print "EOF"

if __name__ == "__main__":
    ex2 = open(argv[1]).read()
    try:
        ex2p = parser.parse(ex2, processor=None)
        if ex2p[0] == 1:
            prev_f = 0 # This is to ensure that 'skipped' newlines get counted
            for (tag_type, s, f, children) in ex2p[1]:
                for c in ex2[prev_f:f]:
                    if c == '\n': line += 1
                prev_f = f
                locals()["p_"+tag_type.lower()](s, f, children, ex2)
        else:
            print "bad parse :-("

    except simpleparse.error.ParserSyntaxError, x:
        print x


