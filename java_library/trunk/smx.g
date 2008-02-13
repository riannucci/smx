grammar smx;

options {
	output=AST;
	memoize=true;
	ASTLabelType=Tree;
}

tokens {
	TAG;
	CHILDREN;
	DATA;
	DTAG;
	ATTRIBS;
	ATTR;
	VALUE;
	DSCLOSER;
	SCLOSER;
	CLOSER;
	NAM;
	COMMENT;
	TAGLET;
}

@header {
	import org.w3c.dom.*;
	import javax.xml.parsers.*;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Map;
}

@members {
	private String ws_atype = null;
	private boolean has_smx_tag = false;
	private boolean allow_smx_tag = true;
	private Document curdoc;
	private Map<String, Integer> countvars = new HashMap<String, Integer>();

	public boolean lvlPreCheck(int lvl) throws RecognitionException {
		return input.LT(1).getType()==WS && getLvl(input.LT(1).getText()) > lvl;
	}
	
	public int getLvl(String t) throws RecognitionException {
		if(ws_atype == null) ws_atype = t.charAt(0) == ' ' ? "\t" : " ";
		if(t.contains(ws_atype)) throw new RecognitionException();
		return t.length();
	}
	
	public Node makeEnt(List<Taglet> l, Node root) {
		for(Taglet t : l) {
			// TODO: Namespace
			System.err.println("Adding element "+t.name);
			Element el = curdoc.createElement(t.name);
			
			for (Map.Entry<String,String> atr : t.attrs.entrySet()) {
				// TODO: Namespace
				el.setAttribute(atr.getKey(), atr.getValue());
			}
			root = root.appendChild(el);
		}
		return root;
	}
	
	public Taglet apply(Taglet t, ContextObj ctx) {
		// order merge(global, local, this)
		String name = null;
		System.err.println("Applying ctx to "+t.name);
		Map<String,String> atrs = new HashMap<String,String>();
		
		if(!ctx.ignore_global) {
			name = ctx.nam_pat_g;
			atrs.putAll(ctx.attribs_g);
		}
		
		if(ctx.nam_pat != null) {
			name = merge(merge(name, ctx.nam_pat), t.name);
			atrs = merge(merge(atrs, ctx.attribs), t.attrs);
		}
	
		for(String key : atrs.keySet()) {
			String val = atrs.get(key);
				if(val.equals("!")) atrs.remove(key);
			else if(!Character.isLetter(val.charAt(0))) {
				// TODO: key=@!(@ == key="@!(@"
				if(!countvars.containsKey(val))
					countvars.put(key, 0);
				atrs.put(key, countvars.get(val).toString());
				countvars.put(val, countvars.get(val)+1);
			}
		}
	
		t.name = name == null ? t.name : name;
		t.attrs = atrs;
		System.err.println("\tResulted in "+t.name);
		return t;
	}
	
	public void del_ctx(ContextObj ctx, int type, Taglet pat)
	 throws RecognitionException {
	 	int numBangs = 0;
	 	while(pat.name.charAt(numBangs++) == '!');
	 
	 	if((type != SCLOSER && type != DSCLOSER) || numBangs > 3) {
	 	   throw new RecognitionException();
	 	}
	 	
	 	if(numBangs == 1 || numBangs == 3) {
	 		// ! *>    # delete the local context
	 		// ! **>   # delete the local context
	 		ctx.nam_pat = null;
	 		ctx.attribs = new HashMap<String, String>();
	 	}
	 	
	 	if(numBangs == 2 || numBangs == 3) {
		 	if(type == SCLOSER) {
		 		// !! *>   # set ignore_global for children
		 		ctx.ignore_global = true;
		 	} else if (type == DSCLOSER) {
		 		// !! **>  # delete the global context
		 		ctx.nam_pat_g = null;
	 			ctx.attribs_g = new HashMap<String, String>();
		 	}
	 	}
	}
	
	public static Map<String, String> merge (Map<String, String> ctx,
								             Map<String, String> prtn) {
		Map<String, String> m = new HashMap<String,String>(ctx);
		for(Map.Entry<String, String> atr : m.entrySet()) {
			if(atr.getValue().equals("!")) {
				m.remove(atr.getKey());
			} else {
				m.put(atr.getKey(), merge(m.get(atr.getKey()), atr.getValue()));
			}
		}
		return m;
	}
	
	public static String merge (String ctx, String ptrn) {
		if(ctx != null && ctx.contains("*"))
			return ctx.replaceAll("\\*", ptrn);
		else
			return ptrn;
	}

	public void update_ctx(ContextObj ctx, int type, Taglet pat)
	 throws RecognitionException {
		// del any preceding !'s on pat.name
		int i=0;
		while(pat.name.charAt(i++) == '!');
		pat.name = pat.name.substring(i);
		
		if(type == SCLOSER) {
			ctx.nam_pat = merge(ctx.nam_pat, pat.name);
			ctx.attribs = merge(ctx.attribs, pat.attrs);
		} else if (type == DSCLOSER) {
			ctx.nam_pat_g = merge(ctx.nam_pat_g, pat.name);
			ctx.attribs_g = merge(ctx.attribs_g, pat.attrs);
		} else {
			throw new RecognitionException();
		}
	}
	
	private class Taglet {
		String name;
		Map<String, String> attrs;
		
		public Taglet(Tree p) {
			name = p.getChild(0).getChild(0).getText();
			System.err.println("Created taglet " + name);
			Tree atrs = p.getChild(1);
			attrs = new HashMap<String, String>();
			for(int i = 0; i < atrs.getChildCount(); i++) {
				Tree t = atrs.getChild(i);
				String nam = t.getChild(0).getText();
				String val = t.getChild(1).getText();
				attrs.put(nam, val);
				System.err.println("\t"+nam+"="+val);
			}
		}
	}
	
   private class ContextObj {
      public String nam_pat_g = null;
      public String nam_pat   = null;
      public Map<String, String> attribs_g  = new HashMap<String,String>();
      public Map<String, String> attribs    = new HashMap<String,String>();
      boolean ignore_global	= false; 
      // creating a new obj, this is always false
      // to make it have a single generation lifespan
      
      public ContextObj(String nam_pat_g, String nam_pat, 
       Map<String, String> attribs_g, Map<String, String> attribs) {
         this.nam_pat_g = nam_pat_g;
         this.nam_pat   = nam_pat;
         this.attribs_g = new HashMap<String,String>(attribs_g);
         this.attribs   = new HashMap<String,String>(attribs);
      }
      
      public ContextObj(ContextObj c) {
         nam_pat_g = c.nam_pat_g;
         nam_pat = c.nam_pat;
         attribs_g = c.attribs_g;
         attribs = c.attribs;
      }
      
      public ContextObj() {}
   }
}


