#!/usr/bin/python

from grammar import parser
import simpleparse
from simpleparse.processor import Processor
from simpleparse.stt.TextTools import TextTools
from sys import exit

class smxProcessor(Processor):
    def levelMethod(self, taglist, txt, s, f, sub):
        dpth = f-s
        if dpth > self.curdpth():
            self.level_stack += [(dpth, self.curlvl()+1)]
            taglist += ["indent"]
        elif dpth < self.curdpth():
            tmp = self.level_stack
            if len(self.level_pile_stk) <> 0:
                taglist += ['dedent']*self.level_pile_stk[-1]
                del self.level_pile_stk[-1]
            while len(tmp) > 0 and dpth < self.curdpth(tmp):
                del tmp[-1]
                taglist += ['dedent']
            if len(tmp) == 0 or dpth <> self.curdpth(tmp):
                print "\nIndentation error in", argv[1]+":"+str(self.line)
                print "unindent does not match any outer indentation level"
                exit()
            else:
                self.level_stack = tmp  
        elif self.close and len(self.level_pile_stk) <> 0:
            taglist += ['dedent']*self.level_pile_stk[-1]
            del self.level_pile_stk[-1]
        self.close = False

    def curlvl(self, stk=None):
        if stk == None: stk = self.level_stack
        return stk[-1][1]

    def curdpth(self, stk=None):
        if stk == None: stk = self.level_stack
        return stk[-1][0]
    
    def EOLMethod(self, taglist, txt, s, f, sub):
        self.line += 1
    
    def stringMethod(self, taglist, txt, s, f, sub):
        taglist += [('str',eval(txt[s:f]))]

    def textMethod(self, taglist, txt, s, f, sub):
        taglist += [('text', txt[s:f])]

    def bodyMethod(self, taglist, txt, s, f, sub):
        taglist += [('body',s,f,sub)]
        for x in self.level_stack:
            sub += ['dedent']

    def tailMethod(self, taglist, txt, s, f, sub):
        if s <> f:
            if sub[0][0] <> 'comment':
                taglist += ['indent']
                taglist += sub
                taglist += ['dedent']
            else:
                taglist += sub
                if self.close and len(self.level_pile_stk) <> 0:
                    for x in xrange(self.level_pile_stk[-1]):
                        taglist += ['dedent']
                    del self.level_pile_stk[-1]
                
    def attributeMethod(self, taglist, txt, s, f, sub):
        taglist += [('attribute', sub[0], sub[1])]

    def name_tagmMethod(self, taglist, txt, s, f, sub):
        for x in sub[:-1]:
            taglist += [x, 'indent']
        taglist += [sub[-1]]
        if len(sub) > 1:
            self.level_pile_stk += [len(sub)]
            self.close = True
        print self.level_pile_stk

    def ns_nameMethod(self, taglist, txt, s, f, sub):
        taglist += [('ns_name', sub[0], sub[1])]

    close = False
    level_stack = [(0,0)]
    level_pile_stk = []
    line = 0

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
    _m_attribute = attributeMethod
    _m_name_tagm = name_tagmMethod
    _m_el_tagm = name_tagmMethod
    _m_star_tagm = name_tagmMethod
    

def parse(str):
    try:
        rep = parser.parse(ex2, processor = smxProcessor())
    except simpleparse.error.ParserSyntaxError, x:
        print x
        return None

    return rep

if __name__ == "__main__":
    from sys import argv
    from pprint import PrettyPrinter
    pp = PrettyPrinter(indent=2, width=120).pprint
    ex2 = open(argv[1]).read()
    result = parse(ex2)
    pp(result)
