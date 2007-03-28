#!/usr/bin/python

from grammar import parser
from sys import argv
from sys import exit
import simpleparse
import pprint

pp = pprint.PrettyPrinter(indent=2, width=120).pprint

class treeiter:
    def __init__(self, txt):
        (self.valid, self.data, a) = parser.parse(txt, processor=None)
        self.pos = 0
        self.length = len(self.data)
        self.line = 0
        self.prev_f = 0
        self.txt = txt

    def remainder(self):
        return (self.pos < len(self.data))

    def __line_sync(self, f):
        for x in self.txt[self.prev_f:f]:
            if x == '\n':
                self.line += 1
        self.prev_f = f

    def __iter__(self):
        return self

    def next(self):
        self.check()
        d = self.data[self.pos]
        self.__line_sync(d[2])
        self.pos += 1
        return d

    def check(self):
        if self.pos >= len(self.data):
            raise StopIteration

    def peek(self):
        self.check()
        return self.data[self.pos]

def m_root(itr):
    print "root"
    t = itr.next()
    r = globals()['m_'+t[0]](t, itr.txt)
    for (t, s, f, c) in itr:
        # This is an SMX_NODE of some sort
        if t <> 'smx_node': break
        # This is not at the baseline
        if c[0][1] - c[0][2] == 0: break
        m_smx_node(itr, r)
    return r

class m_smx_pi:
    def __init__(self, itm, txt):
        itm = itm[3][0]
        self.txt = eval(txt[itm[1]:itm[2]])

    def __repr__(self):
        return "<m_smx_pi: "+self.txt+">"
        
class m_pi_tag:
    def __init__(self, itm, txt):
        cs = itm[3]
        namer, strr = cs[0], cs[1]
        self.name = txt[namer[1]:namer[2]]
        self.txt = eval(txt[strr[1]:strr[2]])

    def __repr__(self):
        return "<m_pi_tag: ("+self.name+") "+self.txt+">"

def m_star_tag(itm):
    print "star_tag"
    return itm

class children(object):
    def __init__(self, itm, txt):
        self.children = []
        super(children, self).__init__(itm, txt)

    def deepest_child(self, i=0):
        deep = (i, self)
        for x in children:
            t = x.deepest_child(i+1)
            if t[0] > deep[0]:
                deep = t
        return deep

class attributes(object):
    def __init__(self, itm, txt):
        self.attributes = []
        super(attributes, self).__init__(itm, txt)

class m_name_tag(children, attributes):
    def __init__(self, itm, txt):
        (n, a, b, cs) = itm
        root_tag = cs[0]
        self.cur = 0
        if root_tag[3][self.cur] == 'ns':
            self.ns = excise(txt, root_tag[3][self.cur]); self.cur += 1
            self.name = excise(txt, root_tag[3][self.cur]); self.cur += 1

        super(m_name_tag, self).__init__(root_tag, txt)
    
class m_name_tag1:
    def __init__(self, itm, txt):
        pass

def m_smx_node(itr):
    print "node"
    try:
        itr.next()
    except StopIteration, x:
        pass

def cur_level():
    global level_stack
    return level_stack[-1][1]

def cur_txt_level():
    global level_stack
    return level_stack[-1][1]

def m_level(lvl, line):
    global level_stack
    (n, s, f, c) = lvl
    dpth = f-s;
    if dpth > cur_txt_level():
        level_stack += [(dpth, cur_level()+1)]
    elif dpth < cur_txt_level():
        tmp = level_stack
        while len(tmp) > 0 and dpth < tmp[-1][0]:
            del tmp[-1]
        if len(tmp) == 0 or dpth <> tmp[-1][0]:
            print "\nIndentation error in", argv[1]+":"+str(line)
            print "unindent does not match any outer indentation level"
            exit()
        else:
            level_stack = tmp

def parse(sdata):
    smx_doc = [None, [], None]
    try:
        itr = treeiter(sdata)
        if itr.valid == 1:
            # Go through the tags until you hit "root"
            if itr.peek()[0].lower() == "smx_pi":
                smx_doc[0] = m_smx_pi(itr.next(), sdata)

            while itr.peek()[0].lower() == "pi_tag":
                smx_doc[1] += [m_pi_tag(itr.next(), sdata)]

            smx_doc[2] = m_root(itr)
            if itr.remainder():
                print "Error on line"+str(itr.line)
                print "SMX must have only one root element"
                exit()
        else:
            print "bad parse :-("
    except simpleparse.error.ParserSyntaxError, x:
        print x
    return smx_doc

if __name__ == "__main__":
    sdata = open(argv[1]).read()
    smx_doc = parse(sdata)
    pp(smx_doc)