// Parser
smx[DocumentBuilderFactory dbf]returns [Document retdoc]
@init{
	DocumentBuilder db = null;
	try {
		db = dbf.newDocumentBuilder();
	}catch (Exception e) {
		throw new RecognitionException();
	}
	curdoc = db.newDocument();
}
@after {
	$retdoc = curdoc;
}:
	a=line[new ContextObj(), curdoc]
	{
	if(a != null){
		CommonTree t = ((CommonTree)$a.tree);
		if(t.getType() == TAG) {
			String nam = ""+(t.getFirstChildWithType(TAGLET)).getChild(0).getChild(0);
			String closer = "";
			Tree cls = t.getFirstChildWithType(CLOSER);
			for(int i=0; i < cls.getChildCount(); i ++) {
				closer += cls.getChild(i);
			}
			if(nam.equals("config") && closer.equals("smx>")) {
				System.err.println("Found smx config");
				has_smx_tag = true;
				Document tmp = $retdoc;
				curdoc = db.newDocument();
				//    set global options  from tmp
				//      xml header type
			}
		}
	}
	}
	({has_smx_tag}?=> 
		{System.err.println("Parsing real document...");} b=line[new ContextObj(), curdoc ]
	)?
 	EOF
 	-> {has_smx_tag}? $b
 	-> $a
 	;

line[ContextObj ctx, Node n]
@init{int level=0;}:
	lvl=WS? {level = $lvl != null ? getLvl($lvl.text) : 0;}
	(comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n]) 
	-> comment? tag? dtag? data?;
	
dot: ~NL*;
comment[Node n]
@after{
	System.err.println("Adding Comment...");
	n.appendChild(curdoc.createComment($d.text));
}:	HASH+ WS d=dot NL -> ;// ^(COMMENT dot); 


children[int level, ContextObj ctx, Node n] returns [int num]
@init{$num=0;}:
	 WS? (childs+=comment[n] | NL) (WS? NL)*
	 ({lvlPreCheck(level)}?=> {$num++;} childs+=line[ctx, n])* -> $childs*;
 
data[Node n]: data_sub[n] (comment[n] | NL) -> ; // ^(DATA data_sub) comment?;
data_sub[Node n]
@after {
	System.err.println("Adding Text Node: "+$data_sub.text);
	n.appendChild(curdoc.createTextNode($data_sub.text));
}: STRING  | ~(NL|WS|WAKA|HASH|STRING) ~(NL|HASH|WAKA)*;

