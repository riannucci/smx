#!/usr/bin/python
from parse import parse
from ast import *

def mkpreamble(cst_pre):
    pre = PREAMBLE()

    cst_pre = cst_pre[1]

    if cst_pre:
        if cst_pre[0][1] == 'smx':
            pre.smx_pi = PI_TAG(cst_pre[0], pre)
            del cst_pre[0]
        for x in cst_pre:
            p = PI_TAG(x, pre)
            pre.extra_pi += [p]

    return pre

def strbody(cst_body):
    ret = ""
    stack = []
    lvl = 1
    cur = []
    for x in cst_body[1]:
        if x == 'indent':
            lvl += 1
        elif x == 'dedent':
            lvl -= 1
            ret += tab*lvl + '.\n'
        else:
            ret += tab*lvl + str(x) +"\n"

    return ret

def mkbody(cst_body): 
    ret = []
    cur_open = []
    cur = None
    root = BODY()
    stk = [root]
    for x in cst_body[1]:
        if x == 'indent':
            stk += [cur]
        elif x == 'dedent':
            del stk[-1]
        elif x[0] in ['name_tag1','el_tag1','star_tag1']:
            cur = ELEMENT(x, stk[-1])
            stk[-1].children += [cur]
            cur_open += [cur]
        elif x[0] == 'closer':
            for c in cur_open:
               c.closer = x[1] 
            cur_open = []
        else:
            val = {
            'text':TEXT,
            'comment':COMMENT,
            'ctag':CDATA,
            'pi_tag':PI_TAG,
            'cust_tag':CUST_TAG
            }.get(x[0], lambda *a: x)(x, stk[-1])

            stk[-1].children += [val]

    return root

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
