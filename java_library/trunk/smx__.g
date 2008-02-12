lexer grammar smx;

// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 349
WS:	(' '|'\t')+;		
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 350
NL	:	 '\n';
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 351
SYMBOLS:	SYMBOL (SYMBOL|'&'|BANG)*;
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 352
NAME:		(PCHAR|BANG) (PCHAR|BANG|SYMBOL|HASH|'&')*;
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 353
WAKA:	 '>';
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 354
AND	:	'&&';
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 355
EQ	:	'=';
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 356
HASH:	'#';

// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 358
fragment
BANG:   '!';

// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 361
STRING
@init{int num=0;}
@after{setText(getText().substring(num, getText().length()-num));}
	:	('\'\'\'' (options {greedy=false;}:(ESC|.))* '\'\'\'' {num=3;})
    |	('"""' (options {greedy=false;}:(ESC|.))* '"""'	      {num=3;})
    |	('"' (ESC|~('\\'|'\n'|'"'))* '"'					  {num=1;})
    |	('\'' (ESC|~('\\'|'\n'|'\''))* '\''					  {num=1;})
	;

// Fragments	
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 371
fragment ESC:  	'\\' .;
// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 372
fragment PCHAR:	~(NL|' '|'\t'|'"'|'\''|WAKA|SYMBOL|HASH|EQ|'&');

// $ANTLR src "/Users/robertiannucci/antlr/test/smx10/smx.g" 374
fragment
SYMBOL: ('$'|'%'|'('|')'|'*'|'+'|','|'-'|'.'|'/'|':'|';'|
	  	 '<'|'?'|'@'|'['|'\\'|']'|'^'|'_'|'`'|'{'|'|'|'}'|'~');