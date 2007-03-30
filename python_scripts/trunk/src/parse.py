#!/usr/bin/python

from grammar import parser
import simpleparse
from simpleparse.processor import Processor
from simpleparse.stt.TextTools import TextTools
from sys import exit

class smxProcessor(Processor):
    def EOLMethod(self, taglist, txt, s, f, sub):
        self.line += 1
    
    def stringMethod(self, taglist, txt, s, f, sub):
        taglist += [('str',eval(txt[s:f]))]

    def textMethod(self, taglist, txt, s, f, sub):
        taglist += [('text', txt[s:f])]

    def commentMethod(self, taglist, txt, s, f, sub):
        txt = txt[s:f].strip()
        taglist += [('comment', txt[1:].strip())]

    def attributeMethod(self, taglist, txt, s, f, sub):
        taglist += [('attribute', sub[0], sub[1])]

    def ns_nameMethod(self, taglist, txt, s, f, sub):
        taglist += [('ns_name', sub[0], sub[1])]

    def ns_wild_nameMethod(self, taglist, txt, s, f, sub):
        taglist += [('ns_wild_name', sub[0], sub[1])]

    def levelMethod(self, taglist, txt, s, f, sub):
        lvl_stk = self.level_stack
        depth = f - s
        if depth > lvl_stk[-1]:
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
                print "\nIndentation error in", argv[1]+":"+str(self.line)
                print "unindent does not match any outer indentation level"
                exit()
        elif self.close:
            while len(lvl_stk) > 0 and depth == lvl_stk[-2]:
                taglist += ['dedent']
                del lvl_stk[-1]
        self.close = False

    def tailMethod(self, taglist, txt, s, f, sub):
        if s <> f:
            if sub[0][0] <> 'comment':
                taglist += ['indent']
                taglist += sub
                taglist += ['dedent']
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

        # now do magic
        taglist += [('body',s,f,sub)]

    def name1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('name', taglist, txt, s, f, sub)

    def star1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('star', taglist, txt, s, f, sub)

    def el1Method(self, taglist, txt, s, f, sub):
        self.tag1Method('el', taglist, txt, s, f, sub)

    def tag1Method(self, typ, taglist, txt, s, f, sub):
        taglist += [(typ+'_tag1', sub)]

    def closerMethod(self, taglist, txt, s, f, sub):
        taglist += [('closer', txt[s:f])]

    def smx_piMethod(self, taglist, txt, s, f, sub):
        taglist += [('smx_pi',sub[0][1])]

    def pi_tagMethod(self, taglist, txt, s, f, sub):
        taglist += [('smx_pi',sub[0], sub[1][1])]

    def preambleMethod(self, taglist, txt, s, f, sub):
        taglist += [('preamble',sub)]

    level_stack = [0]
    line = 0
    close = False

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
        rep = parser.parse(str, processor = smxProcessor())
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
