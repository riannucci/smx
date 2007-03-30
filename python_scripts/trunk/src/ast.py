class SMX:
    preamble = None
    body = None

    def __str__(s):
        return "preamble:\n\t"+str(s.preamble)+\
               "\nbody:\n"+str(s.body)

class PREAMBLE:
    smx_pi = None
    extra_pi = []

    def __str__(s):
        return "smx_pi:\n\t\t"+str(s.smx_pi)+\
               "\n\textra_pis:\n\t\t"+'\n\t\t'.join([str(x) for x in s.extra_pi])

class ELEMENT(object):
    def __init__(s, ns, name, attribs=[], children = []):
        s.ns = ns
        s.name = name
        s.attribs = attribs
        s.children = children

    def __str__(s):
        ret = "<"+s.ns+s.name
        for x in s.attribs:
            ret += str(x)
        ret == ">"
        for x in s.children:
            ret += str(x)
        return ret

class PLAIN_DATA(object):
    def __init__(s,data):
        s.data = ""

    def __str__(s):
        return s.data

class COMMENT(PLAIN_DATA):
    def __str__(s):
        return "COMMENT: "+str(super(COMMENT, s))

class CDATA(PLAIN_DATA):
    def __str__(s):
        return "CDATA: "+str(super(COMMENT, s))

class STAR(ELEMENT):
    def __init__(s, ns = None, name=None, attribs=[], children = [], type = None):
        s.type = type
        ELEMENT.__init__(s, ns, name, attribs, children)

    def __str__(s):
        return s.type + super(STAR, s).__str__()

class PI_TAG:
    def __init__(s, name, data):
        s.name = name
        s.data = data

    def __str__(s):
        return "PI "+s.name+": "+str(s.data)

class CUST_TAG(PI_TAG):
    def __init__(s, nsname, data, tag):
        s.nsname
        s.data
        s.tag

    def __str__(s):
        return "Cust Tag ("+s.tag+"): "+s.nsname+" '"+s.data+"'"
