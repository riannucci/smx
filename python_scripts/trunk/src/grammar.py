from simpleparse.common import numbers, strings, comments
from simpleparse.parser import Parser

declaration = r'''
smx                 := preamble, EOL*, body?, EOL* ,EOF!
smx_pi              := c'smx',!, SO, str, SO, '?>'!, EOL!

preamble            := smx_pi?, EOL*, pi_tag*

body                := ((star_tag/name_tag), (EOL/smx_node)*)

name_tag            := name_tagm, '>', !, SO, el_tag_tail,  EOL!
name_tagm           := name_tag1, name_extra?
name_tag1           := ns_name, (SR, attributes)?, SO
>name_extra<        := ('&&', SO, name_tag1)*

>smx_node<          := level, !, (pi_tag/c_tag/star_tag/el_tag/cust_tag/(comment,EOL!)/((str/text),EOL!))

pi_tag              := name, SO, str, SO, '?>', comment?, EOL!

c_tag               := str, SO, 'C>', comment?, EOL!

star_tag            := star_tagm, ('*>'/'**>'), star_tag_tail, EOL!
star_tagm           := star_tag1, star_extra?
star_tag1           := (star_attributes/(ns_wild_name),(SR,star_attributes)?)?, SO 
star_tag_tail       := comment?
>star_extra<        := ('&&', SO, star_tag1)*

el_tag              := el_tagm, '>', !, SO, el_tag_tail, EOL!
el_tagm             := el_tag1, el_extra?
el_tag1             := (attributes/((ns_name),(SR,attributes)?))?, SO
el_tag_tail         := (str/text)?, comment?
>el_extra<          := ('&&', SO, el_tag1)*

cust_tag            := (ns, name)? , SO, str, SO, tag_end, comment?, EOL!

comment             := SO,'#',-'\n'*

tag_end             := (([\041-\075\077-\176])*,'>')+

wild                := '*'
counter             := '#'

ns_name             := ns, name
ns_wild_name        := ns, (wild/name)

ns                  := (((let/'_'),(let/dig/'_')*)?,':')*
attribute           := ns_name, SO, EQ, SO, str
>attributes<        := attribute, (SO,attribute)*
>star_attributes<   := star_attribute, (SO, star_attribute)*
star_attribute      := ns, name, SO, EQ, SO, (counter/str)
text                := (let/dig/punctchar/S)+
>str<               := string
<punctchar>         := punctuationchar
EOL                 := SO, '\n'
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
