// $ANTLR 3.0.1 smx.g 2008-02-12 13:32:22

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class smxLexer extends Lexer {
    public static final int WAKA=21;
    public static final int HASH=19;
    public static final int DSCLOSER=11;
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
    public static final int SYMBOLS=25;
    public static final int SYMBOL=27;
    public static final int Tokens=30;
    public static final int CHILDREN=5;
    public static final int CLOSER=13;
    public static final int TAG=4;
    public static final int NAME=22;
    public static final int PCHAR=28;
    public static final int DTAG=7;
    public smxLexer() {;} 
    public smxLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "smx.g"; }

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // smx.g:349:3: ( ( ' ' | '\\t' )+ )
            // smx.g:349:5: ( ' ' | '\\t' )+
            {
            // smx.g:349:5: ( ' ' | '\\t' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\t'||LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // smx.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start NL
    public final void mNL() throws RecognitionException {
        try {
            int _type = NL;
            // smx.g:350:4: ( '\\n' )
            // smx.g:350:7: '\\n'
            {
            match('\n'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NL

    // $ANTLR start SYMBOLS
    public final void mSYMBOLS() throws RecognitionException {
        try {
            int _type = SYMBOLS;
            // smx.g:351:8: ( SYMBOL ( SYMBOL | '&' | BANG )* )
            // smx.g:351:10: SYMBOL ( SYMBOL | '&' | BANG )*
            {
            mSYMBOL(); 
            // smx.g:351:17: ( SYMBOL | '&' | BANG )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='!'||(LA2_0>='$' && LA2_0<='&')||(LA2_0>='(' && LA2_0<='/')||(LA2_0>=':' && LA2_0<='<')||(LA2_0>='?' && LA2_0<='@')||(LA2_0>='[' && LA2_0<='`')||(LA2_0>='{' && LA2_0<='~')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // smx.g:
            	    {
            	    if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='/')||(input.LA(1)>=':' && input.LA(1)<='<')||(input.LA(1)>='?' && input.LA(1)<='@')||(input.LA(1)>='[' && input.LA(1)<='`')||(input.LA(1)>='{' && input.LA(1)<='~') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SYMBOLS

    // $ANTLR start NAME
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            // smx.g:352:5: ( ( PCHAR | BANG ) ( PCHAR | BANG | SYMBOL | HASH | '&' )* )
            // smx.g:352:8: ( PCHAR | BANG ) ( PCHAR | BANG | SYMBOL | HASH | '&' )*
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // smx.g:352:21: ( PCHAR | BANG | SYMBOL | HASH | '&' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='\b')||(LA3_0>='\u000B' && LA3_0<='\u001F')||LA3_0=='!'||(LA3_0>='#' && LA3_0<='&')||(LA3_0>='(' && LA3_0<='<')||(LA3_0>='?' && LA3_0<='\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // smx.g:
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='<')||(input.LA(1)>='?' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NAME

    // $ANTLR start WAKA
    public final void mWAKA() throws RecognitionException {
        try {
            int _type = WAKA;
            // smx.g:353:5: ( '>' )
            // smx.g:353:8: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WAKA

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // smx.g:354:5: ( '&&' )
            // smx.g:354:7: '&&'
            {
            match("&&"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start EQ
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            // smx.g:355:4: ( '=' )
            // smx.g:355:6: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQ

    // $ANTLR start HASH
    public final void mHASH() throws RecognitionException {
        try {
            int _type = HASH;
            // smx.g:356:5: ( '#' )
            // smx.g:356:7: '#'
            {
            match('#'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HASH

    // $ANTLR start BANG
    public final void mBANG() throws RecognitionException {
        try {
            // smx.g:359:5: ( '!' )
            // smx.g:359:9: '!'
            {
            match('!'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end BANG

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int num=0;
            // smx.g:364:2: ( ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' ) | ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' ) | ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' ) | ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) )
            int alt10=4;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\'') ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1=='\'') ) {
                    int LA10_3 = input.LA(3);

                    if ( (LA10_3=='\'') ) {
                        alt10=1;
                    }
                    else {
                        alt10=4;}
                }
                else if ( ((LA10_1>='\u0000' && LA10_1<='\t')||(LA10_1>='\u000B' && LA10_1<='&')||(LA10_1>='(' && LA10_1<='\uFFFE')) ) {
                    alt10=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: STRING : ( ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' ) | ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' ) | ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' ) | ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) );", 10, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA10_0=='\"') ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2=='\"') ) {
                    int LA10_5 = input.LA(3);

                    if ( (LA10_5=='\"') ) {
                        alt10=2;
                    }
                    else {
                        alt10=3;}
                }
                else if ( ((LA10_2>='\u0000' && LA10_2<='\t')||(LA10_2>='\u000B' && LA10_2<='!')||(LA10_2>='#' && LA10_2<='\uFFFE')) ) {
                    alt10=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("361:1: STRING : ( ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' ) | ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' ) | ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' ) | ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) );", 10, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("361:1: STRING : ( ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' ) | ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' ) | ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' ) | ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // smx.g:364:4: ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' )
                    {
                    // smx.g:364:4: ( '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\'' )
                    // smx.g:364:5: '\\'\\'\\'' ( options {greedy=false; } : ( ESC | . ) )* '\\'\\'\\''
                    {
                    match("\'\'\'"); 

                    // smx.g:364:14: ( options {greedy=false; } : ( ESC | . ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='\'') ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1=='\'') ) {
                                int LA5_3 = input.LA(3);

                                if ( (LA5_3=='\'') ) {
                                    alt5=2;
                                }
                                else if ( ((LA5_3>='\u0000' && LA5_3<='&')||(LA5_3>='(' && LA5_3<='\uFFFE')) ) {
                                    alt5=1;
                                }


                            }
                            else if ( ((LA5_1>='\u0000' && LA5_1<='&')||(LA5_1>='(' && LA5_1<='\uFFFE')) ) {
                                alt5=1;
                            }


                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<='&')||(LA5_0>='(' && LA5_0<='\uFFFE')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // smx.g:364:39: ( ESC | . )
                    	    {
                    	    // smx.g:364:39: ( ESC | . )
                    	    int alt4=2;
                    	    int LA4_0 = input.LA(1);

                    	    if ( (LA4_0=='\\') ) {
                    	        int LA4_1 = input.LA(2);

                    	        if ( (LA4_1=='\'') ) {
                    	            alt4=1;
                    	        }
                    	        else if ( (LA4_1=='\\') ) {
                    	            alt4=1;
                    	        }
                    	        else if ( ((LA4_1>='\u0000' && LA4_1<='&')||(LA4_1>='(' && LA4_1<='[')||(LA4_1>=']' && LA4_1<='\uFFFE')) ) {
                    	            alt4=1;
                    	        }
                    	        else {
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("364:39: ( ESC | . )", 4, 1, input);

                    	            throw nvae;
                    	        }
                    	    }
                    	    else if ( ((LA4_0>='\u0000' && LA4_0<='[')||(LA4_0>=']' && LA4_0<='\uFFFE')) ) {
                    	        alt4=2;
                    	    }
                    	    else {
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("364:39: ( ESC | . )", 4, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt4) {
                    	        case 1 :
                    	            // smx.g:364:40: ESC
                    	            {
                    	            mESC(); 

                    	            }
                    	            break;
                    	        case 2 :
                    	            // smx.g:364:44: .
                    	            {
                    	            matchAny(); 

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    match("\'\'\'"); 

                    num=3;

                    }


                    }
                    break;
                case 2 :
                    // smx.g:365:7: ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' )
                    {
                    // smx.g:365:7: ( '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"' )
                    // smx.g:365:8: '\"\"\"' ( options {greedy=false; } : ( ESC | . ) )* '\"\"\"'
                    {
                    match("\"\"\""); 

                    // smx.g:365:14: ( options {greedy=false; } : ( ESC | . ) )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0=='\"') ) {
                            int LA7_1 = input.LA(2);

                            if ( (LA7_1=='\"') ) {
                                int LA7_3 = input.LA(3);

                                if ( (LA7_3=='\"') ) {
                                    alt7=2;
                                }
                                else if ( ((LA7_3>='\u0000' && LA7_3<='!')||(LA7_3>='#' && LA7_3<='\uFFFE')) ) {
                                    alt7=1;
                                }


                            }
                            else if ( ((LA7_1>='\u0000' && LA7_1<='!')||(LA7_1>='#' && LA7_1<='\uFFFE')) ) {
                                alt7=1;
                            }


                        }
                        else if ( ((LA7_0>='\u0000' && LA7_0<='!')||(LA7_0>='#' && LA7_0<='\uFFFE')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // smx.g:365:39: ( ESC | . )
                    	    {
                    	    // smx.g:365:39: ( ESC | . )
                    	    int alt6=2;
                    	    int LA6_0 = input.LA(1);

                    	    if ( (LA6_0=='\\') ) {
                    	        int LA6_1 = input.LA(2);

                    	        if ( (LA6_1=='\"') ) {
                    	            alt6=1;
                    	        }
                    	        else if ( (LA6_1=='\\') ) {
                    	            alt6=1;
                    	        }
                    	        else if ( ((LA6_1>='\u0000' && LA6_1<='!')||(LA6_1>='#' && LA6_1<='[')||(LA6_1>=']' && LA6_1<='\uFFFE')) ) {
                    	            alt6=1;
                    	        }
                    	        else {
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("365:39: ( ESC | . )", 6, 1, input);

                    	            throw nvae;
                    	        }
                    	    }
                    	    else if ( ((LA6_0>='\u0000' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='\uFFFE')) ) {
                    	        alt6=2;
                    	    }
                    	    else {
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("365:39: ( ESC | . )", 6, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt6) {
                    	        case 1 :
                    	            // smx.g:365:40: ESC
                    	            {
                    	            mESC(); 

                    	            }
                    	            break;
                    	        case 2 :
                    	            // smx.g:365:44: .
                    	            {
                    	            matchAny(); 

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match("\"\"\""); 

                    num=3;

                    }


                    }
                    break;
                case 3 :
                    // smx.g:366:7: ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' )
                    {
                    // smx.g:366:7: ( '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"' )
                    // smx.g:366:8: '\"' ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // smx.g:366:12: ( ESC | ~ ( '\\\\' | '\\n' | '\"' ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0>='\u0000' && LA8_0<='\t')||(LA8_0>='\u000B' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='\uFFFE')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // smx.g:366:13: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // smx.g:366:17: ~ ( '\\\\' | '\\n' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    match('\"'); 
                    num=1;

                    }


                    }
                    break;
                case 4 :
                    // smx.g:367:7: ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
                    {
                    // smx.g:367:7: ( '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\'' )
                    // smx.g:367:8: '\\'' ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )* '\\''
                    {
                    match('\''); 
                    // smx.g:367:13: ( ESC | ~ ( '\\\\' | '\\n' | '\\'' ) )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0>='\u0000' && LA9_0<='\t')||(LA9_0>='\u000B' && LA9_0<='&')||(LA9_0>='(' && LA9_0<='[')||(LA9_0>=']' && LA9_0<='\uFFFE')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // smx.g:367:14: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // smx.g:367:18: ~ ( '\\\\' | '\\n' | '\\'' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    match('\''); 
                    num=1;

                    }


                    }
                    break;

            }
            this.type = _type;
            setText(getText().substring(num, getText().length()-num));    }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start ESC
    public final void mESC() throws RecognitionException {
        try {
            // smx.g:371:13: ( '\\\\' . )
            // smx.g:371:17: '\\\\' .
            {
            match('\\'); 
            matchAny(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end ESC

    // $ANTLR start PCHAR
    public final void mPCHAR() throws RecognitionException {
        try {
            // smx.g:372:15: (~ ( NL | ' ' | '\\t' | '\"' | '\\'' | WAKA | SYMBOL | HASH | EQ | '&' ) )
            // smx.g:372:17: ~ ( NL | ' ' | '\\t' | '\"' | '\\'' | WAKA | SYMBOL | HASH | EQ | '&' )
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end PCHAR

    // $ANTLR start SYMBOL
    public final void mSYMBOL() throws RecognitionException {
        try {
            // smx.g:375:7: ( ( '$' | '%' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | ':' | ';' | '<' | '?' | '@' | '[' | '\\\\' | ']' | '^' | '_' | '`' | '{' | '|' | '}' | '~' ) )
            // smx.g:375:9: ( '$' | '%' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | ':' | ';' | '<' | '?' | '@' | '[' | '\\\\' | ']' | '^' | '_' | '`' | '{' | '|' | '}' | '~' )
            {
            if ( (input.LA(1)>='$' && input.LA(1)<='%')||(input.LA(1)>='(' && input.LA(1)<='/')||(input.LA(1)>=':' && input.LA(1)<='<')||(input.LA(1)>='?' && input.LA(1)<='@')||(input.LA(1)>='[' && input.LA(1)<='`')||(input.LA(1)>='{' && input.LA(1)<='~') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // smx.g:1:8: ( WS | NL | SYMBOLS | NAME | WAKA | AND | EQ | HASH | STRING )
        int alt11=9;
        int LA11_0 = input.LA(1);

        if ( (LA11_0=='\t'||LA11_0==' ') ) {
            alt11=1;
        }
        else if ( (LA11_0=='\n') ) {
            alt11=2;
        }
        else if ( ((LA11_0>='$' && LA11_0<='%')||(LA11_0>='(' && LA11_0<='/')||(LA11_0>=':' && LA11_0<='<')||(LA11_0>='?' && LA11_0<='@')||(LA11_0>='[' && LA11_0<='`')||(LA11_0>='{' && LA11_0<='~')) ) {
            alt11=3;
        }
        else if ( ((LA11_0>='\u0000' && LA11_0<='\b')||(LA11_0>='\u000B' && LA11_0<='\u001F')||LA11_0=='!'||(LA11_0>='0' && LA11_0<='9')||(LA11_0>='A' && LA11_0<='Z')||(LA11_0>='a' && LA11_0<='z')||(LA11_0>='\u007F' && LA11_0<='\uFFFE')) ) {
            alt11=4;
        }
        else if ( (LA11_0=='>') ) {
            alt11=5;
        }
        else if ( (LA11_0=='&') ) {
            alt11=6;
        }
        else if ( (LA11_0=='=') ) {
            alt11=7;
        }
        else if ( (LA11_0=='#') ) {
            alt11=8;
        }
        else if ( (LA11_0=='\"'||LA11_0=='\'') ) {
            alt11=9;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( WS | NL | SYMBOLS | NAME | WAKA | AND | EQ | HASH | STRING );", 11, 0, input);

            throw nvae;
        }
        switch (alt11) {
            case 1 :
                // smx.g:1:10: WS
                {
                mWS(); 

                }
                break;
            case 2 :
                // smx.g:1:13: NL
                {
                mNL(); 

                }
                break;
            case 3 :
                // smx.g:1:16: SYMBOLS
                {
                mSYMBOLS(); 

                }
                break;
            case 4 :
                // smx.g:1:24: NAME
                {
                mNAME(); 

                }
                break;
            case 5 :
                // smx.g:1:29: WAKA
                {
                mWAKA(); 

                }
                break;
            case 6 :
                // smx.g:1:34: AND
                {
                mAND(); 

                }
                break;
            case 7 :
                // smx.g:1:38: EQ
                {
                mEQ(); 

                }
                break;
            case 8 :
                // smx.g:1:41: HASH
                {
                mHASH(); 

                }
                break;
            case 9 :
                // smx.g:1:46: STRING
                {
                mSTRING(); 

                }
                break;

        }

    }


 

}