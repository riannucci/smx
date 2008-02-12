// $ANTLR 3.0.1 smx.g 2008-02-12 13:32:22

	import org.w3c.dom.*;
	import javax.xml.parsers.*;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Map;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class smxParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "TAG", "CHILDREN", "DATA", "DTAG", "ATTRIBS", "ATTR", "VALUE", "DSCLOSER", "SCLOSER", "CLOSER", "NAM", "COMMENT", "TAGLET", "WS", "NL", "HASH", "STRING", "WAKA", "NAME", "AND", "EQ", "SYMBOLS", "BANG", "SYMBOL", "PCHAR", "ESC"
    };
    public static final int WAKA=21;
    public static final int DSCLOSER=11;
    public static final int HASH=19;
    public static final int WS=17;
    public static final int BANG=26;
    public static final int EQ=24;
    public static final int STRING=20;
    public static final int DATA=6;
    public static final int COMMENT=15;
    public static final int VALUE=10;
    public static final int ESC=29;
    public static final int TAGLET=16;
    public static final int AND=23;
    public static final int NL=18;
    public static final int ATTR=9;
    public static final int NAM=14;
    public static final int SCLOSER=12;
    public static final int EOF=-1;
    public static final int ATTRIBS=8;
    public static final int SYMBOL=27;
    public static final int SYMBOLS=25;
    public static final int CHILDREN=5;
    public static final int TAG=4;
    public static final int CLOSER=13;
    public static final int NAME=22;
    public static final int PCHAR=28;
    public static final int DTAG=7;

        public smxParser(TokenStream input) {
            super(input);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "smx.g"; }


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
    			System.out.println("Adding element "+t.name);
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
    		System.out.println("Applying ctx to "+t.name);
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
    		System.out.println("\tResulted in "+t.name);
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
    			System.out.println("Created taglet " + name);
    			Tree atrs = p.getChild(1);
    			attrs = new HashMap<String, String>();
    			for(int i = 0; i < atrs.getChildCount(); i++) {
    				Tree t = atrs.getChild(i);
    				String nam = t.getChild(0).getText();
    				String val = t.getChild(1).getText();
    				attrs.put(nam, val);
    				System.out.println("\t"+nam+"="+val);
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


    public static class smx_return extends ParserRuleReturnScope {
        public Document retdoc;
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start smx
    // smx.g:214:1: smx[DocumentBuilderFactory dbf] returns [Document retdoc] : a= line[new ContextObj(), curdoc] ({...}? =>b= line[new ContextObj(), curdoc ] )? EOF -> {has_smx_tag}? $b -> $a;
    public final smx_return smx(DocumentBuilderFactory dbf) throws RecognitionException {
        smx_return retval = new smx_return();
        retval.start = input.LT(1);
        int smx_StartIndex = input.index();
        Tree root_0 = null;

        Token EOF1=null;
        line_return a = null;

        line_return b = null;


        Tree EOF1_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_line=new RewriteRuleSubtreeStream(adaptor,"rule line");

        	DocumentBuilder db = null;
        	try {
        		db = dbf.newDocumentBuilder();
        	}catch (Exception e) {
        		throw new RecognitionException();
        	}
        	curdoc = db.newDocument();

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // smx.g:226:2: (a= line[new ContextObj(), curdoc] ({...}? =>b= line[new ContextObj(), curdoc ] )? EOF -> {has_smx_tag}? $b -> $a)
            // smx.g:227:2: a= line[new ContextObj(), curdoc] ({...}? =>b= line[new ContextObj(), curdoc ] )? EOF
            {
            pushFollow(FOLLOW_line_in_smx119);
            a=line(new ContextObj(),  curdoc);
            _fsp--;

            stream_line.add(a.getTree());

            	if(a != null){
            		CommonTree t = ((CommonTree)((Tree)a.tree));
            		if(t.getType() == TAG) {
            			String nam = ""+(t.getFirstChildWithType(TAGLET)).getChild(0).getChild(0);
            			String closer = "";
            			Tree cls = t.getFirstChildWithType(CLOSER);
            			for(int i=0; i < cls.getChildCount(); i ++) {
            				closer += cls.getChild(i);
            			}
            			if(nam.equals("config") && closer.equals("smx>")) {
            				System.out.println("Found smx config");
            				has_smx_tag = true;
            				Document tmp = retval.retdoc;
            				curdoc = db.newDocument();
            				//    set global options  from tmp
            				//      xml header type
            			}
            		}
            	}
            	
            // smx.g:249:2: ({...}? =>b= line[new ContextObj(), curdoc ] )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( ((LA1_0>=TAG && LA1_0<=WS)||(LA1_0>=HASH && LA1_0<=ESC)) && (has_smx_tag)) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // smx.g:249:3: {...}? =>b= line[new ContextObj(), curdoc ]
                    {
                    if ( !(has_smx_tag) ) {
                        throw new FailedPredicateException(input, "smx", "has_smx_tag");
                    }
                    System.out.println("Parsing real document...");
                    pushFollow(FOLLOW_line_in_smx137);
                    b=line(new ContextObj(),  curdoc );
                    _fsp--;

                    stream_line.add(b.getTree());

                    }
                    break;

            }

            EOF1=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_smx146); 
            stream_EOF.add(EOF1);


            // AST REWRITE
            // elements: b, a
            // token labels: 
            // rule labels: a, retval, b
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"token a",a!=null?a.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_b=new RewriteRuleSubtreeStream(adaptor,"token b",b!=null?b.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 253:3: -> {has_smx_tag}? $b
            if (has_smx_tag) {
                adaptor.addChild(root_0, stream_b.next());

            }
            else // 254:3: -> $a
            {
                adaptor.addChild(root_0, stream_a.next());

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


            	retval.retdoc = curdoc;

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end smx

    public static class line_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start line
    // smx.g:257:1: line[ContextObj ctx, Node n] : (lvl= WS )? ( comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n] ) -> ( comment )? ( tag )? ( dtag )? ( data )? ;
    public final line_return line(ContextObj ctx, Node n) throws RecognitionException {
        line_return retval = new line_return();
        retval.start = input.LT(1);
        int line_StartIndex = input.index();
        Tree root_0 = null;

        Token lvl=null;
        comment_return comment2 = null;

        tag_return tag3 = null;

        dtag_return dtag4 = null;

        data_return data5 = null;


        Tree lvl_tree=null;
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_data=new RewriteRuleSubtreeStream(adaptor,"rule data");
        RewriteRuleSubtreeStream stream_tag=new RewriteRuleSubtreeStream(adaptor,"rule tag");
        RewriteRuleSubtreeStream stream_comment=new RewriteRuleSubtreeStream(adaptor,"rule comment");
        RewriteRuleSubtreeStream stream_dtag=new RewriteRuleSubtreeStream(adaptor,"rule dtag");
        int level=0;
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // smx.g:258:20: ( (lvl= WS )? ( comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n] ) -> ( comment )? ( tag )? ( dtag )? ( data )? )
            // smx.g:259:2: (lvl= WS )? ( comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n] )
            {
            // smx.g:259:5: (lvl= WS )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==WS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // smx.g:259:5: lvl= WS
                    {
                    lvl=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_line180); 
                    stream_WS.add(lvl);


                    }
                    break;

            }

            level = lvl != null ? getLvl(lvl.getText()) : 0;
            // smx.g:260:2: ( comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n] )
            int alt3=4;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // smx.g:260:3: comment[n]
                    {
                    pushFollow(FOLLOW_comment_in_line187);
                    comment2=comment(n);
                    _fsp--;

                    stream_comment.add(comment2.getTree());

                    }
                    break;
                case 2 :
                    // smx.g:260:16: tag[level, ctx, n]
                    {
                    pushFollow(FOLLOW_tag_in_line192);
                    tag3=tag(level,  ctx,  n);
                    _fsp--;

                    stream_tag.add(tag3.getTree());

                    }
                    break;
                case 3 :
                    // smx.g:260:37: dtag[ctx, n]
                    {
                    pushFollow(FOLLOW_dtag_in_line197);
                    dtag4=dtag(ctx,  n);
                    _fsp--;

                    stream_dtag.add(dtag4.getTree());

                    }
                    break;
                case 4 :
                    // smx.g:260:52: data[n]
                    {
                    pushFollow(FOLLOW_data_in_line202);
                    data5=data(n);
                    _fsp--;

                    stream_data.add(data5.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: comment, data, dtag, tag
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 261:2: -> ( comment )? ( tag )? ( dtag )? ( data )?
            {
                // smx.g:261:5: ( comment )?
                if ( stream_comment.hasNext() ) {
                    adaptor.addChild(root_0, stream_comment.next());

                }
                stream_comment.reset();
                // smx.g:261:14: ( tag )?
                if ( stream_tag.hasNext() ) {
                    adaptor.addChild(root_0, stream_tag.next());

                }
                stream_tag.reset();
                // smx.g:261:19: ( dtag )?
                if ( stream_dtag.hasNext() ) {
                    adaptor.addChild(root_0, stream_dtag.next());

                }
                stream_dtag.reset();
                // smx.g:261:25: ( data )?
                if ( stream_data.hasNext() ) {
                    adaptor.addChild(root_0, stream_data.next());

                }
                stream_data.reset();

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end line

    public static class dot_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dot
    // smx.g:263:1: dot : (~ NL )* ;
    public final dot_return dot() throws RecognitionException {
        dot_return retval = new dot_return();
        retval.start = input.LT(1);
        int dot_StartIndex = input.index();
        Tree root_0 = null;

        Token set6=null;

        Tree set6_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // smx.g:263:4: ( (~ NL )* )
            // smx.g:263:6: (~ NL )*
            {
            root_0 = (Tree)adaptor.nil();

            // smx.g:263:6: (~ NL )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=TAG && LA4_0<=WS)||(LA4_0>=HASH && LA4_0<=ESC)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // smx.g:263:6: ~ NL
            	    {
            	    set6=(Token)input.LT(1);
            	    if ( (input.LA(1)>=TAG && input.LA(1)<=WS)||(input.LA(1)>=HASH && input.LA(1)<=ESC) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, adaptor.create(set6));
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_dot228);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dot

    public static class comment_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start comment
    // smx.g:264:1: comment[Node n] : ( HASH )+ WS d= dot NL ->;
    public final comment_return comment(Node n) throws RecognitionException {
        comment_return retval = new comment_return();
        retval.start = input.LT(1);
        int comment_StartIndex = input.index();
        Tree root_0 = null;

        Token HASH7=null;
        Token WS8=null;
        Token NL9=null;
        dot_return d = null;


        Tree HASH7_tree=null;
        Tree WS8_tree=null;
        Tree NL9_tree=null;
        RewriteRuleTokenStream stream_NL=new RewriteRuleTokenStream(adaptor,"token NL");
        RewriteRuleTokenStream stream_HASH=new RewriteRuleTokenStream(adaptor,"token HASH");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_dot=new RewriteRuleSubtreeStream(adaptor,"rule dot");
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // smx.g:268:2: ( ( HASH )+ WS d= dot NL ->)
            // smx.g:268:4: ( HASH )+ WS d= dot NL
            {
            // smx.g:268:4: ( HASH )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==HASH) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // smx.g:268:4: HASH
            	    {
            	    HASH7=(Token)input.LT(1);
            	    match(input,HASH,FOLLOW_HASH_in_comment241); 
            	    stream_HASH.add(HASH7);


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            WS8=(Token)input.LT(1);
            match(input,WS,FOLLOW_WS_in_comment244); 
            stream_WS.add(WS8);

            pushFollow(FOLLOW_dot_in_comment248);
            d=dot();
            _fsp--;

            stream_dot.add(d.getTree());
            NL9=(Token)input.LT(1);
            match(input,NL,FOLLOW_NL_in_comment250); 
            stream_NL.add(NL9);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 268:22: ->
            {
                root_0 = null;
            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


            	System.out.println("Adding Comment...");
            	n.appendChild(curdoc.createComment(input.toString(d.start,d.stop)));

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end comment

    public static class children_return extends ParserRuleReturnScope {
        public int num;
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start children
    // smx.g:271:1: children[int level, ContextObj ctx, Node n] returns [int num] : ( WS )? (childs+= comment[n] | NL ) ( ( WS )? NL )* ({...}? =>childs+= line[ctx, n] )* -> ( $childs)* ;
    public final children_return children(int level, ContextObj ctx, Node n) throws RecognitionException {
        children_return retval = new children_return();
        retval.start = input.LT(1);
        int children_StartIndex = input.index();
        Tree root_0 = null;

        Token WS10=null;
        Token NL11=null;
        Token WS12=null;
        Token NL13=null;
        List list_childs=null;
        RuleReturnScope childs = null;
        Tree WS10_tree=null;
        Tree NL11_tree=null;
        Tree WS12_tree=null;
        Tree NL13_tree=null;
        RewriteRuleTokenStream stream_NL=new RewriteRuleTokenStream(adaptor,"token NL");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_line=new RewriteRuleSubtreeStream(adaptor,"rule line");
        RewriteRuleSubtreeStream stream_comment=new RewriteRuleSubtreeStream(adaptor,"rule comment");
        retval.num =0;
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // smx.g:272:15: ( ( WS )? (childs+= comment[n] | NL ) ( ( WS )? NL )* ({...}? =>childs+= line[ctx, n] )* -> ( $childs)* )
            // smx.g:273:3: ( WS )? (childs+= comment[n] | NL ) ( ( WS )? NL )* ({...}? =>childs+= line[ctx, n] )*
            {
            // smx.g:273:3: ( WS )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==WS) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // smx.g:273:3: WS
                    {
                    WS10=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_children272); 
                    stream_WS.add(WS10);


                    }
                    break;

            }

            // smx.g:273:7: (childs+= comment[n] | NL )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==HASH) ) {
                alt7=1;
            }
            else if ( (LA7_0==NL) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("273:7: (childs+= comment[n] | NL )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // smx.g:273:8: childs+= comment[n]
                    {
                    pushFollow(FOLLOW_comment_in_children278);
                    childs=comment(n);
                    _fsp--;

                    stream_comment.add(childs.getTree());
                    if (list_childs==null) list_childs=new ArrayList();
                    list_childs.add(childs);


                    }
                    break;
                case 2 :
                    // smx.g:273:29: NL
                    {
                    NL11=(Token)input.LT(1);
                    match(input,NL,FOLLOW_NL_in_children283); 
                    stream_NL.add(NL11);


                    }
                    break;

            }

            // smx.g:273:33: ( ( WS )? NL )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==WS) ) {
                    int LA9_1 = input.LA(2);

                    if ( (LA9_1==NL) ) {
                        alt9=1;
                    }


                }
                else if ( (LA9_0==NL) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // smx.g:273:34: ( WS )? NL
            	    {
            	    // smx.g:273:34: ( WS )?
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( (LA8_0==WS) ) {
            	        alt8=1;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // smx.g:273:34: WS
            	            {
            	            WS12=(Token)input.LT(1);
            	            match(input,WS,FOLLOW_WS_in_children287); 
            	            stream_WS.add(WS12);


            	            }
            	            break;

            	    }

            	    NL13=(Token)input.LT(1);
            	    match(input,NL,FOLLOW_NL_in_children290); 
            	    stream_NL.add(NL13);


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            // smx.g:274:3: ({...}? =>childs+= line[ctx, n] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==WS) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_1 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==HASH) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_2 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==NAME) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_3 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==AND) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_4 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==SYMBOLS) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_5 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==EQ) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_6 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==WAKA) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_7 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( (LA10_0==STRING) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_8 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }
                else if ( ((LA10_0>=TAG && LA10_0<=TAGLET)||(LA10_0>=BANG && LA10_0<=ESC)) && ((lvlPreCheck(level)||has_smx_tag))) {
                    int LA10_9 = input.LA(2);

                    if ( (lvlPreCheck(level)) ) {
                        alt10=1;
                    }


                }


                switch (alt10) {
            	case 1 :
            	    // smx.g:274:4: {...}? =>childs+= line[ctx, n]
            	    {
            	    if ( !(lvlPreCheck(level)) ) {
            	        throw new FailedPredicateException(input, "children", "lvlPreCheck(level)");
            	    }
            	    retval.num++;
            	    pushFollow(FOLLOW_line_in_children304);
            	    childs=line(ctx,  n);
            	    _fsp--;

            	    stream_line.add(childs.getTree());
            	    if (list_childs==null) list_childs=new ArrayList();
            	    list_childs.add(childs);


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            // AST REWRITE
            // elements: childs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: childs
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_childs=new RewriteRuleSubtreeStream(adaptor,"token childs",list_childs);
            root_0 = (Tree)adaptor.nil();
            // 274:61: -> ( $childs)*
            {
                // smx.g:274:64: ( $childs)*
                while ( stream_childs.hasNext() ) {
                    adaptor.addChild(root_0, ((ParserRuleReturnScope)stream_childs.next()).getTree());

                }
                stream_childs.reset();

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end children

    public static class data_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start data
    // smx.g:276:1: data[Node n] : data_sub[n] ( comment[n] | NL ) ->;
    public final data_return data(Node n) throws RecognitionException {
        data_return retval = new data_return();
        retval.start = input.LT(1);
        int data_StartIndex = input.index();
        Tree root_0 = null;

        Token NL16=null;
        data_sub_return data_sub14 = null;

        comment_return comment15 = null;


        Tree NL16_tree=null;
        RewriteRuleTokenStream stream_NL=new RewriteRuleTokenStream(adaptor,"token NL");
        RewriteRuleSubtreeStream stream_comment=new RewriteRuleSubtreeStream(adaptor,"rule comment");
        RewriteRuleSubtreeStream stream_data_sub=new RewriteRuleSubtreeStream(adaptor,"rule data_sub");
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // smx.g:276:13: ( data_sub[n] ( comment[n] | NL ) ->)
            // smx.g:276:15: data_sub[n] ( comment[n] | NL )
            {
            pushFollow(FOLLOW_data_sub_in_data322);
            data_sub14=data_sub(n);
            _fsp--;

            stream_data_sub.add(data_sub14.getTree());
            // smx.g:276:27: ( comment[n] | NL )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==HASH) ) {
                alt11=1;
            }
            else if ( (LA11_0==NL) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("276:27: ( comment[n] | NL )", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // smx.g:276:28: comment[n]
                    {
                    pushFollow(FOLLOW_comment_in_data326);
                    comment15=comment(n);
                    _fsp--;

                    stream_comment.add(comment15.getTree());

                    }
                    break;
                case 2 :
                    // smx.g:276:41: NL
                    {
                    NL16=(Token)input.LT(1);
                    match(input,NL,FOLLOW_NL_in_data331); 
                    stream_NL.add(NL16);


                    }
                    break;

            }


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 276:45: ->
            {
                root_0 = null;
            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end data

    public static class data_sub_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start data_sub
    // smx.g:277:1: data_sub[Node n] : ( STRING | ~ ( NL | WS | WAKA | HASH | STRING ) (~ ( NL | HASH | WAKA ) )* );
    public final data_sub_return data_sub(Node n) throws RecognitionException {
        data_sub_return retval = new data_sub_return();
        retval.start = input.LT(1);
        int data_sub_StartIndex = input.index();
        Tree root_0 = null;

        Token STRING17=null;
        Token set18=null;
        Token set19=null;

        Tree STRING17_tree=null;
        Tree set18_tree=null;
        Tree set19_tree=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // smx.g:281:2: ( STRING | ~ ( NL | WS | WAKA | HASH | STRING ) (~ ( NL | HASH | WAKA ) )* )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==STRING) ) {
                alt13=1;
            }
            else if ( ((LA13_0>=TAG && LA13_0<=TAGLET)||(LA13_0>=NAME && LA13_0<=ESC)) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("277:1: data_sub[Node n] : ( STRING | ~ ( NL | WS | WAKA | HASH | STRING ) (~ ( NL | HASH | WAKA ) )* );", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // smx.g:281:4: STRING
                    {
                    root_0 = (Tree)adaptor.nil();

                    STRING17=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_data_sub348); 
                    STRING17_tree = (Tree)adaptor.create(STRING17);
                    adaptor.addChild(root_0, STRING17_tree);


                    }
                    break;
                case 2 :
                    // smx.g:281:14: ~ ( NL | WS | WAKA | HASH | STRING ) (~ ( NL | HASH | WAKA ) )*
                    {
                    root_0 = (Tree)adaptor.nil();

                    set18=(Token)input.LT(1);
                    if ( (input.LA(1)>=TAG && input.LA(1)<=TAGLET)||(input.LA(1)>=NAME && input.LA(1)<=ESC) ) {
                        input.consume();
                        adaptor.addChild(root_0, adaptor.create(set18));
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_data_sub353);    throw mse;
                    }

                    // smx.g:281:40: (~ ( NL | HASH | WAKA ) )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>=TAG && LA12_0<=WS)||LA12_0==STRING||(LA12_0>=NAME && LA12_0<=ESC)) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // smx.g:281:40: ~ ( NL | HASH | WAKA )
                    	    {
                    	    set19=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=TAG && input.LA(1)<=WS)||input.LA(1)==STRING||(input.LA(1)>=NAME && input.LA(1)<=ESC) ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, adaptor.create(set19));
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_data_sub366);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


            	System.out.println("Adding Text Node: "+input.toString(retval.start,input.LT(-1)));
            	n.appendChild(curdoc.createTextNode(input.toString(retval.start,input.LT(-1))));

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end data_sub

    public static class dtag_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dtag
    // smx.g:283:1: dtag[ContextObj ctx, Node n] : (nam= NAME WS )? d= STRING ( WS )? c= closer ->;
    public final dtag_return dtag(ContextObj ctx, Node n) throws RecognitionException {
        dtag_return retval = new dtag_return();
        retval.start = input.LT(1);
        int dtag_StartIndex = input.index();
        Tree root_0 = null;

        Token nam=null;
        Token d=null;
        Token WS20=null;
        Token WS21=null;
        closer_return c = null;


        Tree nam_tree=null;
        Tree d_tree=null;
        Tree WS20_tree=null;
        Tree WS21_tree=null;
        RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleSubtreeStream stream_closer=new RewriteRuleSubtreeStream(adaptor,"rule closer");
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // smx.g:294:2: ( (nam= NAME WS )? d= STRING ( WS )? c= closer ->)
            // smx.g:294:4: (nam= NAME WS )? d= STRING ( WS )? c= closer
            {
            // smx.g:294:4: (nam= NAME WS )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==NAME) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // smx.g:294:5: nam= NAME WS
                    {
                    nam=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_dtag390); 
                    stream_NAME.add(nam);

                    WS20=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_dtag392); 
                    stream_WS.add(WS20);


                    }
                    break;

            }

            d=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dtag398); 
            stream_STRING.add(d);

            // smx.g:294:28: ( WS )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==WS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // smx.g:294:28: WS
                    {
                    WS21=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_dtag400); 
                    stream_WS.add(WS21);


                    }
                    break;

            }

            pushFollow(FOLLOW_closer_in_dtag405);
            c=closer();
            _fsp--;

            stream_closer.add(c.getTree());

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 294:41: ->
            {
                root_0 = null;
            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

            ;
            	if(input.toString(c.start,c.stop).equals("C>")) {
            		System.out.println("Adding CDATA Node");
            		n.appendChild(curdoc.createCDATASection(d.getText()));
            	} else if(input.toString(c.start,c.stop).equals("%>") && nam.getText() != null) {
            		System.out.println("Adding PI Node");
            		n.appendChild(curdoc.createProcessingInstruction(nam.getText(), d.getText()));
            	} else {
            		throw new RecognitionException();
            	}

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dtag

    public static class taglet_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start taglet
    // smx.g:296:1: taglet : (n= NAME WS )? attribs -> ^( TAGLET ^( NAM ( $n)? ) attribs ) ;
    public final taglet_return taglet() throws RecognitionException {
        taglet_return retval = new taglet_return();
        retval.start = input.LT(1);
        int taglet_StartIndex = input.index();
        Tree root_0 = null;

        Token n=null;
        Token WS22=null;
        attribs_return attribs23 = null;


        Tree n_tree=null;
        Tree WS22_tree=null;
        RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_attribs=new RewriteRuleSubtreeStream(adaptor,"rule attribs");
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // smx.g:296:7: ( (n= NAME WS )? attribs -> ^( TAGLET ^( NAM ( $n)? ) attribs ) )
            // smx.g:296:9: (n= NAME WS )? attribs
            {
            // smx.g:296:9: (n= NAME WS )?
            int alt16=2;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // smx.g:296:10: n= NAME WS
                    {
                    n=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_taglet419); 
                    stream_NAME.add(n);

                    WS22=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_taglet421); 
                    stream_WS.add(WS22);


                    }
                    break;

            }

            pushFollow(FOLLOW_attribs_in_taglet425);
            attribs23=attribs();
            _fsp--;

            stream_attribs.add(attribs23.getTree());

            // AST REWRITE
            // elements: n, attribs
            // token labels: n
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 296:30: -> ^( TAGLET ^( NAM ( $n)? ) attribs )
            {
                // smx.g:296:33: ^( TAGLET ^( NAM ( $n)? ) attribs )
                {
                Tree root_1 = (Tree)adaptor.nil();
                root_1 = (Tree)adaptor.becomeRoot(adaptor.create(TAGLET, "TAGLET"), root_1);

                // smx.g:296:42: ^( NAM ( $n)? )
                {
                Tree root_2 = (Tree)adaptor.nil();
                root_2 = (Tree)adaptor.becomeRoot(adaptor.create(NAM, "NAM"), root_2);

                // smx.g:296:48: ( $n)?
                if ( stream_n.hasNext() ) {
                    adaptor.addChild(root_2, stream_n.next());

                }
                stream_n.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_attribs.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end taglet

    public static class tag_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tag
    // smx.g:297:1: tag[int level, ContextObj ctx, Node n] : ts+= taglet ( AND ( WS )? ts+= taglet )* closer ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] ) ->;
    public final tag_return tag(int level, ContextObj ctx, Node n) throws RecognitionException {
        tag_return retval = new tag_return();
        retval.start = input.LT(1);
        int tag_StartIndex = input.index();
        Tree root_0 = null;

        Token AND24=null;
        Token WS25=null;
        Token WS27=null;
        List list_ts=null;
        closer_return closer26 = null;

        data_return data28 = null;

        children_return children29 = null;

        RuleReturnScope ts = null;
        Tree AND24_tree=null;
        Tree WS25_tree=null;
        Tree WS27_tree=null;
        RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_closer=new RewriteRuleSubtreeStream(adaptor,"rule closer");
        RewriteRuleSubtreeStream stream_data=new RewriteRuleSubtreeStream(adaptor,"rule data");
        RewriteRuleSubtreeStream stream_taglet=new RewriteRuleSubtreeStream(adaptor,"rule taglet");
        RewriteRuleSubtreeStream stream_children=new RewriteRuleSubtreeStream(adaptor,"rule children");
         boolean star=true; ContextObj ctx_new = ctx; Node ths = n; 
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // smx.g:300:2: (ts+= taglet ( AND ( WS )? ts+= taglet )* closer ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] ) ->)
            // smx.g:301:4: ts+= taglet ( AND ( WS )? ts+= taglet )* closer ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] )
            {
            pushFollow(FOLLOW_taglet_in_tag463);
            ts=taglet();
            _fsp--;

            stream_taglet.add(ts.getTree());
            if (list_ts==null) list_ts=new ArrayList();
            list_ts.add(ts);

            // smx.g:301:15: ( AND ( WS )? ts+= taglet )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==AND) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // smx.g:301:16: AND ( WS )? ts+= taglet
            	    {
            	    AND24=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_tag466); 
            	    stream_AND.add(AND24);

            	    // smx.g:301:20: ( WS )?
            	    int alt17=2;
            	    int LA17_0 = input.LA(1);

            	    if ( (LA17_0==WS) ) {
            	        alt17=1;
            	    }
            	    switch (alt17) {
            	        case 1 :
            	            // smx.g:301:20: WS
            	            {
            	            WS25=(Token)input.LT(1);
            	            match(input,WS,FOLLOW_WS_in_tag468); 
            	            stream_WS.add(WS25);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_taglet_in_tag473);
            	    ts=taglet();
            	    _fsp--;

            	    stream_taglet.add(ts.getTree());
            	    if (list_ts==null) list_ts=new ArrayList();
            	    list_ts.add(ts);


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            pushFollow(FOLLOW_closer_in_tag477);
            closer26=closer();
            _fsp--;

            stream_closer.add(closer26.getTree());

            	  	if (closer26.type == SCLOSER || closer26.type == DSCLOSER) {
            	  		if(list_ts.size() == 1) {
            	  			Taglet pattern = new Taglet(((taglet_return)list_ts.get(0)).tree);
            	  			ctx_new = new ContextObj(ctx);
            	  			del_ctx(ctx_new, closer26.type, pattern);
            		  		update_ctx(ctx_new, closer26.type, pattern);
            	  		} else {
            	  			throw new RecognitionException();
            	  		}
            	  	}else if (input.toString(closer26.start,closer26.stop).equals(">") || (allow_smx_tag && input.toString(closer26.start,closer26.stop).equals("smx>"))) {		
              			List<Taglet> lt = new ArrayList<Taglet>();
              			for(taglet_return taglt : (List<taglet_return>)list_ts) {
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
            	  
            // smx.g:328:4: ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==WS) ) {
                int LA20_1 = input.LA(2);

                if ( ((LA20_1>=TAG && LA20_1<=TAGLET)||LA20_1==STRING||(LA20_1>=NAME && LA20_1<=ESC)) && (!star)) {
                    alt20=1;
                }
                else if ( ((LA20_1>=NL && LA20_1<=HASH)) ) {
                    alt20=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("328:4: ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] )", 20, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA20_0>=TAG && LA20_0<=TAGLET)||LA20_0==STRING||(LA20_0>=NAME && LA20_0<=ESC)) && (!star)) {
                alt20=1;
            }
            else if ( ((LA20_0>=NL && LA20_0<=HASH)) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("328:4: ( ({...}? => ( WS )? data[ths] ) | children[level, ctx_new, ths] )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // smx.g:328:5: ({...}? => ( WS )? data[ths] )
                    {
                    // smx.g:328:5: ({...}? => ( WS )? data[ths] )
                    // smx.g:328:6: {...}? => ( WS )? data[ths]
                    {
                    if ( !(!star) ) {
                        throw new FailedPredicateException(input, "tag", "!star");
                    }
                    // smx.g:328:17: ( WS )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==WS) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // smx.g:328:17: WS
                            {
                            WS27=(Token)input.LT(1);
                            match(input,WS,FOLLOW_WS_in_tag493); 
                            stream_WS.add(WS27);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_data_in_tag496);
                    data28=data(ths);
                    _fsp--;

                    stream_data.add(data28.getTree());

                    }


                    }
                    break;
                case 2 :
                    // smx.g:328:34: children[level, ctx_new, ths]
                    {
                    pushFollow(FOLLOW_children_in_tag502);
                    children29=children(level,  ctx_new,  ths);
                    _fsp--;

                    stream_children.add(children29.getTree());

                    }
                    break;

            }

             if(star && children29.num <= 0) throw new RecognitionException(); 

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 332:3: ->
            {
                root_0 = null;
            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

            allow_smx_tag=false; // guarnatees that only first line can be smx> 

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tag

    public static class attribs_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attribs
    // smx.g:336:1: attribs : (a+= attr ( WS )? )* -> ^( ATTRIBS ( $a)* ) ;
    public final attribs_return attribs() throws RecognitionException {
        attribs_return retval = new attribs_return();
        retval.start = input.LT(1);
        int attribs_StartIndex = input.index();
        Tree root_0 = null;

        Token WS30=null;
        List list_a=null;
        RuleReturnScope a = null;
        Tree WS30_tree=null;
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleSubtreeStream stream_attr=new RewriteRuleSubtreeStream(adaptor,"rule attr");
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // smx.g:336:8: ( (a+= attr ( WS )? )* -> ^( ATTRIBS ( $a)* ) )
            // smx.g:336:10: (a+= attr ( WS )? )*
            {
            // smx.g:336:10: (a+= attr ( WS )? )*
            loop22:
            do {
                int alt22=2;
                alt22 = dfa22.predict(input);
                switch (alt22) {
            	case 1 :
            	    // smx.g:336:11: a+= attr ( WS )?
            	    {
            	    pushFollow(FOLLOW_attr_in_attribs536);
            	    a=attr();
            	    _fsp--;

            	    stream_attr.add(a.getTree());
            	    if (list_a==null) list_a=new ArrayList();
            	    list_a.add(a);

            	    // smx.g:336:19: ( WS )?
            	    int alt21=2;
            	    int LA21_0 = input.LA(1);

            	    if ( (LA21_0==WS) ) {
            	        alt21=1;
            	    }
            	    switch (alt21) {
            	        case 1 :
            	            // smx.g:336:19: WS
            	            {
            	            WS30=(Token)input.LT(1);
            	            match(input,WS,FOLLOW_WS_in_attribs538); 
            	            stream_WS.add(WS30);


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            // AST REWRITE
            // elements: a
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: a
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"token a",list_a);
            root_0 = (Tree)adaptor.nil();
            // 336:25: -> ^( ATTRIBS ( $a)* )
            {
                // smx.g:336:28: ^( ATTRIBS ( $a)* )
                {
                Tree root_1 = (Tree)adaptor.nil();
                root_1 = (Tree)adaptor.becomeRoot(adaptor.create(ATTRIBS, "ATTRIBS"), root_1);

                // smx.g:336:38: ( $a)*
                while ( stream_a.hasNext() ) {
                    adaptor.addChild(root_1, ((ParserRuleReturnScope)stream_a.next()).getTree());

                }
                stream_a.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end attribs

    public static class attr_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attr
    // smx.g:337:1: attr : NAME ( WS )? EQ ( WS )? ( (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL | v+= STRING ) -> ^( ATTR NAME ( $v)+ ) ;
    public final attr_return attr() throws RecognitionException {
        attr_return retval = new attr_return();
        retval.start = input.LT(1);
        int attr_StartIndex = input.index();
        Tree root_0 = null;

        Token NAME31=null;
        Token WS32=null;
        Token EQ33=null;
        Token WS34=null;
        Token SYMBOLS35=null;
        Token HASH36=null;
        Token BANG37=null;
        Token v=null;
        List list_v=null;

        Tree NAME31_tree=null;
        Tree WS32_tree=null;
        Tree EQ33_tree=null;
        Tree WS34_tree=null;
        Tree SYMBOLS35_tree=null;
        Tree HASH36_tree=null;
        Tree BANG37_tree=null;
        Tree v_tree=null;
        RewriteRuleTokenStream stream_HASH=new RewriteRuleTokenStream(adaptor,"token HASH");
        RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
        RewriteRuleTokenStream stream_WS=new RewriteRuleTokenStream(adaptor,"token WS");
        RewriteRuleTokenStream stream_EQ=new RewriteRuleTokenStream(adaptor,"token EQ");
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleTokenStream stream_SYMBOLS=new RewriteRuleTokenStream(adaptor,"token SYMBOLS");
        RewriteRuleTokenStream stream_SYMBOL=new RewriteRuleTokenStream(adaptor,"token SYMBOL");

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // smx.g:337:5: ( NAME ( WS )? EQ ( WS )? ( (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL | v+= STRING ) -> ^( ATTR NAME ( $v)+ ) )
            // smx.g:337:8: NAME ( WS )? EQ ( WS )? ( (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL | v+= STRING )
            {
            NAME31=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_attr558); 
            stream_NAME.add(NAME31);

            // smx.g:337:13: ( WS )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==WS) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // smx.g:337:13: WS
                    {
                    WS32=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_attr560); 
                    stream_WS.add(WS32);


                    }
                    break;

            }

            EQ33=(Token)input.LT(1);
            match(input,EQ,FOLLOW_EQ_in_attr563); 
            stream_EQ.add(EQ33);

            // smx.g:337:20: ( WS )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==WS) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // smx.g:337:20: WS
                    {
                    WS34=(Token)input.LT(1);
                    match(input,WS,FOLLOW_WS_in_attr565); 
                    stream_WS.add(WS34);


                    }
                    break;

            }

            // smx.g:337:24: ( (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL | v+= STRING )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==HASH||(LA27_0>=SYMBOLS && LA27_0<=SYMBOL)) ) {
                alt27=1;
            }
            else if ( (LA27_0==STRING) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("337:24: ( (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL | v+= STRING )", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // smx.g:337:25: (v+= ( SYMBOLS | HASH | BANG ) )* v+= SYMBOL
                    {
                    // smx.g:337:26: (v+= ( SYMBOLS | HASH | BANG ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==HASH||(LA26_0>=SYMBOLS && LA26_0<=BANG)) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // smx.g:337:26: v+= ( SYMBOLS | HASH | BANG )
                    	    {
                    	    // smx.g:337:28: ( SYMBOLS | HASH | BANG )
                    	    int alt25=3;
                    	    switch ( input.LA(1) ) {
                    	    case SYMBOLS:
                    	        {
                    	        alt25=1;
                    	        }
                    	        break;
                    	    case HASH:
                    	        {
                    	        alt25=2;
                    	        }
                    	        break;
                    	    case BANG:
                    	        {
                    	        alt25=3;
                    	        }
                    	        break;
                    	    default:
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("337:28: ( SYMBOLS | HASH | BANG )", 25, 0, input);

                    	        throw nvae;
                    	    }

                    	    switch (alt25) {
                    	        case 1 :
                    	            // smx.g:337:29: SYMBOLS
                    	            {
                    	            SYMBOLS35=(Token)input.LT(1);
                    	            match(input,SYMBOLS,FOLLOW_SYMBOLS_in_attr572); 
                    	            stream_SYMBOLS.add(SYMBOLS35);


                    	            }
                    	            break;
                    	        case 2 :
                    	            // smx.g:337:37: HASH
                    	            {
                    	            HASH36=(Token)input.LT(1);
                    	            match(input,HASH,FOLLOW_HASH_in_attr574); 
                    	            stream_HASH.add(HASH36);


                    	            }
                    	            break;
                    	        case 3 :
                    	            // smx.g:337:42: BANG
                    	            {
                    	            BANG37=(Token)input.LT(1);
                    	            match(input,BANG,FOLLOW_BANG_in_attr576); 
                    	            stream_BANG.add(BANG37);


                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    v=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_attr582); 
                    stream_SYMBOL.add(v);

                    if (list_v==null) list_v=new ArrayList();
                    list_v.add(v);


                    }
                    break;
                case 2 :
                    // smx.g:337:61: v+= STRING
                    {
                    v=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_attr588); 
                    stream_STRING.add(v);

                    if (list_v==null) list_v=new ArrayList();
                    list_v.add(v);


                    }
                    break;

            }


            // AST REWRITE
            // elements: NAME, v
            // token labels: 
            // rule labels: retval
            // token list labels: v
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_v=new RewriteRuleTokenStream(adaptor,"token v", list_v);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 338:7: -> ^( ATTR NAME ( $v)+ )
            {
                // smx.g:338:10: ^( ATTR NAME ( $v)+ )
                {
                Tree root_1 = (Tree)adaptor.nil();
                root_1 = (Tree)adaptor.becomeRoot(adaptor.create(ATTR, "ATTR"), root_1);

                adaptor.addChild(root_1, stream_NAME.next());
                if ( !(stream_v.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_v.hasNext() ) {
                    adaptor.addChild(root_1, stream_v.next());

                }
                stream_v.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end attr

    public static class closer_return extends ParserRuleReturnScope {
        public int type;
        Tree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start closer
    // smx.g:340:1: closer returns [int type] : (s+= SYMBOLS | s+= HASH | s+= EQ | s+= NAME )* s+= WAKA -> ^( CLOSER ( $s)+ ) ;
    public final closer_return closer() throws RecognitionException {
        closer_return retval = new closer_return();
        retval.start = input.LT(1);
        int closer_StartIndex = input.index();
        Tree root_0 = null;

        Token s=null;
        List list_s=null;

        Tree s_tree=null;
        RewriteRuleTokenStream stream_WAKA=new RewriteRuleTokenStream(adaptor,"token WAKA");
        RewriteRuleTokenStream stream_HASH=new RewriteRuleTokenStream(adaptor,"token HASH");
        RewriteRuleTokenStream stream_NAME=new RewriteRuleTokenStream(adaptor,"token NAME");
        RewriteRuleTokenStream stream_EQ=new RewriteRuleTokenStream(adaptor,"token EQ");
        RewriteRuleTokenStream stream_SYMBOLS=new RewriteRuleTokenStream(adaptor,"token SYMBOLS");

        String text="";
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // smx.g:341:23: ( (s+= SYMBOLS | s+= HASH | s+= EQ | s+= NAME )* s+= WAKA -> ^( CLOSER ( $s)+ ) )
            // smx.g:342:3: (s+= SYMBOLS | s+= HASH | s+= EQ | s+= NAME )* s+= WAKA
            {
            // smx.g:342:3: (s+= SYMBOLS | s+= HASH | s+= EQ | s+= NAME )*
            loop28:
            do {
                int alt28=5;
                switch ( input.LA(1) ) {
                case SYMBOLS:
                    {
                    alt28=1;
                    }
                    break;
                case HASH:
                    {
                    alt28=2;
                    }
                    break;
                case EQ:
                    {
                    alt28=3;
                    }
                    break;
                case NAME:
                    {
                    alt28=4;
                    }
                    break;

                }

                switch (alt28) {
            	case 1 :
            	    // smx.g:342:4: s+= SYMBOLS
            	    {
            	    s=(Token)input.LT(1);
            	    match(input,SYMBOLS,FOLLOW_SYMBOLS_in_closer628); 
            	    stream_SYMBOLS.add(s);

            	    if (list_s==null) list_s=new ArrayList();
            	    list_s.add(s);


            	    }
            	    break;
            	case 2 :
            	    // smx.g:342:15: s+= HASH
            	    {
            	    s=(Token)input.LT(1);
            	    match(input,HASH,FOLLOW_HASH_in_closer632); 
            	    stream_HASH.add(s);

            	    if (list_s==null) list_s=new ArrayList();
            	    list_s.add(s);


            	    }
            	    break;
            	case 3 :
            	    // smx.g:342:23: s+= EQ
            	    {
            	    s=(Token)input.LT(1);
            	    match(input,EQ,FOLLOW_EQ_in_closer636); 
            	    stream_EQ.add(s);

            	    if (list_s==null) list_s=new ArrayList();
            	    list_s.add(s);


            	    }
            	    break;
            	case 4 :
            	    // smx.g:342:29: s+= NAME
            	    {
            	    s=(Token)input.LT(1);
            	    match(input,NAME,FOLLOW_NAME_in_closer640); 
            	    stream_NAME.add(s);

            	    if (list_s==null) list_s=new ArrayList();
            	    list_s.add(s);


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            s=(Token)input.LT(1);
            match(input,WAKA,FOLLOW_WAKA_in_closer646); 
            stream_WAKA.add(s);

            if (list_s==null) list_s=new ArrayList();
            list_s.add(s);

            for(Object o: list_s) {text += ((Token)o).getText();}
            		retval.type = text.equals("*>") ? SCLOSER :  text.equals("**>") ? DSCLOSER : CLOSER;

            // AST REWRITE
            // elements: s
            // token labels: 
            // rule labels: retval
            // token list labels: s
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_s=new RewriteRuleTokenStream(adaptor,"token s", list_s);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Tree)adaptor.nil();
            // 345:3: -> ^( CLOSER ( $s)+ )
            {
                // smx.g:345:6: ^( CLOSER ( $s)+ )
                {
                Tree root_1 = (Tree)adaptor.nil();
                root_1 = (Tree)adaptor.becomeRoot(adaptor.create(CLOSER, "CLOSER"), root_1);

                if ( !(stream_s.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_s.hasNext() ) {
                    adaptor.addChild(root_1, stream_s.next());

                }
                stream_s.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end closer


    protected DFA3 dfa3 = new DFA3(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA3_eotS =
        "\64\uffff";
    static final String DFA3_eofS =
        "\64\uffff";
    static final String DFA3_minS =
        "\1\4\1\21\4\4\1\uffff\1\21\2\uffff\3\4\1\21\5\4\1\21\1\uffff\2\4"+
        "\1\21\15\4\1\21\2\4\1\21\7\4\1\21\3\4";
    static final String DFA3_maxS =
        "\1\35\1\31\4\35\1\uffff\1\31\2\uffff\3\35\1\31\5\35\1\31\1\uffff"+
        "\2\35\1\33\15\35\1\33\2\35\1\33\7\35\1\33\3\35";
    static final String DFA3_acceptS =
        "\6\uffff\1\2\1\uffff\1\4\1\1\12\uffff\1\3\37\uffff";
    static final String DFA3_specialS =
        "\64\uffff}>";
    static final String[] DFA3_transitionS = {
            "\15\10\2\uffff\1\1\1\7\1\6\1\2\1\3\1\5\1\4\4\10",
            "\1\11\1\uffff\1\1\1\uffff\2\6\1\uffff\2\6",
            "\15\10\1\12\1\10\1\15\1\10\1\6\1\16\1\10\1\13\1\14\4\10",
            "\15\10\1\17\1\10\1\15\1\10\1\6\1\20\1\22\1\21\1\14\4\10",
            "\17\10\1\15\1\10\1\6\1\16\1\10\1\21\1\14\4\10",
            "\17\10\1\15\1\10\1\6\1\16\1\10\1\21\1\14\4\10",
            "",
            "\1\24\1\10\1\23\1\uffff\2\24\1\uffff\2\24",
            "",
            "",
            "\17\10\1\15\1\26\1\6\1\25\1\22\1\13\1\14\4\10",
            "\15\10\1\30\1\10\1\27\1\34\1\6\1\16\1\10\1\21\1\31\1\32\1\33"+
            "\2\10",
            "\17\10\1\15\1\10\1\6\1\16\1\10\1\21\1\14\4\10",
            "\1\10\1\uffff\1\15\1\uffff\2\6\1\uffff\2\6",
            "\17\10\1\15\1\10\1\6\1\16\1\10\1\21\1\14\4\10",
            "\17\10\1\15\1\10\1\6\1\20\1\22\1\21\1\14\4\10",
            "\15\10\1\35\1\10\1\15\1\10\1\6\1\16\1\10\1\36\1\14\4\10",
            "\17\10\1\15\1\10\1\6\1\16\1\10\1\21\1\14\4\10",
            "\15\10\1\17\1\10\1\15\1\10\1\6\1\20\1\22\1\21\1\14\4\10",
            "\1\10\1\uffff\1\23\1\uffff\2\24\1\uffff\2\24",
            "",
            "\15\10\1\37\1\10\1\15\1\10\1\6\1\16\1\10\1\13\1\14\4\10",
            "\15\10\1\40\1\10\1\23\1\10\1\24\1\43\1\10\1\42\1\41\4\10",
            "\1\10\1\uffff\1\27\1\uffff\2\6\1\uffff\4\6",
            "\17\10\1\45\1\34\1\uffff\3\10\1\44\1\32\1\33\2\10",
            "\17\10\1\27\1\10\1\6\1\16\1\10\1\21\1\31\1\32\1\33\2\10",
            "\17\10\1\45\1\10\1\uffff\3\10\1\44\1\32\1\33\2\10",
            "\15\10\1\46\1\10\1\15\1\10\1\6\1\25\1\22\1\21\1\14\4\10",
            "\15\10\1\46\1\10\1\15\1\10\1\6\1\25\1\22\1\21\1\14\4\10",
            "\17\10\1\15\1\10\1\6\1\47\1\22\1\36\1\14\4\10",
            "\15\10\1\52\1\10\1\50\1\55\1\6\1\16\1\10\1\21\1\51\1\53\1\54"+
            "\2\10",
            "\21\10\1\uffff\2\10\1\56\5\10",
            "\17\10\1\23\1\10\1\24\1\43\1\10\1\42\1\41\4\10",
            "\17\10\1\23\1\10\1\24\1\43\1\10\1\42\1\41\4\10",
            "\17\10\1\23\1\10\1\24\1\43\1\10\1\42\1\41\4\10",
            "\17\10\1\23\1\10\1\24\1\43\1\10\1\42\1\41\4\10",
            "\17\10\1\45\1\10\1\uffff\3\10\1\44\1\32\1\33\2\10",
            "\1\10\1\uffff\1\45\5\uffff\3\6",
            "\17\10\1\15\1\10\1\6\1\25\1\22\1\21\1\14\4\10",
            "\15\10\1\57\1\10\1\15\1\10\1\6\1\16\1\10\1\36\1\14\4\10",
            "\1\10\1\uffff\1\50\1\uffff\2\6\1\uffff\4\6",
            "\17\10\1\50\1\10\1\6\1\16\1\10\1\21\1\51\1\53\1\54\2\10",
            "\17\10\1\60\1\55\1\uffff\3\10\1\61\1\53\1\54\2\10",
            "\17\10\1\60\1\10\1\uffff\3\10\1\61\1\53\1\54\2\10",
            "\15\10\1\62\1\10\1\15\1\10\1\6\1\47\1\22\1\21\1\14\4\10",
            "\15\10\1\62\1\10\1\15\1\10\1\6\1\47\1\22\1\21\1\14\4\10",
            "\15\10\1\30\1\10\1\45\1\34\1\uffff\3\10\1\44\1\32\1\33\2\10",
            "\21\10\1\uffff\2\10\1\63\5\10",
            "\1\10\1\uffff\1\60\5\uffff\3\6",
            "\17\10\1\60\1\10\1\uffff\3\10\1\61\1\53\1\54\2\10",
            "\17\10\1\15\1\10\1\6\1\47\1\22\1\21\1\14\4\10",
            "\15\10\1\52\1\10\1\60\1\55\1\uffff\3\10\1\61\1\53\1\54\2\10"
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "260:2: ( comment[n] | tag[level, ctx, n] | dtag[ctx, n] | data[n] )";
        }
    }
    static final String DFA16_eotS =
        "\10\uffff";
    static final String DFA16_eofS =
        "\10\uffff";
    static final String DFA16_minS =
        "\1\23\1\21\1\uffff\1\23\1\uffff\1\21\2\23";
    static final String DFA16_maxS =
        "\2\31\1\uffff\1\31\1\uffff\3\33";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\3\uffff";
    static final String DFA16_specialS =
        "\10\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\2\1\uffff\1\2\1\1\3\2",
            "\1\3\1\uffff\1\2\1\uffff\2\2\1\uffff\2\2",
            "",
            "\1\4\1\uffff\3\4\1\5\1\4",
            "",
            "\1\2\1\uffff\1\7\1\2\2\4\1\uffff\1\4\1\6\2\2",
            "\1\7\1\uffff\2\4\1\uffff\1\4\1\6\2\2",
            "\1\7\1\uffff\2\4\1\uffff\1\4\1\6\2\2"
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "296:9: (n= NAME WS )?";
        }
    }
    static final String DFA22_eotS =
        "\7\uffff";
    static final String DFA22_eofS =
        "\7\uffff";
    static final String DFA22_minS =
        "\1\23\1\uffff\2\21\1\uffff\2\23";
    static final String DFA22_maxS =
        "\1\31\1\uffff\1\31\1\33\1\uffff\2\33";
    static final String DFA22_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\2\uffff";
    static final String DFA22_specialS =
        "\7\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\1\1\uffff\1\1\1\2\3\1",
            "",
            "\1\4\1\uffff\1\1\1\uffff\2\1\1\uffff\1\3\1\1",
            "\1\4\1\uffff\1\6\1\4\2\1\1\uffff\1\1\1\5\2\4",
            "",
            "\1\6\1\uffff\2\1\1\uffff\1\1\1\5\2\4",
            "\1\6\1\uffff\2\1\1\uffff\1\1\1\5\2\4"
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "()* loopback of 336:10: (a+= attr ( WS )? )*";
        }
    }
 

    public static final BitSet FOLLOW_line_in_smx119 = new BitSet(new long[]{0x000000003FFBFFF0L});
    public static final BitSet FOLLOW_line_in_smx137 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_smx146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WS_in_line180 = new BitSet(new long[]{0x000000003FF9FFF0L});
    public static final BitSet FOLLOW_comment_in_line187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tag_in_line192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dtag_in_line197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_in_line202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_dot228 = new BitSet(new long[]{0x000000003FFBFFF2L});
    public static final BitSet FOLLOW_HASH_in_comment241 = new BitSet(new long[]{0x00000000000A0000L});
    public static final BitSet FOLLOW_WS_in_comment244 = new BitSet(new long[]{0x000000003FFFFFF0L});
    public static final BitSet FOLLOW_dot_in_comment248 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_NL_in_comment250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WS_in_children272 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_comment_in_children278 = new BitSet(new long[]{0x000000003FFFFFF2L});
    public static final BitSet FOLLOW_NL_in_children283 = new BitSet(new long[]{0x000000003FFFFFF2L});
    public static final BitSet FOLLOW_WS_in_children287 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_NL_in_children290 = new BitSet(new long[]{0x000000003FFFFFF2L});
    public static final BitSet FOLLOW_line_in_children304 = new BitSet(new long[]{0x000000003FFBFFF2L});
    public static final BitSet FOLLOW_data_sub_in_data322 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_comment_in_data326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NL_in_data331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_data_sub348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_data_sub353 = new BitSet(new long[]{0x000000003FD3FFF2L});
    public static final BitSet FOLLOW_set_in_data_sub366 = new BitSet(new long[]{0x000000003FD3FFF2L});
    public static final BitSet FOLLOW_NAME_in_dtag390 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WS_in_dtag392 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_dtag398 = new BitSet(new long[]{0x00000000036A0000L});
    public static final BitSet FOLLOW_WS_in_dtag400 = new BitSet(new long[]{0x0000000003680000L});
    public static final BitSet FOLLOW_closer_in_dtag405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_taglet419 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_WS_in_taglet421 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_attribs_in_taglet425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_taglet_in_tag463 = new BitSet(new long[]{0x0000000003E80000L});
    public static final BitSet FOLLOW_AND_in_tag466 = new BitSet(new long[]{0x0000000003EA0000L});
    public static final BitSet FOLLOW_WS_in_tag468 = new BitSet(new long[]{0x0000000003E80000L});
    public static final BitSet FOLLOW_taglet_in_tag473 = new BitSet(new long[]{0x0000000003E80000L});
    public static final BitSet FOLLOW_closer_in_tag477 = new BitSet(new long[]{0x000000003FDFFFF0L});
    public static final BitSet FOLLOW_WS_in_tag493 = new BitSet(new long[]{0x000000003FD1FFF0L});
    public static final BitSet FOLLOW_data_in_tag496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_children_in_tag502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attr_in_attribs536 = new BitSet(new long[]{0x0000000000420002L});
    public static final BitSet FOLLOW_WS_in_attribs538 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_NAME_in_attr558 = new BitSet(new long[]{0x0000000001020000L});
    public static final BitSet FOLLOW_WS_in_attr560 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_EQ_in_attr563 = new BitSet(new long[]{0x000000000E1A0000L});
    public static final BitSet FOLLOW_WS_in_attr565 = new BitSet(new long[]{0x000000000E180000L});
    public static final BitSet FOLLOW_SYMBOLS_in_attr572 = new BitSet(new long[]{0x000000000E080000L});
    public static final BitSet FOLLOW_HASH_in_attr574 = new BitSet(new long[]{0x000000000E080000L});
    public static final BitSet FOLLOW_BANG_in_attr576 = new BitSet(new long[]{0x000000000E080000L});
    public static final BitSet FOLLOW_SYMBOL_in_attr582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_attr588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOLS_in_closer628 = new BitSet(new long[]{0x0000000003680000L});
    public static final BitSet FOLLOW_HASH_in_closer632 = new BitSet(new long[]{0x0000000003680000L});
    public static final BitSet FOLLOW_EQ_in_closer636 = new BitSet(new long[]{0x0000000003680000L});
    public static final BitSet FOLLOW_NAME_in_closer640 = new BitSet(new long[]{0x0000000003680000L});
    public static final BitSet FOLLOW_WAKA_in_closer646 = new BitSet(new long[]{0x0000000000000002L});

}