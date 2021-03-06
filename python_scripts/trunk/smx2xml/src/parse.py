#!/usr/bin/python

from grammar import parser
import simpleparse
from simpleparse.processor import Processor
from simpleparse.stt.TextTools import TextTools
from sys import exit
import re

class IndentError:
    pass

class smxProcessor(Processor):
    def EOLMethod(self, taglist, txt, s, f, sub):
        self.line += 1
    
    def stringMethod(self, taglist, txt, s, f, sub):
        s = eval(txt[s:f])
        self.line += len(re.findall("\r\n|\r|\n", s))
        taglist += [('str', s)]

    def textMethod(self, taglist, txt, s, f, sub):
        taglist += [('text', txt[s:f])]

    def commentMethod(self, taglist, txt, s, f, sub):
        txt = txt[s:f].strip()
        taglist += [('comment', txt[1:].strip())]

    def attributeMethod(self, taglist, txt, s, f, sub):
        name = (sub[0][1], sub[0][2])
        value = sub[1][1]
        taglist += [(name, value)]

    def ns_nameMethod(self, taglist, txt, s, f, sub):
        taglist += [('ns_name', sub[0], sub[1])]

    def ns_wild_nameMethod(self, taglist, txt, s, f, sub):
        taglist += [('ns_wild_name', 
                sub[0] if sub[1] <> ':' else '',
                sub[1] if sub[1] <> '*' else '')]

    def levelMethod(self, taglist, txt, s, f, sub):
        lvl_stk = self.level_stack
        depth = f - s
        if lvl_stk[-1] == 0:
            self.oneline = False
        if depth > lvl_stk[-1]:
            if self.oneline:
                print "Indentation error on line",self.line
                print "Unexpected Indent after oneline tag"
                exit()
            else:
                lvl_stk += [depth]
                taglist += ['indent']
        elif depth < lvl_stk[-1]:
            while len(lvl_stk) > 0 and depth < lvl_stk[-1]:
                taglist += ['dedent']
                del lvl_stk[-1]
            while len(lvl_stk) > 0 and depth == lvl_stk[-2]:
                taglist += ['dedent']
                del lvl_stk[-1]
            if len(lvl_stk) == 0 or depth <> lvl_stk[-1]:
                print "Indentation error on line",self.line
                print "unindent does not match any outer indentation level"
                exit()                                                           
        elif self.close:
            while len(lvl_stk) > 0 and depth == lvl_stk[-2]:
                taglist += ['dedent']
                del lvl_stk[-1]
        self.close = False
        self.oneline = False

    def tailMethod(self, taglist, txt, s, f, sub):
        if s <> f:
            if sub[0][0] <> 'comment':
                taglist += ['indent']
                taglist += sub
                taglist += ['dedent']
                self.oneline = True
            else:
                taglist += sub

    def tagMethod(self, taglist, txt, s, f, sub):
        extra = sub[1:]
        sub = sub[0][3]
        for x in sub[:-1]:
            taglist += [x, 'indent']
        taglist += [sub[-1]]
        if len(sub) > 1:
            self.level_stack += [self.level_stack[-1]]*(len(sub)-1)
            self.close = True
        taglist += extra

    def bodyMethod(self, taglist, txt, s, f, sub):
        sub += ['dedent']*(len(self.level_stack)-1)
        taglist += [('body',sub)]

    def name1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('name', taglist, txt, s, f, sub)

    def star1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('star', taglist, txt, s, f, sub)

    def el1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('el', taglist, txt, s, f, sub)

    def tag1Method(self, typ, taglist, txt, s, f, sub):
        attrs = []
        nsname = None
        for x in sub:
            if x[0] in ['ns_name', 'ns_wild_name']:
                nsname = (sub[0][1], sub[0][2])
            else:
                attrs += [x]
        taglist += [(typ+'_tag1', nsname, attrs)]

    def closerMethod(self, taglist, txt, s, f, sub):
        taglist += [('closer', txt[s:f])]

    def smx_piMethod(self, taglist, txt, s, f, sub):
        if not self.root_proc:
            print "Recursing parser error in smx pi"
            print "recursive invocations of parser in SMX_PI production."
            exit()
        else:
            pass # recurse into SMX definitions and extract information
        taglist += [('pi_tag', 'smx',sub[0][1])]

    def pi_tagMethod(self, taglist, txt, s, f, sub):
        taglist += [('pi_tag', sub[0], sub[1][1])]
        taglist += sub[2:]
        self.oneline = True

    def preambleMethod(self, taglist, txt, s, f, sub):
        taglist += [('preamble',sub)]

    def c_tagMethod(self, taglist, txt, s, f, sub):
        taglist += [('ctag',sub[0][1])]
        if len(sub) > 1:
            taglist += sub[1:]
        self.oneline = True

    def cust_tagMethod(self, taglist, txt, s, f, sub):
        name = sub[0]
        data = sub[1][0]
        end = sub[2]
        taglist += [('cust_tag', name, data, end)]
        taglist += sub[3:]

    def nameoMethod(self, taglist, txt, s, f, sub):
        if len(sub) == 0:
            taglist += ['']
        else:
            taglist += [sub[0]]
    

    level_stack = [0]
    line = 1
    close = False
    oneline = False
    root_proc = False

    _m_wild     = TextTools.AppendMatch
    _m_nameo    = nameoMethod
    _m_cust_tag = cust_tagMethod
    _m_tag_end = TextTools.AppendMatch
    _m_c_tag  = c_tagMethod
    _m_body   = bodyMethod
    _m_string   = stringMethod
    _m_EOL   = EOLMethod
    _m_text  = textMethod
    _m_level = levelMethod
    _m_ns    = TextTools.AppendMatch
    _m_name    = TextTools.AppendMatch
    _m_el_tag_tail = tailMethod
    _m_star_tag_tail = tailMethod
    _m_ns_name  = ns_nameMethod
    _m_ns_wild_name  = ns_wild_nameMethod
    _m_attribute = attributeMethod
    _m_star_attribute = attributeMethod
    _m_comment = commentMethod
    _m_name_tag = tagMethod
    _m_el_tag = tagMethod
    _m_star_tag = tagMethod
    _m_star_tag1 = star1Method
    _m_el_tag1 = el1Method
    _m_name_tag1 = name1Method
    _m_el_closer = closerMethod
    _m_star_closer = closerMethod
    _m_preamble = preambleMethod
    _m_smx_pi = smx_piMethod
    _m_pi_tag = pi_tagMethod
    

def parse(str):
    try:
        proc = smxProcessor()
        proc.root_proc = True
        rep = parser.parse(str, processor = proc)
    except simpleparse.error.ParserSyntaxError, x:
        print x
        return None

    return rep[1][:-1]

if __name__ == "__main__":
    from sys import argv
    from pprint import PrettyPrinter
    pp = PrettyPrinter(indent=2, width=120).pprint
    ex2 = open(argv[1]).read()
    result = parse(ex2)
    pp(result)
