class dec:
    children = []

class ns_name:
    ns = None
    name = None

class attribute(ns_name):
    data = None

class tag(dec, ns_name):
    attrs = []
    tag = None
    comment = None

class smx_o:
    spi = None
    ppi = []
    root = None

class pi_o(ns_name):
    data = None
    comment = None

class c_o:
    data = None
    comment = None

class star_o(tag):
    pass

class el_o(tag):
    pass

class cust_o(ns_name):
    data = None
    tag = None
    comment = None