dtag[ContextObj ctx, Node n]
@after {;
	if($c.text.equals("C>")) {
		System.err.println("Adding CDATA Node");
		n.appendChild(curdoc.createCDATASection($d.text));
	} else if($c.text.equals("\%>") && $nam.text != null) {
		System.err.println("Adding PI Node");
		n.appendChild(curdoc.createProcessingInstruction($nam.text, $d.text));
	} else {
		System.err.println("Got dtag with tag '"+ $c.text + "'");
		throw new RecognitionException();
	}
}: (nam=NAME WS)? d=STRING WS? c=closer -> ; // ^(DTAG ^(NAM $nam?) STRING closer );

taglet: (n=NAME WS)? attribs -> ^(TAGLET ^(NAM $n?) attribs);
tag[int level, ContextObj ctx, Node n]
@init { boolean star=true; ContextObj ctx_new = ctx; Node ths = n; }
@after {allow_smx_tag=false; // guarnatees that only first line can be smx> 
}:
	  ts+=taglet (AND WS? ts+=taglet)* closer
	  {
	  	if ($closer.type == SCLOSER || $closer.type == DSCLOSER) {
	  		if(list_ts.size() == 1) {
	  			Taglet pattern = new Taglet(((taglet_return)list_ts.get(0)).tree);
	  			ctx_new = new ContextObj(ctx);
	  			del_ctx(ctx_new, $closer.type, pattern);
		  		update_ctx(ctx_new, $closer.type, pattern);
	  		} else {
	  			throw new RecognitionException();
	  		}
	  	}else if ($closer.text.equals(">") || (allow_smx_tag && $closer.text.equals("smx>"))) {		
  			List<Taglet> lt = new ArrayList<Taglet>();
  			for(taglet_return taglt : (List<taglet_return>)$ts) {
  				lt.add(apply(new Taglet(taglt.tree), ctx));
  			}
  			
  			ths = makeEnt(lt, ths);
  			ctx_new = new ContextObj(ctx);
			ctx_new.nam_pat = null;
			ctx_new.attribs = null;
			
			star = false;
		}else {
			throw new RecognitionException();
		}
	  } 
	  (({!star}?=> WS? data[ths]) | children[level, ctx_new, ths])

	  // error check.. star and no children?
	  { if(star && $children.num <= 0) throw new RecognitionException(); }
  -> ; 
  //{!star}? {solidify(list_ts, $children.tree, ctx)}
  //-> children?; // these are the potentially modified children

attribs:	(a+=attr WS?)* -> ^(ATTRIBS $a*);
attr:		NAME WS? EQ WS? (v+=(SYMBOLS|HASH|BANG)* v+=SYMBOL | v+=STRING) 
		    -> ^(ATTR NAME $v+);

closer returns [int type]
@init{String text="";}:
		(s+=SYMBOLS|s+=HASH|s+=EQ|s+=NAME)* s+=WAKA 
		{for(Object o: $s) {text += ((Token)o).getText();}
		$type = text.equals("*>") ? SCLOSER :  text.equals("**>") ? DSCLOSER : CLOSER;}
		-> ^(CLOSER $s+);

// LEXER

WS:	(' '|'\t')+;		
NL	:	 '\n';
SYMBOLS:	SYMBOL (SYMBOL|'&'|BANG)*;
NAME:		(PCHAR|BANG) (PCHAR|BANG|SYMBOL|HASH|'&')*;
WAKA:	 '>';
AND	:	'&&';
EQ	:	'=';
HASH:	'#';

fragment
BANG:   '!';

STRING
@init{int num=0;}
@after{setText(getText().substring(num, getText().length()-num));}
	:	('\'\'\'' (options {greedy=false;}:(ESC|.))* '\'\'\'' {num=3;})
    |	('"""' (options {greedy=false;}:(ESC|.))* '"""'	      {num=3;})
    |	('"' (ESC|~('\\'|'\n'|'"'))* '"'					  {num=1;})
    |	('\'' (ESC|~('\\'|'\n'|'\''))* '\''					  {num=1;})
	;

// Fragments	
fragment ESC:  	'\\' .;
fragment PCHAR:	~(NL|' '|'\t'|'"'|'\''|WAKA|SYMBOL|HASH|EQ|'&');

fragment
SYMBOL: ('$'|'%'|'('|')'|'*'|'+'|','|'-'|'.'|'/'|':'|';'|
	  	 '<'|'?'|'@'|'['|'\\'|']'|'^'|'_'|'`'|'{'|'|'|'}'|'~');
