from simpleparse.common import numbers, strings, comments
from simpleparse.parser import Parser

declaration = r'''
smx                 := smx_pi?, EOL*, pi_tag*, EOL*, ((star_tag/name_tag), (smx_node/EOL)*)?, EOL* ,EOF!
smx_pi              := c'smx',!, SO, str, SO, '?>'!, EOL!

name_tag            := name_tagm,  '>', !, SO, (str/text)?, comment?, EOL!
name_tagm           := ns, name, (SR, attributes)?, SO, ('&&', SO, name_tagm)*

smx_node            := level, !, (pi_tag/c_tag/star_tag/el_tag/cust_tag/(comment,EOL!)/((str/text),EOL!))

pi_tag              := name, SO, str, SO, '?>', comment?, EOL!

c_tag               := str, SO, 'C>', comment?, EOL!

star_tag            := star_tagm, ('*>'/'**>'), comment?, EOL!
star_tagm           := (star_attributes/((ns,(wild/name)),(SR,star_attributes)?))?, SO, ('&&', SO, star_tagm)?

el_tag              := el_tagm, '>', !, SO, (str/text)?, comment?, EOL!
el_tagm             := (attributes/((ns,name),(SR,attributes)?))?, SO, ('&&', SO, el_tagm)?

cust_tag            := (ns, name)? , SO, str, SO, tag_end, comment?, EOL!

comment             := SO,'#',-'\n'*

tag_end             := (([\041-\075\077-\176])*,'>')+

wild                := '*'
counter             := '#'
ns                  := (((let/'_'),(let/dig/'_')*)?,':')*
attribute           := ns, name, SO, EQ, SO, str
>attributes<        := attribute, (SO,attribute)*
>star_attributes<   := star_attribute, (SO, star_attribute)*
star_attribute      := ns, name, SO, EQ, SO, (counter/str)
text                := (let/dig/punctchar/S)+
str                 := str1
<str1>              := string
<punctchar>         := punctuationchar
<EOL>               := SO, '\n'
<EQ>                := '='
name                := (let/'_'),(name_char)*
<let>               := letter
<dig>               := digit
<name_char>         := let/dig/'.'/'-'/'_'
level               := SR
<SO>                := S*
<SR>                := S+
<S>                 := [ \t]
'''

parser = Parser(declaration, "smx")
