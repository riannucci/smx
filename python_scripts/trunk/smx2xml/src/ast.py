tab = '    '

class SMX:
    preamble = None
    body = None

    def __str__(s):
        return str(s.preamble)+str(s.body)

class PREAMBLE:
    smx_pi = None
    extra_pi = []

    def level(s):
        return -1

    def __str__(s):
        ret  = """<?xml version='1.0' encoding='UTF-8' ?>\n"""
        ret += str(s.smx_pi) if s.smx_pi else ""
        ret += ''.join([str(x) for x in s.extra_pi])
        return ret

class ATTRIBUTE(object):
    def __init__(s, raw):
        s.ns, s.name = raw[0][:2]
        s.value = raw[1]
        
    def __str__(s):
        return s.ns+s.name+"='"+s.value+"'"

class PLAIN_DATA(object):
    def __init__(s,data):
        s.data = ""

    def __str__(s):
        return s.data


class ELEMENT:
    def __init__(s, raw, parent = None):
        s.ns, s.name = raw[1][:2] if raw[1] else ('', '')
        s.attribs = [ATTRIBUTE(x) for x in raw[2]]
        s.children = []
        s.closer = ""
        s.parent = parent

    def star(s):
        return True if s.closer in ['*>', '**>'] else False

    def level(s):
        s.lvl = 1 + s.parent.level()
        s.level = lambda: s.lvl
        return s.lvl

    def collapse(s):
        # collapse all children first
        for x in s.children:
            x.collapse()
        # If this is not a star, return
        if not s.star(): return

        # Now for the tricky part.  Pliers!
        lst = s.children if s.closer == '*>' else s.descendants()
        for x in lst:
            try:
                #apply transformation
                if s.ns <> '' and x.ns == '': x.ns = s.ns
                if s.name <> '' and x.name == '' : x.name = s.name
                for a in s.attribs:
                    if (a.ns, a.name) not in [(tmp.ns, tmp.name) for tmp in x.attribs]:
                        x.attribs += [a]
            except AttributeError:
                pass

        #drop list into parent
        pcs = s.parent.children
        idx = pcs.index(s)
        pcs[idx:idx+1] = s.children
        for x in s.children:
            x.parent = s.parent


    def descendants(s):
        ret = []
        for x in s.children:
            ret += [x]
            ret += x.descendants()
        return ret

    def __str__(s):
        die = False
        if s.name == '':
            print "Error in Tag: No name"
            die = True
        ret = tab*s.level()+"<"+s.ns+s.name
        for x in s.attribs:
            ret += " "+str(x)
        if len(s.children) > 0:
            ret += " "+s.closer+"\n"
            for x in s.children:
                ret += str(x)
            ret += tab*s.level()+"</"+s.ns+s.name+s.closer+"\n"
        else:
            ret += " /"+s.closer+"\n"
        if die:
            print ret
            exit()
        return ret

class BODY(object):
    children = []

    def level(s):
        return -1

    def __str__(s):
        return str(s.children[0])

    def collapse(s):
        s.children[0].collapse()
        if len(s.children) <> 1:
            print "Indentation Error on Line ???\n"
            print "XML may only have ONE root node.\n"
            print "This includes star nodes, and collapsed star nodes\n"
            exit()

class TEXT(ELEMENT):
    def __init__(s, raw, parent = None):
        s.data = raw[1]
        s.parent = parent
        s.children = []
        s.closer = ""

    def __str__(s):
        return tab*s.level()+s.data+"\n"

class COMMENT(TEXT):
    def __str__(s):
        return tab*s.level()+"<!-- "+s.data+" -->\n"

class CDATA(TEXT):
    def __str__(s):
        return tab*s.level()+"<![CDATA[ "+s.data+" ]]>\n"

class PI_TAG(TEXT):
    def __init__(s, raw, parent=None):
        s.name, s.data = raw[1:]
        s.parent = parent
        s.children = []
        s.closer = '?>'

    def __str__(s):
        return tab*s.level()+"<?"+s.name+" "+str(s.data)+"?>\n"


class CUST_TAG(PI_TAG):
    def __init__(s, raw, parent = None):
        s.name, s.data = raw[1:3]
        s.tag = raw[3]
        s.parent = parent

    def __str__(s):
        return tab*s.level()+s.tag+s.name+" "+" '"+s.data+"' "+s.tag+"\n"
