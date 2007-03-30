#!/usr/bin/python
from parse import parse
from ast import *

def mkpreamble(cst_pre):
    pre = PREAMBLE()

    cst_pre = cst_pre[1]

    if cst_pre:
        if cst_pre[0][0] == 'smx_pi':
            pre.smx_pi = PI_TAG('smx', cst_pre[0][1])
            del cst_pre[0]
        for x in cst_pre:
            p = PI_TAG(x[1], x[2])
            pre.extra_pi += [p]

    return pre

def mkbody(cst_body):
    stack = []
    lvl = 0
    cur = []
    for x in cst_body[1]:
        if x == 'indent':
            lvl += 1
        elif x == 'dedent':
            lvl -= 1
            print '    '*lvl, '.'
        else:
            print '    '*lvl, x

    return None



def mktree(cst):
    smx = SMX()
    smx.preamble = mkpreamble(cst[0])
    smx.body = mkbody(cst[1])

    return smx


if __name__ == "__main__":
    from sys import argv
    from pprint import PrettyPrinter
    pp = PrettyPrinter(indent=2, width=120).pprint
    ex2 = open(argv[1]).read()
    result = parse(ex2)
    result = mktree(result)
    print (result)
