tab = '    '

class SMX:
    preamble = None
    body = None

    def __str__(s):
        return "preamble:\n"+str(s.preamble)+\
               "\nbody:\n"+str(s.body)

class PREAMBLE:
    smx_pi = None
    extra_pi = []

    def level(s):
        return -1

    def __str__(s):
        return tab+"smx_pi:\n"+tab*2+str(s.smx_pi)+\
               tab+"extra_pis:\n"+tab*2+(tab*2).join([str(x) for x in s.extra_pi])



class ATTRIBUTE(object):
    def __init__(s, raw):
        print raw
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

    def type(s):
        return True if s.closer in ['*>', '**>'] else False

    def level(s):
        s.lvl = 1 + s.parent.level()
        s.level = lambda: s.lvl
        return s.lvl

    def __str__(s):
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
        return ret

class BODY(object):
    children = []

    def level(s):
        return -1

    def __str__(s):
        return str(s.children[0])


class TEXT(ELEMENT):
    def __init__(s, raw, parent = None):
        s.data = raw[1]
        s.parent = parent

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

    def __str__(s):
        return tab*s.level()+"<?"+s.name+" "+str(s.data)+"?>\n"


class CUST_TAG(PI_TAG):
    def __init__(s, raw, parent = None):
        s.name, s.data = raw[1:3]
        s.tag = raw[3]
        s.parent = parent

    def __str__(s):
        return tab*s.level()+s.tag+s.name+" "+" '"+s.data+"' "+s.tag+"\n"
