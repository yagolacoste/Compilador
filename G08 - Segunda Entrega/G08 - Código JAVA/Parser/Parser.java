//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 1 "G08 - Gramatica - 25102020.y"


package Parser;

import Lexer.LexerAnalyzer;
import SemanticAnalyzer.SemanticAnalyzer;
import java.util.ArrayList;
import java.util.List;
import Lexer.Token;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;
import SymbolTable.Parameter;
import SymbolTable.Parameter;
import SyntacticTree.*;
import semantic_Actions.SemanticAction;


//#line 36 "Parser.java"




public class Parser
{

    boolean yydebug;        //do I want debug output?
    int yynerrs;            //number of errors so far
    int yyerrflag;          //was there an error?
    int yychar;             //the current working character

    //########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
    void debug(String msg)
    {
        if (yydebug)
            System.out.println(msg);
    }

    //########## STATE STACK ##########
    final static int YYSTACKSIZE = 500;  //maximum stack size
    int statestk[] = new int[YYSTACKSIZE]; //state stack
    int stateptr;
    int stateptrmax;                     //highest index of stackptr
    int statemax;                        //state when highest index reached
    //###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
    final void state_push(int state)
    {
        try {
            stateptr++;
            statestk[stateptr]=state;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            int oldsize = statestk.length;
            int newsize = oldsize * 2;
            int[] newstack = new int[newsize];
            System.arraycopy(statestk,0,newstack,0,oldsize);
            statestk = newstack;
            statestk[stateptr]=state;
        }
    }
    final int state_pop()
    {
        return statestk[stateptr--];
    }
    final void state_drop(int cnt)
    {
        stateptr -= cnt;
    }
    final int state_peek(int relative)
    {
        return statestk[stateptr-relative];
    }
    //###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
    final boolean init_stacks()
    {
        stateptr = -1;
        val_init();
        return true;
    }
    //###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
    void dump_stacks(int count)
    {
        int i;
        System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
        for (i=0;i<count;i++)
            System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
        System.out.println("======================");
    }


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


    String   yytext;//user variable to return contextual strings
    ParserVal yyval; //used to return semantic vals from action routines
    ParserVal yylval;//the 'lval' (result) I got from yylex()
    ParserVal valstk[];
    int valptr;
    //###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
    void val_init()
    {
        valstk=new ParserVal[YYSTACKSIZE];
        yyval=new ParserVal();
        yylval=new ParserVal();
        valptr=-1;
    }
    void val_push(ParserVal val)
    {
        if (valptr>=YYSTACKSIZE)
            return;
        valstk[++valptr]=val;
    }
    ParserVal val_pop()
    {
        if (valptr<0)
            return new ParserVal();
        return valstk[valptr--];
    }
    void val_drop(int cnt)
    {
        int ptr;
        ptr=valptr-cnt;
        if (ptr<0)
            return;
        valptr = ptr;
    }
    ParserVal val_peek(int relative)
    {
        int ptr;
        ptr=valptr-relative;
        if (ptr<0)
            return new ParserVal();
        return valstk[ptr];
    }
    final ParserVal dup_yyval(ParserVal val)
    {
        ParserVal dup = new ParserVal();
        dup.ival = val.ival;
        dup.dval = val.dval;
        dup.sval = val.sval;
        dup.obj = val.obj;
        return dup;
    }
    //#### end semantic value section ####
    public final static short ID=257;
    public final static short ULONGINT=258;
    public final static short IF=259;
    public final static short THEN=260;
    public final static short ELSE=261;
    public final static short END_IF=262;
    public final static short FOR=263;
    public final static short OUT=264;
    public final static short PROC=265;
    public final static short RETURN=266;
    public final static short DOUBLE=267;
    public final static short MENOR_IGUAL=268;
    public final static short MAYOR_IGUAL=269;
    public final static short IGUAL=270;
    public final static short DISTINTO=271;
    public final static short PUNTO_PUNTO=272;
    public final static short UP=273;
    public final static short DOWN=274;
    public final static short CADENA=275;
    public final static short NA=276;
    public final static short NRO_DOUBLE=277;
    public final static short NRO_ULONGINT=278;
    public final static short YYERRCODE=256;
    final static short yylhs[] = {                           -1,
            0,    0,    1,    1,    2,    2,    3,    3,    3,    3,
            7,    7,    7,    8,    8,    8,    8,   10,   10,   11,
            11,   11,   11,   12,   12,   12,    9,    9,    9,   14,
            14,   14,   14,   13,   13,    4,    4,    4,    4,    4,
            4,    4,    4,    4,    6,    6,    5,    5,   19,   19,
            19,   19,   19,   19,   19,   19,   15,   15,   15,   21,
            21,   21,   21,   20,   20,   20,   20,   20,   20,   20,
            20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
            20,   20,   20,   22,   22,   22,   22,   23,   23,   23,
            23,   26,   26,   26,   26,   26,   25,   25,   17,   17,
            17,   17,   16,   16,   16,   16,   16,   16,   16,   16,
            16,   28,   28,   28,   28,   29,   29,   29,   29,   29,
            29,   29,   29,   29,   29,   29,   29,   29,   29,   29,
            29,   29,   29,   29,   30,   30,   30,   30,   30,   18,
            18,   18,   18,   18,   18,   31,   31,   31,   31,   24,
            24,   24,   24,   24,   24,   24,   32,   32,   32,   32,
            32,   32,   32,   33,   33,   33,   33,   33,   27,   27,
            27,
    };
    final static short yylen[] = {                            2,
            2,    1,    1,    2,    1,    2,    3,    2,    3,    3,
            2,    2,    2,    3,    3,    3,    3,    2,    2,    3,
            5,    7,    2,    3,    3,    3,    3,    3,    3,    1,
            2,    1,    2,    2,    2,    2,    2,    1,    2,    2,
            2,    2,    2,    2,    1,    3,    1,    1,    4,    4,
            6,    6,    8,    8,    3,    3,    3,    3,    3,    3,
            2,    3,    2,    5,    5,    5,    5,    5,    5,    5,
            5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
            5,    3,    2,    3,    1,    3,    2,    4,    2,    4,
            3,    4,    1,    4,    3,    2,    1,    2,    3,    3,
            3,    3,    9,    9,    9,    9,    9,    9,    9,    9,
            9,    3,    3,    3,    3,    3,    3,    3,    3,    3,
            3,    3,    3,    3,    3,    3,    3,    2,    3,    3,
            3,    3,    3,    3,    2,    2,    2,    2,    2,    4,
            4,    3,    3,    4,    2,    4,    4,    4,    4,    3,
            3,    1,    3,    3,    3,    3,    3,    3,    1,    3,
            3,    3,    3,    1,    1,    2,    1,    1,    3,    3,
            1,
    };
    final static short yydefred[] = {                         0,
            0,    0,   47,    0,    0,    0,    0,   48,    0,    0,
            0,    3,    5,    0,    0,    0,    0,    0,   38,    0,
            0,    0,    0,    0,   45,    0,    0,    0,    0,   13,
            0,    0,    0,    0,    0,    0,    0,    0,  145,    0,
            0,   19,   18,    0,    4,    0,    6,    0,    0,    8,
            0,   11,    0,    0,    0,   37,   36,   40,   39,   42,
            41,   44,   43,    0,    0,   32,   30,    0,    0,    0,
            0,    0,    0,    0,    0,    0,  165,  164,    0,    0,
            167,  168,    0,  159,    0,    9,    0,    0,   17,  170,
            169,    0,   55,    0,   85,   59,    0,    0,    0,    0,
            57,    0,    0,    0,    0,    0,  142,  143,    0,    0,
            0,   10,    7,   16,   23,   15,   14,    0,    0,    0,
            29,   33,   31,   35,   50,    0,   34,   20,    0,    0,
            0,    0,    0,    0,    0,  166,    0,    0,    0,    0,
            0,   27,   46,    0,    0,   49,    0,    0,   97,    0,
            63,    0,   61,    0,   82,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,  141,  140,
            144,   56,    0,    0,    0,    0,    0,    0,  162,  163,
            0,    0,    0,    0,    0,    0,    0,    0,  157,    0,
            158,   26,   25,   24,    0,    0,   84,   98,    0,   89,
            62,   60,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,  115,  114,  113,  112,    0,    0,
            0,    0,    0,    0,    0,   52,    0,   21,    0,  149,
            148,  147,  146,   51,    0,    0,    0,   80,   74,   68,
            79,   73,   67,   78,   72,   66,   81,   75,   69,   76,
            70,   64,   77,   71,   65,    0,    0,    0,    0,    0,
            0,  128,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,   90,   88,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,   54,   22,   53,
            139,  138,  135,  137,  136,    0,    0,    0,    0,    0,
            0,    0,    0,    0,   93,  111,  110,  109,  108,  107,
            106,  105,    0,  103,    0,    0,    0,    0,   94,   92,
    };
    final static short yydgoto[] = {                          9,
            10,   11,   66,  325,   14,   29,   15,   16,   30,   17,
            31,   89,   73,   68,   18,   19,   20,   21,   22,   36,
            96,   97,  154,   80,  150,  326,   81,  104,  221,  301,
            82,   83,   84,
    };
    final static short yysindex[] = {                       349,
            44,  -21,    0,  -27,  -17,  -35,  331,    0,    0,  349,
            -70,    0,    0,  337,   -1,  -97,    6,  -52,    0,  -50,
            -39,  -38,  -43,  349,    0,  -24,  -42,  349,   15,    0,
            -185,  403,   19,  -79,   49,  379,  424,  426,    0,   35,
            -33,    0,    0,  -70,    0,   68,    0,   98,   21,    0,
            349,    0, -185,  230, -214,    0,    0,    0,    0,    0,
            0,    0,    0,  -42,   74,    0,    0,  362, -142,   52,
            0, -117,   81,  670, -111,  142,    0,    0,  -85,   89,
            0,    0,  482,    0,  411,    0,  -59,  -25,    0,    0,
            0,  493,    0,  304,    0,    0, -150,  670,  514,   68,
            0,  147,   43,  153,  114,  -37,    0,    0,   29,  -40,
            25,    0,    0,    0,    0,    0,    0,   89,  670,   89,
            0,    0,    0,    0,    0,   27,    0,    0,  -77,   76,
            76,   88,   88,  -42,   90,    0,   92,  107,  115,  119,
            44,    0,    0,  -58, -229,    0,   77,   68,    0,  540,
            0,  388,    0, -205,    0,  121,  123,  138,  146,  150,
            152,   60,   64, -217,  428,  428,  428,  440,    0,    0,
            0,    0,  497,  502,  466,  482,  482,  273,    0,    0,
            521,  662,   34,  466,  482,  466,  482,  273,    0,  273,
            0,    0,    0,    0,  539,   68,    0,    0,  322,    0,
            0,    0,  777,   47,  795,   73,  803,  131,  811,  315,
            819,  377,  827,  402,    0,    0,    0,    0,  329,  321,
            291,  314,  421,  419,  -12,    0,  227,    0,  -77,    0,
            0,    0,    0,    0,  237,    3,  552,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,  -42,  -42,  -42,  -42,  -42,
            -42,    0,  154,  177,  179,  181,  183,  185, -206, -206,
            -206, -206, -206, -130,  475,  477,  486,    0,    0,   89,
            89,   89,   89,   89,   89,  670,   89,  670,   89,  670,
            89,  670,   89,  670,   89,  670,   89,  226, -215, -200,
            491,  507,  524,  532,  534,  -41,   56,    0,    0,    0,
            0,    0,    0,    0,    0,  481,  481,  481,  481,  481,
            481,  481,  508,  398,    0,    0,    0,    0,    0,    0,
            0,    0,   68,    0,   68,  570,   68,   12,    0,    0,
    };
    final static short yyrindex[] = {                         0,
            0,   59,    0,    0,    0,    0,    0,    0,    0,    0,
            582,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,  584,    0,    0,    0,    0,    0,    0,
            448,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            -49,    0,    0,    0,   40,    0,    0,    0,    0,   14,
            0,    0,  450,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,  530,    0,   42,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,   48,   51,   58,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            551,    0,    0,    0,    0,    0,    0,  -60,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,  455,  460,    0,    0,    0,
            0,    0,    0,  480,  485,  490,  510,  423,    0,  430,
            0,    0,    0,    0,    0,  -51,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0, -126,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,   79,
            80,   83,  106,  243,  302,  345,  353,  355,  366,  367,
            390,  392,  394,  408,  410,  415,  417,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
            0,    0,    2,    0,   16,    0,   30,    0,    0,    0,
    };
    final static short yygindex[] = {                         0,
            0,  605,  103,    4,  -20,  603,    0,    0,  608,    0,
            611,  501,  -98,  602,    0,    0,    0,    0,    0,    0,
            596,    0,    0,  -11, -166,  558,    1,  599,  473,  503,
            0,   32,  463,
    };
    final static int YYTABLESIZE=881;
    static short yytable[];
    static { yytable();}
    static void yytable(){
        yytable = new short[]{                        321,
                23,  104,   79,   13,   41,   72,   57,  110,   59,   56,
                23,   23,   35,   13,   47,   96,   71,   65,   33,   61,
                63,  168,   38,   99,   23,   28,  193,   67,   23,   95,
                174,   67,  237,   72,   23,  145,   23,   95,  217,   95,
                312,  116,  111,   94,   23,   54,  274,   47,  194,  298,
                201,   23,  118,  120,   67,  314,  202,   50,   87,   93,
                218,   88,  313,   27,   87,  172,  299,  300,   23,  170,
                340,  123,  102,   86,  233,  107,  137,  315,  138,  113,
                171,  171,  171,   26,  171,   23,  171,  240,  123,  137,
                88,  138,  125,   79,   23,  126,  323,  149,  171,  171,
                58,  171,   12,  164,   27,  151,  101,  111,   72,  100,
                152,  153,   45,  243,  124,  137,   99,  138,   79,  171,
                79,  128,  181,  183,  129,  306,  104,  278,   27,   91,
                276,  137,   79,  138,   79,   91,   79,  133,  132,  127,
                96,  131,  299,  300,  204,  206,  208,  210,  212,  214,
                23,   79,   23,  198,   95,  200,  112,  336,   51,   79,
                32,  176,  177,   79,  134,   79,   28,   79,  185,  187,
                122,  246,  166,  137,  162,  138,   46,    2,   69,    4,
                3,  135,   79,    5,    6,   46,    2,  122,    4,    8,
                79,  136,    5,    6,   79,   87,   79,  143,   79,   23,
                87,   87,  149,   56,   86,   58,   56,  162,   72,   86,
                86,  165,   64,   74,   75,  171,   60,   62,  167,  192,
                39,   79,  108,   79,   76,   79,   23,   79,   34,   79,
                144,   69,   70,    3,   77,   78,  311,   23,   37,   40,
                198,  109,    8,  273,  280,  281,  282,  283,  284,  285,
                32,  287,  289,  291,  293,  295,  297,  104,  104,  104,
                104,   53,  104,  104,  104,  104,  104,  339,  104,  102,
                115,   96,   96,   96,   96,   92,   96,   96,   96,   96,
                96,   70,   96,  173,  169,   95,   95,   95,   95,  232,
                95,   95,   95,   95,   95,  171,   95,   58,  163,   24,
                25,  129,  239,  101,   98,   75,  100,  171,  171,  171,
                171,  322,  134,   99,  171,   76,   23,   23,   23,   23,
                23,   23,   23,   23,   23,   77,   78,  149,  242,  119,
                75,  175,   75,  195,  133,  132,   23,  215,  131,  198,
                76,  216,   76,  178,   75,  182,   75,  184,   75,  269,
                77,   78,   77,   78,   76,  249,   76,  137,   76,  138,
                130,  134,  186,   75,   77,   78,   77,   78,   77,   78,
                188,   75,  270,   76,  190,   75,  203,   75,  205,   75,
                267,   76,  268,   77,   78,   76,  245,   76,  260,   76,
                261,   77,   78,  207,   75,   77,   78,   77,   78,   77,
                78,  209,   75,  126,   76,  211,   75,  213,   75,  286,
                75,  120,   76,  125,   77,   78,   76,  252,   76,  137,
                76,  138,   77,   78,  119,  124,   77,   78,   77,   78,
                77,   78,  288,   75,  290,   75,  292,   75,  294,   75,
                296,   75,  255,   76,  137,   76,  138,   76,  118,   76,
                127,   76,  121,   77,   78,   77,   78,   77,   78,   77,
                78,   77,   78,  160,  160,  160,  122,  160,  116,  160,
                161,  161,  161,  123,  161,  117,  161,  272,  260,  271,
                261,  160,  160,  275,  160,   69,  121,    3,  161,  161,
                152,  161,  152,  277,  152,  154,    8,  154,  129,  154,
                156,   94,  156,  311,  156,  134,   12,  132,  152,  152,
                199,  152,  133,  154,  154,  308,  154,  309,  156,  156,
                153,  156,  153,  139,  153,  150,  310,  150,  140,  150,
                155,  316,  155,  146,  155,  142,  147,  226,  153,  153,
                227,  153,  228,  150,  150,  229,  150,  317,  155,  155,
                151,  155,  151,  114,  151,  117,  137,  130,  138,  148,
                2,  230,    4,  137,  318,  138,    5,    6,  151,  151,
                248,  151,  319,  160,  320,  161,  262,  236,    2,  234,
                4,    2,  235,    1,    5,    6,   42,   43,  263,  264,
                265,  266,   48,   25,  179,  180,  256,  257,  258,  259,
                126,  189,  191,  324,    1,    2,    3,    4,  120,   28,
                125,    5,    6,    7,   44,    8,   49,    1,    2,    3,
                4,  119,  124,   52,    5,    6,    7,   55,    8,   85,
                324,  101,  251,    0,  100,    2,  106,    4,  222,  223,
                225,    5,    6,   46,    2,  118,    4,  127,    0,  121,
                5,    6,   83,  335,    2,    0,    4,  254,   90,   91,
                5,    6,    0,  122,  197,  116,  141,    2,    3,    4,
                123,    0,  117,    5,    6,    7,  279,    8,  160,  102,
                103,  105,  103,  219,  220,  161,  256,  257,  258,  259,
                160,  160,  160,  160,  338,  224,  220,  161,  161,  161,
                161,  134,  231,  132,  130,  152,  131,    0,  133,  134,
                154,  132,  130,    0,  131,  156,  133,  152,  152,  152,
                152,    0,  154,  154,  154,  154,    0,  156,  156,  156,
                156,    0,    0,    0,    0,  153,   46,    2,    0,    4,
                150,    0,    0,    5,    6,  155,    0,  153,  153,  153,
                153,    0,  150,  150,  150,  150,    0,  155,  155,  155,
                155,    0,    0,  333,    2,  151,    4,    0,    0,  155,
                5,    6,  302,  303,  304,  305,  307,  151,  151,  151,
                151,  156,  157,  158,  159,   83,   83,    0,   83,    0,
                0,    0,   83,   83,    0,  196,    2,    0,    4,    0,
                0,    0,    5,    6,    0,    0,    0,   46,    2,    0,
                4,    0,    0,    0,    5,    6,  134,  238,  132,  130,
                0,  131,    0,  133,    0,  337,    2,    0,    4,    0,
                0,    0,    5,    6,  134,  241,  132,  130,    0,  131,
                0,  133,  134,  244,  132,  130,    0,  131,    0,  133,
                134,  247,  132,  130,    0,  131,    0,  133,  134,  250,
                132,  130,    0,  131,    0,  133,  134,  253,  132,  130,
                0,  131,    0,  133,  327,  328,  329,  330,  331,  332,
                334,
        };
    }
    static short yycheck[];
    static { yycheck(); }
    static void yycheck() {
        yycheck = new short[] {                         41,
                0,    0,   45,    0,   40,   26,   59,   41,   59,   59,
                10,   11,   40,   10,   11,    0,   41,   61,   40,   59,
                59,   59,   40,   35,   24,  123,  256,   24,   28,    0,
                129,   28,  199,   54,   34,   61,   36,   34,  256,   36,
                256,  256,   40,  123,   44,   40,   59,   44,  278,  256,
                256,   51,   64,   65,   51,  256,  262,   59,   44,   41,
                278,  276,  278,   61,   44,   41,  273,  274,   68,   41,
                59,   68,   59,   59,   41,   41,   43,  278,   45,   59,
                41,   42,   43,   40,   45,   85,   47,   41,   85,   43,
                276,   45,   41,   45,   94,   44,   41,   94,   59,   60,
                59,   62,    0,   61,   61,  256,   59,   40,  129,   59,
                261,  262,   10,   41,  257,   43,   59,   45,   45,   61,
                45,   41,  134,  135,   44,  256,  125,  125,   61,  256,
                229,   43,   45,   45,   45,  262,   45,   59,   59,  257,
                125,   59,  273,  274,  156,  157,  158,  159,  160,  161,
                150,   45,  152,  150,  125,  152,   59,  324,  256,   45,
                272,  130,  131,   45,   59,   45,  123,   45,  137,  138,
                68,   41,   59,   43,   61,   45,  256,  257,  256,  259,
                258,   40,   45,  263,  264,  256,  257,   85,  259,  267,
                45,  277,  263,  264,   45,  256,   45,  257,   45,  199,
                261,  262,  199,  256,  256,  256,  256,   61,  229,  261,
                262,   59,  256,  256,  257,  256,  256,  256,  256,  278,
                256,   45,  256,   45,  267,   45,  276,   45,  256,   45,
                256,  256,  257,  258,  277,  278,  278,  237,  256,  275,
                237,  275,  267,  256,  256,  257,  258,  259,  260,  261,
                272,  263,  264,  265,  266,  267,  268,  256,  257,  258,
                259,  256,  261,  262,  263,  264,  265,  256,  267,  256,
                41,  256,  257,  258,  259,  257,  261,  262,  263,  264,
                265,  257,  267,  257,  256,  256,  257,  258,  259,  256,
                261,  262,  263,  264,  265,  256,  267,  256,  256,  256,
                257,   59,  256,  256,  256,  257,  256,  268,  269,  270,
                271,  256,   40,  256,  256,  267,  316,  317,  318,  319,
                320,  321,  322,  323,  324,  277,  278,  324,  256,  256,
                257,  256,  257,  257,  256,  256,  336,  278,  256,  336,
                267,  278,  267,  256,  257,  256,  257,  256,  257,   59,
                277,  278,  277,  278,  267,   41,  267,   43,  267,   45,
                59,  256,  256,  257,  277,  278,  277,  278,  277,  278,
                256,  257,   59,  267,  256,  257,  256,  257,  256,  257,
                60,  267,   62,  277,  278,  267,  256,  267,   60,  267,
                62,  277,  278,  256,  257,  277,  278,  277,  278,  277,
                278,  256,  257,   59,  267,  256,  257,  256,  257,  256,
                257,   59,  267,   59,  277,  278,  267,   41,  267,   43,
                267,   45,  277,  278,   59,   59,  277,  278,  277,  278,
                277,  278,  256,  257,  256,  257,  256,  257,  256,  257,
                256,  257,   41,  267,   43,  267,   45,  267,   59,  267,
                59,  267,   59,  277,  278,  277,  278,  277,  278,  277,
                278,  277,  278,   41,   42,   43,   59,   45,   59,   47,
                41,   42,   43,   59,   45,   59,   47,   59,   60,   59,
                62,   59,   60,  257,   62,  256,  125,  258,   59,   60,
                41,   62,   43,  257,   45,   41,  267,   43,  256,   45,
                41,  123,   43,  278,   45,   40,   59,   42,   59,   60,
                123,   62,   47,   59,   60,   41,   62,   41,   59,   60,
                41,   62,   43,   42,   45,   41,   41,   43,   47,   45,
                41,   41,   43,   41,   45,  125,   44,   41,   59,   60,
                44,   62,   41,   59,   60,   44,   62,   41,   59,   60,
                41,   62,   43,   53,   45,   55,   43,  256,   45,  256,
                257,   41,  259,   43,   41,   45,  263,  264,   59,   60,
                256,   62,   41,   60,   41,   62,  256,  256,  257,   41,
                259,    0,   44,    0,  263,  264,  256,  257,  268,  269,
                270,  271,  256,  257,  132,  133,  268,  269,  270,  271,
                256,  139,  140,  123,  256,  257,  258,  259,  256,   59,
                256,  263,  264,  265,   10,  267,   14,  256,  257,  258,
                259,  256,  256,   16,  263,  264,  265,   17,  267,   28,
                123,   36,  256,   -1,  256,  257,   38,  259,  166,  167,
                168,  263,  264,  256,  257,  256,  259,  256,   -1,  256,
                263,  264,  123,  256,  257,   -1,  259,  256,  256,  257,
                263,  264,   -1,  256,  125,  256,  256,  257,  258,  259,
                256,   -1,  256,  263,  264,  265,  125,  267,  256,  256,
                257,  256,  257,  256,  257,  256,  268,  269,  270,  271,
                268,  269,  270,  271,  125,  256,  257,  268,  269,  270,
                271,   40,   41,   42,   43,  256,   45,   -1,   47,   40,
                256,   42,   43,   -1,   45,  256,   47,  268,  269,  270,
                271,   -1,  268,  269,  270,  271,   -1,  268,  269,  270,
                271,   -1,   -1,   -1,   -1,  256,  256,  257,   -1,  259,
                256,   -1,   -1,  263,  264,  256,   -1,  268,  269,  270,
                271,   -1,  268,  269,  270,  271,   -1,  268,  269,  270,
                271,   -1,   -1,  256,  257,  256,  259,   -1,   -1,  256,
                263,  264,  270,  271,  272,  273,  274,  268,  269,  270,
                271,  268,  269,  270,  271,  256,  257,   -1,  259,   -1,
                -1,   -1,  263,  264,   -1,  256,  257,   -1,  259,   -1,
                -1,   -1,  263,  264,   -1,   -1,   -1,  256,  257,   -1,
                259,   -1,   -1,   -1,  263,  264,   40,   41,   42,   43,
                -1,   45,   -1,   47,   -1,  256,  257,   -1,  259,   -1,
                -1,   -1,  263,  264,   40,   41,   42,   43,   -1,   45,
                -1,   47,   40,   41,   42,   43,   -1,   45,   -1,   47,
                40,   41,   42,   43,   -1,   45,   -1,   47,   40,   41,
                42,   43,   -1,   45,   -1,   47,   40,   41,   42,   43,
                -1,   45,   -1,   47,  317,  318,  319,  320,  321,  322,
                323,
        };
    }
    final static short YYFINAL=9;
    final static short YYMAXTOKEN=278;
    final static String yyname[] = {
            "end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
            "'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
            "'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            "'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null,null,null,null,null,"ID","ULONGINT","IF","THEN","ELSE","END_IF",
            "FOR","OUT","PROC","RETURN","DOUBLE","MENOR_IGUAL","MAYOR_IGUAL","IGUAL",
            "DISTINTO","PUNTO_PUNTO","UP","DOWN","CADENA","NA","NRO_DOUBLE","NRO_ULONGINT",
    };
    final static String yyrule[] = {
            "$accept : programa",
            "programa : lista_sentencias_declarativas lista_sentencias_ejecutables",
            "programa : lista_sentencias_ejecutables",
            "lista_sentencias_declarativas : sent_declarativa",
            "lista_sentencias_declarativas : lista_sentencias_declarativas sent_declarativa",
            "lista_sentencias_ejecutables : sent_ejecutable",
            "lista_sentencias_ejecutables : lista_sentencias_ejecutables sent_ejecutable",
            "sent_declarativa : tipo lista_variables ';'",
            "sent_declarativa : procedimiento ';'",
            "sent_declarativa : error lista_variables ';'",
            "sent_declarativa : tipo error ';'",
            "procedimiento : encabezado cuerpo_procedimiento",
            "procedimiento : encabezado error",
            "procedimiento : error cuerpo_procedimiento",
            "encabezado : encabezado_PROC parametro_PROC asignacion_NA",
            "encabezado : encabezado_PROC parametro_PROC error",
            "encabezado : encabezado_PROC error asignacion_NA",
            "encabezado : error parametro_PROC asignacion_NA",
            "encabezado_PROC : PROC ID",
            "encabezado_PROC : PROC error",
            "parametro_PROC : '(' parametro ')'",
            "parametro_PROC : '(' parametro ',' parametro ')'",
            "parametro_PROC : '(' parametro ',' parametro ',' parametro ')'",
            "parametro_PROC : '(' ')'",
            "asignacion_NA : NA '=' NRO_ULONGINT",
            "asignacion_NA : NA '=' error",
            "asignacion_NA : NA error NRO_ULONGINT",
            "cuerpo_procedimiento : '{' bloque_procedimiento '}'",
            "cuerpo_procedimiento : '{' bloque_procedimiento error",
            "cuerpo_procedimiento : error bloque_procedimiento '}'",
            "bloque_procedimiento : sent_ejecutable",
            "bloque_procedimiento : bloque_procedimiento sent_ejecutable",
            "bloque_procedimiento : sent_declarativa",
            "bloque_procedimiento : bloque_procedimiento sent_declarativa",
            "parametro : tipo ID",
            "parametro : error ID",
            "sent_ejecutable : sentencia_if ';'",
            "sent_ejecutable : sentencia_if error",
            "sent_ejecutable : sentencia_control",
            "sent_ejecutable : asignacion ';'",
            "sent_ejecutable : asignacion error",
            "sent_ejecutable : imprimir ';'",
            "sent_ejecutable : imprimir error",
            "sent_ejecutable : llamado_PROC ';'",
            "sent_ejecutable : llamado_PROC error",
            "lista_variables : ID",
            "lista_variables : lista_variables ',' ID",
            "tipo : ULONGINT",
            "tipo : DOUBLE",
            "llamado_PROC : ID '(' ID ')'",
            "llamado_PROC : error '(' ID ')'",
            "llamado_PROC : ID '(' ID ',' ID ')'",
            "llamado_PROC : error '(' ID ',' ID ')'",
            "llamado_PROC : ID '(' ID ',' ID ',' ID ')'",
            "llamado_PROC : error '(' ID ',' ID ',' ID ')'",
            "llamado_PROC : ID '(' ')'",
            "llamado_PROC : error '(' ')'",
            "sentencia_if : IF condicion_IF cuerpo",
            "sentencia_if : IF condicion_IF error",
            "sentencia_if : IF error cuerpo",
            "cuerpo : bloque_IF bloque_else END_IF",
            "cuerpo : bloque_IF END_IF",
            "cuerpo : bloque_IF bloque_else error",
            "cuerpo : bloque_IF error",
            "condicion_IF : '(' expresion '<' expresion ')'",
            "condicion_IF : '(' expresion '>' expresion ')'",
            "condicion_IF : '(' expresion IGUAL expresion ')'",
            "condicion_IF : '(' expresion MAYOR_IGUAL expresion ')'",
            "condicion_IF : '(' expresion MENOR_IGUAL expresion ')'",
            "condicion_IF : '(' expresion DISTINTO expresion ')'",
            "condicion_IF : '(' expresion '<' expresion error",
            "condicion_IF : '(' expresion '>' expresion error",
            "condicion_IF : '(' expresion IGUAL expresion error",
            "condicion_IF : '(' expresion MAYOR_IGUAL expresion error",
            "condicion_IF : '(' expresion MENOR_IGUAL expresion error",
            "condicion_IF : '(' expresion DISTINTO expresion error",
            "condicion_IF : '(' expresion '<' error ')'",
            "condicion_IF : '(' expresion '>' error ')'",
            "condicion_IF : '(' expresion IGUAL error ')'",
            "condicion_IF : '(' expresion MAYOR_IGUAL error ')'",
            "condicion_IF : '(' expresion MENOR_IGUAL error ')'",
            "condicion_IF : '(' expresion DISTINTO error ')'",
            "condicion_IF : '(' expresion error",
            "condicion_IF : '(' error",
            "bloque_IF : '{' cuerpo_ejecutable '}'",
            "bloque_IF : sent_ejecutable",
            "bloque_IF : '{' cuerpo_ejecutable error",
            "bloque_IF : '{' error",
            "bloque_else : ELSE '{' cuerpo_ejecutable '}'",
            "bloque_else : ELSE sent_ejecutable",
            "bloque_else : ELSE '{' error '}'",
            "bloque_else : ELSE '{' error",
            "bloque_FOR : '{' cuerpo_ejecutable '}' ';'",
            "bloque_FOR : sent_ejecutable",
            "bloque_FOR : '{' cuerpo_ejecutable '}' error",
            "bloque_FOR : '{' cuerpo_ejecutable error",
            "bloque_FOR : '{' error",
            "cuerpo_ejecutable : sent_ejecutable",
            "cuerpo_ejecutable : cuerpo_ejecutable sent_ejecutable",
            "asignacion : tipo_ID '=' expresion",
            "asignacion : tipo_ID '=' error",
            "asignacion : tipo_ID error expresion",
            "asignacion : error '=' expresion",
            "sentencia_control : FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' bloque_FOR",
            "sentencia_control : FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' error",
            "sentencia_control : FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr error bloque_FOR",
            "sentencia_control : FOR '(' asignacion_FOR ';' comparacion_FOR ';' error ')' bloque_FOR",
            "sentencia_control : FOR '(' asignacion_FOR ';' comparacion_FOR error incr_decr ')' bloque_FOR",
            "sentencia_control : FOR '(' asignacion_FOR ';' error ';' incr_decr ')' bloque_FOR",
            "sentencia_control : FOR '(' asignacion_FOR error comparacion_FOR ';' incr_decr ')' bloque_FOR",
            "sentencia_control : FOR '(' error ';' comparacion_FOR ';' incr_decr ')' bloque_FOR",
            "sentencia_control : FOR error asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' bloque_FOR",
            "asignacion_FOR : ID '=' NRO_ULONGINT",
            "asignacion_FOR : ID '=' error",
            "asignacion_FOR : ID error NRO_ULONGINT",
            "asignacion_FOR : error '=' NRO_ULONGINT",
            "comparacion_FOR : ID '<' expresion",
            "comparacion_FOR : ID '>' expresion",
            "comparacion_FOR : ID IGUAL expresion",
            "comparacion_FOR : ID MAYOR_IGUAL expresion",
            "comparacion_FOR : ID MENOR_IGUAL expresion",
            "comparacion_FOR : ID DISTINTO expresion",
            "comparacion_FOR : ID '<' error",
            "comparacion_FOR : ID '>' error",
            "comparacion_FOR : ID IGUAL error",
            "comparacion_FOR : ID MAYOR_IGUAL error",
            "comparacion_FOR : ID MENOR_IGUAL error",
            "comparacion_FOR : ID DISTINTO error",
            "comparacion_FOR : ID error",
            "comparacion_FOR : error '<' expresion",
            "comparacion_FOR : error '>' expresion",
            "comparacion_FOR : error IGUAL expresion",
            "comparacion_FOR : error MAYOR_IGUAL expresion",
            "comparacion_FOR : error MENOR_IGUAL expresion",
            "comparacion_FOR : error DISTINTO expresion",
            "incr_decr : UP NRO_ULONGINT",
            "incr_decr : DOWN NRO_ULONGINT",
            "incr_decr : DOWN error",
            "incr_decr : UP error",
            "incr_decr : error NRO_ULONGINT",
            "imprimir : OUT '(' CADENA ')'",
            "imprimir : OUT '(' CADENA error",
            "imprimir : OUT CADENA ')'",
            "imprimir : OUT '(' error",
            "imprimir : OUT '(' ')' error",
            "imprimir : OUT error",
            "conversion_explicita : DOUBLE '(' expresion ')'",
            "conversion_explicita : DOUBLE '(' expresion error",
            "conversion_explicita : DOUBLE '(' error ')'",
            "conversion_explicita : error '(' expresion ')'",
            "expresion : expresion '+' termino",
            "expresion : expresion '-' termino",
            "expresion : termino",
            "expresion : expresion '+' error",
            "expresion : error '+' termino",
            "expresion : expresion '-' error",
            "expresion : error '-' termino",
            "termino : termino '*' factor",
            "termino : termino '/' factor",
            "termino : factor",
            "termino : termino '*' error",
            "termino : termino '/' error",
            "termino : error '*' factor",
            "termino : error '/' factor",
            "factor : NRO_ULONGINT",
            "factor : NRO_DOUBLE",
            "factor : '-' NRO_DOUBLE",
            "factor : tipo_ID",
            "factor : conversion_explicita",
            "tipo_ID : ID PUNTO_PUNTO ID",
            "tipo_ID : ID PUNTO_PUNTO error",
            "tipo_ID : ID",
    };

//#line 1160 "G08 - Gramatica - 25102020.y"


    private LexerAnalyzer la;
    private SemanticAnalyzer sa;
    private List<String> errors;
    private List<String> rules;
    private SyntacticTree syntacticTree;
    private String globalScope = "";
    private String PROCscope = "";
    private List<SyntacticTree> PROCtrees = new ArrayList<>();
    private List<SyntacticTree> PROCtreesAux = new ArrayList<>();
    private int counter = 0;

    public Parser(LexerAnalyzer la){
        this.errors = new ArrayList<String>();
        this.rules = new ArrayList<String>();
        this.la = la;
        this.sa = new SemanticAnalyzer();
    }

    public int yylex(){
        Token token = this.la.getNextToken();
        List<Attribute> attributes = la.getAttribute(token.getLexema());
        yylval = new ParserVal(attributes);
        return token.getId();
    }

    public void yyerror(String error){

    }

    public int yyparser(){
        return yyparse();
    }

    private void addError(String msg) {
        errors.add(msg);
    }

    private void addRule(String msg) {
        rules.add(msg);
    }

    public List<String> getRules(){
        List<String> rules = new ArrayList<>(this.rules);
        return rules;
    }

    public List<String> getErrors(){
        List<String> errors = new ArrayList<>(this.errors);
        return errors;
    }

    public String printSyntacticTree(){
        if(this.syntacticTree != null){
            this.syntacticTree.printTree(this.syntacticTree, "Root: ");
            return this.syntacticTree.getPrintTree();
        }
        return "";
    }

    public SyntacticTree returnTree(){
        return this.syntacticTree;
    }

    public boolean checkType(SyntacticTree root){
        return root.checkType(root);
    }

    public void setScopeProcID(Attribute attribute){
        attribute.setUse(Use.nombre_procedimiento);
        attribute.setScope(globalScope);
    }

    public void setScopeProcParam (List<String> list){
        for(String lexeme : list){
            List<Attribute> attributes = la.getAttribute(lexeme);
            attributes.get(attributes.size()-1).setScope(globalScope);

        }
    }

    public void decreaseScope(){
        String [] array = this.globalScope.split("\\@");
        String aux = "";
        for(int i=0; i<array.length-1; i++){
            if(i == 0)
                aux = array[i];
            else
                aux = aux + "@" + array[i];
        }
        this.globalScope = aux;
    }

    public List<Attribute> getListUse(List<Attribute> list, Use use){
        List<Attribute> aux = new ArrayList<>();
        for(Attribute attribute : list){
            if(attribute.getUse() != null){
                if(attribute.getUse().equals(use)){
                    aux.add(attribute);
                }
            }
        }
        return aux;
    }

    public void isRedeclared(List<Attribute> attributes, String lexeme, String error){
        boolean found = false;
        if(!this.globalScope.isEmpty() || !(attributes.size() == 1)) {
            if (attributes.size() == 1) {
                attributes.get(attributes.size() - 1).setDeclared();
                found = true;
            }else{
                if (sa.isRedeclared(this.globalScope, lexeme, attributes)) {
                    attributes.get(attributes.size() - 1).decreaseAmount();
                    addError("Error Semantico en linea " + la.getNroLinea() + ":" + error);
                    la.getSt().deleteLastElement(lexeme);
                    found = true;
                }
            }
        }
        if(!found){
            attributes.get(attributes.size()-1).setDeclared();
        }
    }

    //Elimina de la tabla de símbolos los atributos de un lexema, que coinciden con el uso pasado por parámetro
    public void deleteSTEntry(String lexeme, Use use){
        List<Attribute> listAttributes = la.getSymbolTable().getSymbolTable().get(lexeme);
        for (int i=0; i<listAttributes.size(); i++){
            if(listAttributes.get(i).getUse().equals(use)){
                listAttributes.remove(i);
                i--;
            }
        }
        la.getSymbolTable().getSymbolTable().replace(lexeme, listAttributes);
    }

    //Chequea si existe una variable al alcance
    public Attribute checkID(List<Attribute> attributes, String lexeme){

        attributes.remove(attributes.size()-1);
        List<Attribute> variables = getListUse(attributes, Use.variable);
        List<Attribute> parametros = getListUse(attributes, Use.nombre_parametro);

        Attribute att = null;

        boolean encontro = false;

        if(variables.size() > 0){
            if(parametros.size() > 0){
                parametros.addAll(variables);
                attributes = parametros;
            }else{
                attributes = variables;
            }
        }else{
            if(parametros.size() > 0) {
                attributes = parametros;
            }else {
                attributes = null;
            }
        }

        if(attributes == null){
            encontro = false;
        }else{
            for(Attribute attribute : attributes) {
                encontro = sa.isReachable(this.globalScope, lexeme, attribute);
                if(encontro){
                    att = attribute;
                    break;
                }
            }
        }

        if(!encontro){
            addError("Error Semántico en línea "+ la.getNroLinea() +": No se encuentra variable al alcance");
        }

        return att;

    }

    //Chequea si existe un procedimiento al alcance para ser llamado
    public List<Parameter> checkIDPROC(List<Attribute> attributes, String lexeme){
        attributes = getListUse(attributes, Use.nombre_procedimiento);
        List<Parameter> parameters = new ArrayList<>();
        boolean encontro = false;
        if(attributes.isEmpty())
            encontro = false;
        else {
            for(Attribute attribute : attributes) {
                encontro = sa.isReachable(this.globalScope, lexeme, attribute);
                if(encontro){
                    this.PROCscope = attribute.getScope();
                    parameters = attribute.getParameters();
                    break;
                }

            }
        }
        if(!encontro){
            addError("Error Semántico en línea "+ la.getNroLinea() +": No se encuentra declaración de procedimiento al alcance");
        }

        return parameters;
    }


    //Chequear que exista una variable en el ámbito del procedimiento
    public Type checkIDdospuntosID(String scopePROC, String lexeme, List<Attribute> attributes){
        String[] scopeSplit = scopePROC.split("@");
        String scope = lexeme + "@";
        for(int i = 1; i < scopeSplit.length; i++)
            scope += scopeSplit[i] + "@";
        scope += scopeSplit[0];
        boolean encontro = false;
        for(int i = attributes.size()-1; i >= 0; i--){
            if (attributes.get(i).getScope().equals(scope))
                return attributes.get(i).getType();
        }

        if(!encontro)
            addError("Error Semántico en línea "+ la.getNroLinea() +": No se encuentra variable al alcance (ID::ID)");

        return Type.ERROR;

    }


    public void checkParameters(List<Parameter> formalParameters, List<Type> types){
        if(formalParameters.isEmpty() && !types.isEmpty() || !formalParameters.isEmpty() && types.isEmpty()){
            addError("Error Semantico en linea "+ la.getNroLinea() +": Llamado a procedimiento con numero erroneo de parametros");
        }else{
            if(types.size() != formalParameters.size())
                addError("Error Semantico en linea "+ la.getNroLinea() +": Llamado a procedimiento con distinto numero de parametros");
            else{
                if(!formalParameters.isEmpty() && !types.isEmpty()){
                    for(int i=0; i < formalParameters.size(); i++){
                        if(!formalParameters.get(i).getType().getName().equals(types.get(i).getName())){
                            addError("Error Semantico en linea "+ la.getNroLinea() +": Llamado a procedimiento con parametro cuyo tipo no coincide");
                            break;
                        }
                    }
                }
            }
        }
    }


    public List<SyntacticTree> getPROCtreeList(){
        List<SyntacticTree> list = new ArrayList<>(this.PROCtrees);
        return list;
    }

    public String printPROCtree(){
        String procTree = "";
        for(SyntacticTree node : this.PROCtrees){
            node.printTree(node, "Root: ");
            procTree += node.getPrintTree() + '\n';
        }
        return procTree;
    }
    //#line 942 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
    void yylexdebug(int state,int ch)
    {
        String s=null;
        if (ch < 0) ch=0;
        if (ch <= YYMAXTOKEN) //check index bounds
            s = yyname[ch];    //now get it
        if (s==null)
            s = "illegal-symbol";
        debug("state "+state+", reading "+ch+" ("+s+")");
    }





    //The following are now global, to aid in error reporting
    int yyn;       //next next thing to do
    int yym;       //
    int yystate;   //current parsing state from state table
    String yys;    //current token string


    //###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
    int yyparse()
    {
        boolean doaction;
        init_stacks();
        yynerrs = 0;
        yyerrflag = 0;
        yychar = -1;          //impossible char forces a read
        yystate=0;            //initial state
        state_push(yystate);  //save it
        val_push(yylval);     //save empty value
        while (true) //until parsing is done, either correctly, or w/error
        {
            doaction=true;
            if (yydebug) debug("loop");
            //#### NEXT ACTION (from reduction table)
            for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
            {
                if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
                if (yychar < 0)      //we want a char?
                {
                    yychar = yylex();  //get next token
                    if (yydebug) debug(" next yychar:"+yychar);
                    //#### ERROR CHECK ####
                    if (yychar < 0)    //it it didn't work/error
                    {
                        yychar = 0;      //change it to default string (no -1!)
                        if (yydebug)
                            yylexdebug(yystate,yychar);
                    }
                }//yychar<0
                yyn = yysindex[yystate];  //get amount to shift by (shift index)
                if ((yyn != 0) && (yyn += yychar) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
                {
                    if (yydebug)
                        debug("state "+yystate+", shifting to state "+yytable[yyn]);
                    //#### NEXT STATE ####
                    yystate = yytable[yyn];//we are in a new state
                    state_push(yystate);   //save it
                    val_push(yylval);      //push our lval as the input for next rule
                    yychar = -1;           //since we have 'eaten' a token, say we need another
                    if (yyerrflag > 0)     //have we recovered an error?
                        --yyerrflag;        //give ourselves credit
                    doaction=false;        //but don't process yet
                    break;   //quit the yyn=0 loop
                }

                yyn = yyrindex[yystate];  //reduce
                if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
                {   //we reduced!
                    if (yydebug) debug("reduce");
                    yyn = yytable[yyn];
                    doaction=true; //get ready to execute
                    break;         //drop down to actions
                }
                else //ERROR RECOVERY
                {
                    if (yyerrflag==0)
                    {
                        yyerror("syntax error");
                        yynerrs++;
                    }
                    if (yyerrflag < 3) //low error count?
                    {
                        yyerrflag = 3;
                        while (true)   //do until break
                        {
                            if (stateptr<0)   //check for under & overflow here
                            {
                                yyerror("stack underflow. aborting...");  //note lower case 's'
                                return 1;
                            }
                            yyn = yysindex[state_peek(0)];
                            if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
                            {
                                if (yydebug)
                                    debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
                                yystate = yytable[yyn];
                                state_push(yystate);
                                val_push(yylval);
                                doaction=false;
                                break;
                            }
                            else
                            {
                                if (yydebug)
                                    debug("error recovery discarding state "+state_peek(0)+" ");
                                if (stateptr<0)   //check for under & overflow here
                                {
                                    yyerror("Stack underflow. aborting...");  //capital 'S'
                                    return 1;
                                }
                                state_pop();
                                val_pop();
                            }
                        }
                    }
                    else            //discard this token
                    {
                        if (yychar == 0)
                            return 1; //yyabort
                        if (yydebug)
                        {
                            yys = null;
                            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
                            if (yys == null) yys = "illegal-symbol";
                            debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
                        }
                        yychar = -1;  //read another
                    }
                }//end error recovery
            }//yyn=0 loop
            if (!doaction)   //any reason not to proceed?
                continue;      //skip action
            yym = yylen[yyn];          //get count of terminals on rhs
            if (yydebug)
                debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
            if (yym>0)                 //if count of rhs not 'nil'
                yyval = val_peek(yym-1); //get current semantic value
            yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
            switch(yyn)
            {
//########## USER-SUPPLIED ACTIONS ##########
                case 1:
//#line 31 "G08 - Gramatica - 25102020.y"
                {
                    syntacticTree = val_peek(0).tree;
                }
                break;
                case 2:
//#line 35 "G08 - Gramatica - 25102020.y"
                {
                    syntacticTree = val_peek(0).tree;
                }
                break;
                case 5:
//#line 46 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 6:
//#line 50 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("LISTA SENTENCIAS");
                    yyval.tree = new SyntacticTreeSentence(val_peek(1).tree, val_peek(0).tree, attribute);
                }
                break;
                case 7:
//#line 58 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia declarativa - Variable/s.");
                    yyval = val_peek(1);
                    Type type = new Type(val_peek(2).type.getName());
                    for(String lexeme : yyval.attributesSetteable){
                        List<Attribute> attributes = la.getAttribute(lexeme);
                        attributes.get(attributes.size() - 1).setUse(Use.variable);
                        attributes.get(attributes.size() - 1).setType(type);

                        List<Attribute> variables = getListUse(attributes, Use.variable);
                        List<Attribute> parametros = getListUse(attributes, Use.nombre_parametro);

                        boolean encontro = false;

                        if(variables.size() > 0){
                            if(parametros.size() > 0){
                                parametros.addAll(variables);
                                attributes = parametros;
                            }else{
                                attributes = variables;
                            }
                        }else{
                            if(parametros.size() > 0) {
                                attributes = parametros;
                            }else {
                                attributes = null;
                            }
                        }

                        this.isRedeclared(attributes, lexeme, "Sentencia declarativa - Redefinicion de  VARIABLE/S");

                        attributes.get(attributes.size()-1).setScope(this.globalScope);
                    }
                    yyval = val_peek(1);
                }
                break;
                case 8:
//#line 95 "G08 - Gramatica - 25102020.y"
                {
                    this.decreaseScope();
                    this.counter--;
                    this.PROCtreesAux.get(this.counter).setLeft(val_peek(1).tree);
                    yyval.tree = null;

                    this.PROCtrees.add(this.PROCtreesAux.get(this.PROCtreesAux.size()-1));
                    this.PROCtreesAux.remove(this.PROCtreesAux.size()-1);

                }
                break;
                case 9:
//#line 106 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Falta definir el tipo de la/s VARIABLE/S."); }
                break;
                case 10:
//#line 108 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Falta definir la/s VARIABLE/S."); }
                break;
                case 11:
//#line 113 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Procedimiento");
                    this.sa.deleteNA();
                    Attribute attribute = new Attribute("INICIO PROCEDIMIENTO");
                    attribute.setUse(Use.cuerpo_procedimiento);
                    yyval.tree = new SyntacticTreePROCHEAD(val_peek(0).tree, attribute);
                }
                break;
                case 12:
//#line 121 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera CUERPO del PROCEDIMIENTO"); }
                break;
                case 13:
//#line 122 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ENCABEZADO del PROCEDIMIENTO"); }
                break;
                case 14:
//#line 126 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Encabezado procedimiento");
                }
                break;
                case 15:
//#line 130 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ASIGNACION NA"); }
                break;
                case 16:
//#line 131 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se esperan '(' parametro ')' "); }
                break;
                case 17:
//#line 132 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera PROC ID"); }
                break;
                case 18:
//#line 136 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": PROC ID");

                    yyval.attributes = val_peek(0).attributes;
                    String lexeme = val_peek(0).attributes.get(0).getLexeme();

                    List<Attribute> attributes = la.getAttribute(lexeme);
                    attributes.get(attributes.size() - 1).setUse(Use.nombre_procedimiento);
                    attributes.get(attributes.size() - 1).setScope(this.globalScope);

                    val_peek(0).attributes = getListUse(val_peek(0).attributes, Use.nombre_procedimiento);
                    this.isRedeclared(val_peek(0).attributes, lexeme, "Sentencia declarativa - Redefinicion de ID procedimiento");

                    this.globalScope += "@" + lexeme;

                    Attribute attribute = val_peek(0).attributes.get(val_peek(0).attributes.size()-1);
                    SyntacticTree root = new SyntacticTreePROCHEAD(null, attribute);
                    this.PROCtreesAux.add(root);
                    this.counter++;
                }
                break;
                case 19:
//#line 156 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ID"); }
                break;
                case 20:
//#line 160 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Procedimiento - un parametro");

                    this.setScopeProcParam(val_peek(1).attributesSetteable);

                    String[] scope = this.globalScope.split("@");
                    String lexeme = scope[scope.length - 1];
                    List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

                    List<Parameter> parameters = new ArrayList<>();

                    parameters.add(new Parameter(val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getScope(), val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getType()));

                    attributes.get(attributes.size()-1).setParameters(parameters);
                }
                break;
                case 21:
//#line 177 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Procedimiento - dos parametros");

                    this.setScopeProcParam(val_peek(3).attributesSetteable);
                    this.setScopeProcParam(val_peek(1).attributesSetteable);
                    String lexemeParam1 = val_peek(3).attributes.get(0).getLexeme();
                    String lexemeParam2 = val_peek(1).attributes.get(0).getLexeme();

                    if(lexemeParam1.equals(lexemeParam2)){
                        addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Redefinicion de parametro");
                    }

                    String[] scope = this.globalScope.split("@");
                    String lexeme = scope[scope.length - 1];
                    List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

                    List<Parameter> parameters = new ArrayList<>();

                    parameters.add(new Parameter(val_peek(3).attributes.get(val_peek(3).attributes.size()-1).getScope(), val_peek(3).attributes.get(val_peek(3).attributes.size()-1).getType()));
                    parameters.add(new Parameter(val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getScope(), val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getType()));

                    attributes.get(attributes.size()-1).setParameters(parameters);
                }
                break;
                case 22:
//#line 202 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Procedimiento - dos parametros");

                    this.setScopeProcParam(val_peek(5).attributesSetteable);
                    this.setScopeProcParam(val_peek(3).attributesSetteable);
                    this.setScopeProcParam(val_peek(1).attributesSetteable);
                    String lexemeParm1 = val_peek(5).attributes.get(0).getLexeme();
                    String lexemeParm2 = val_peek(3).attributes.get(0).getLexeme();
                    String lexemeParm3 = val_peek(1).attributes.get(0).getLexeme();

                    if(lexemeParm1.equals(lexemeParm2) || lexemeParm1.equals(lexemeParm3) || lexemeParm2.equals(lexemeParm3)){
                        addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Redefinicion de parametro");
                    }

                    String[] scope = this.globalScope.split("@");
                    String lexeme = scope[scope.length - 1];
                    List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

                    List<Parameter> parameters = new ArrayList<>();

                    parameters.add(new Parameter(val_peek(5).attributes.get(val_peek(5).attributes.size()-1).getScope(), val_peek(5).attributes.get(val_peek(5).attributes.size()-1).getType()));
                    parameters.add(new Parameter(val_peek(3).attributes.get(val_peek(3).attributes.size()-1).getScope(), val_peek(3).attributes.get(val_peek(3).attributes.size()-1).getType()));
                    parameters.add(new Parameter(val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getScope(), val_peek(1).attributes.get(val_peek(1).attributes.size()-1).getType()));

                    attributes.get(attributes.size()-1).setParameters(parameters);
                }
                break;
                case 24:
//#line 233 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Procedimiento - Asignacion NA");

                    int pos = sa.checkNA(this.globalScope);
                    if(pos != -1){
                        String ID_PROC = sa.errorNA(pos, this.globalScope);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Sentencia declarativa - Se supera numero de anidamiento en PROC " + ID_PROC);
                    }

                    sa.addNA(Integer.valueOf(val_peek(0).attributes.get(0).getScope()));

                }
                break;
                case 25:
//#line 246 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion NA procedimiento - Se espera NRO_ULONGINT"); }
                break;
                case 26:
//#line 247 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion NA procedimiento - Se espera ="); }
                break;
                case 27:
//#line 251 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(1).tree;
                }
                break;
                case 28:
//#line 255 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Cuerpo procedimiento - Se espera }"); }
                break;
                case 29:
//#line 256 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Cuerpo procedimiento - Se espera }"); }
                break;
                case 30:
//#line 260 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 31:
//#line 264 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("SENTENCIA EJECUTABLE");
                    attribute.setUse(Use.cuerpo_procedimiento);
                    yyval.tree = new SyntacticTreeBODY(val_peek(1).tree, val_peek(0).tree, attribute);
                }
                break;
                case 32:
//#line 270 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 33:
//#line 274 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("SENTENCIA DECLARATIVA");
                    attribute.setUse(Use.cuerpo_procedimiento);
                    yyval.tree = new SyntacticTreeBODY(val_peek(1).tree, val_peek(0).tree, attribute);
                }
                break;
                case 34:
//#line 283 "G08 - Gramatica - 25102020.y"
                {
                    yyval = val_peek(0);

                    yyval.attributesSetteable = new ArrayList<>();
                    String lexeme = val_peek(0).attributes.get(0).getLexeme();
                    yyval.attributesSetteable.add(lexeme);

                    Type type = new Type(val_peek(1).type.getName());
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setType(type);
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setUse(Use.nombre_parametro);
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                }
                break;
                case 35:
//#line 296 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Parametros - Se espera tipo parametro procedimiento"); }
                break;
                case 36:
//#line 301 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(1).tree;
                }
                break;
                case 37:
//#line 304 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable IF - Falta ;"); }
                break;
                case 38:
//#line 307 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 39:
//#line 312 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(1).tree;
                }
                break;
                case 40:
//#line 315 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable asignacion - Falta ;"); }
                break;
                case 41:
//#line 318 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(1).tree;
                }
                break;
                case 42:
//#line 321 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable OUT - Falta ;"); }
                break;
                case 43:
//#line 324 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(1).tree;
                }
                break;
                case 44:
//#line 328 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta ;"); }
                break;
                case 45:
//#line 335 "G08 - Gramatica - 25102020.y"
                {
                    yyval.attributesSetteable = new ArrayList<>();
                    String lexeme = val_peek(0).attributes.get(0).getLexeme();
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.attributesSetteable.add(lexeme);
                }
                break;
                case 46:
//#line 343 "G08 - Gramatica - 25102020.y"
                {
                    yyval = val_peek(2);
                    String lexeme = val_peek(0).attributes.get(0).getLexeme();
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.attributesSetteable.add(lexeme);
                }
                break;
                case 47:
//#line 352 "G08 - Gramatica - 25102020.y"
                {
                    yyval.type = Type.ULONGINT;
                    Attribute attribute = new Attribute("ULONGINT");
                    attribute.setType(Type.ULONGINT);
                    yyval.tree  = new SyntacticTreeCONV(null, null, attribute);

                }
                break;
                case 48:
//#line 360 "G08 - Gramatica - 25102020.y"
                {
                    yyval.type = Type.DOUBLE;
                    Attribute attribute = new Attribute("DOUBLE");
                    attribute.setType(Type.DOUBLE);
                    yyval.tree = new SyntacticTreeCONV(null, null, attribute);
                }
                break;
                case 49:
//#line 370 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con un parametro");
                    String lexeme = val_peek(3).attributes.get(0).getLexeme();
                    String lexemeParam = val_peek(1).attributes.get(0).getLexeme();

                    val_peek(3).attributes.get(val_peek(3).attributes.size()-1).setUse(Use.llamado_procedimiento);

                    List<Parameter> formalParameters = checkIDPROC(val_peek(3).attributes, lexeme);

                    String scope = val_peek(3).attributes.get(0).getLexeme() + this.globalScope;
                    val_peek(3).attributes.get(val_peek(3).attributes.size()-1).setScopePROC(scope);

                    List<Type> types = new ArrayList<>();

                    lexeme = val_peek(1).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(1).attributes, lexeme);
                    Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    this.checkParameters(formalParameters, types);

                    List<Parameter> parameters = new ArrayList<>();
                    parameters.add(ID1);
                    val_peek(3).attributes.get(val_peek(3).attributes.size()-1).setParameters(parameters);

                    Attribute ID = val_peek(3).attributes.get(val_peek(3).attributes.size()-1);
                    ID.setScopePROC(this.PROCscope);
                    yyval.tree = new SyntacticTreeCALL(val_peek(3).tree, ID, formalParameters);
                }
                break;
                case 50:
//#line 402 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }
                break;
                case 51:
//#line 405 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con parametros");
                    String lexeme = val_peek(5).attributes.get(0).getLexeme();

                    val_peek(5).attributes.get(val_peek(5).attributes.size()-1).setUse(Use.llamado_procedimiento);

                    List<Parameter> formalParameters = checkIDPROC(val_peek(5).attributes, lexeme);

                    String scope = val_peek(5).attributes.get(0).getLexeme() + this.globalScope;
                    val_peek(5).attributes.get(val_peek(5).attributes.size()-1).setScopePROC(scope);

                    List<Type> types = new ArrayList<>();

                    Attribute attribute = this.checkID(val_peek(3).attributes, val_peek(3).attributes.get(0).getLexeme());
                    Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    attribute = this.checkID(val_peek(1).attributes, val_peek(1).attributes.get(0).getLexeme());
                    Parameter ID2 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    this.checkParameters(formalParameters, types);

                    List<Parameter> parameters = new ArrayList<>();
                    parameters.add(ID1);
                    parameters.add(ID2);
                    val_peek(5).attributes.get(val_peek(5).attributes.size()-1).setParameters(parameters);
                    Attribute ID = val_peek(5).attributes.get(val_peek(5).attributes.size()-1);
                    ID.setScopePROC(this.PROCscope);
                    yyval.tree = new SyntacticTreeCALL(val_peek(5).tree, ID, formalParameters);
                }
                break;
                case 52:
//#line 439 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }
                break;
                case 53:
//#line 442 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con parametros");
                    String lexeme = val_peek(7).attributes.get(0).getLexeme();

                    val_peek(7).attributes.get(val_peek(7).attributes.size()-1).setUse(Use.llamado_procedimiento);
                    List<Parameter> formalParameters = checkIDPROC(val_peek(7).attributes, lexeme);

                    String scope = val_peek(7).attributes.get(0).getLexeme() + this.globalScope;
                    val_peek(7).attributes.get(val_peek(7).attributes.size()-1).setScopePROC(scope);

                    List<Type> types = new ArrayList<>();

                    Attribute attribute = this.checkID(val_peek(5).attributes, val_peek(5).attributes.get(0).getLexeme());
                    Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    attribute = this.checkID(val_peek(3).attributes, val_peek(3).attributes.get(0).getLexeme());
                    Parameter ID2 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    attribute = this.checkID(val_peek(1).attributes, val_peek(1).attributes.get(0).getLexeme());
                    Parameter ID3 = new Parameter(attribute.getScope(), attribute.getType());
                    if(attribute != null)
                        types.add(attribute.getType());

                    this.checkParameters(formalParameters, types);

                    List<Parameter> parameters = new ArrayList<>();
                    parameters.add(ID1);
                    parameters.add(ID2);
                    parameters.add(ID3);
                    val_peek(7).attributes.get(val_peek(7).attributes.size()-1).setParameters(parameters);
                    Attribute ID = val_peek(7).attributes.get(val_peek(7).attributes.size()-1);
                    ID.setScopePROC(this.PROCscope);

                    yyval.tree = new SyntacticTreeCALL(val_peek(7).tree, ID, formalParameters);

                }
                break;
                case 54:
//#line 482 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }
                break;
                case 55:
//#line 485 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento sin parametros");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    val_peek(2).attributes.get(val_peek(2).attributes.size()-1).setUse(Use.llamado_procedimiento);
                    List<Parameter> formalParameters = checkIDPROC(val_peek(2).attributes, lexeme);

                    String scope = val_peek(2).attributes.get(0).getLexeme() + this.globalScope;
                    val_peek(2).attributes.get(val_peek(2).attributes.size()-1).setScopePROC(scope);

                    List<Type> types = new ArrayList<>();

                    this.checkParameters(formalParameters, types);

                    Attribute ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);
                    ID.setScopePROC(this.PROCscope);
                    yyval.tree = new SyntacticTreeCALL(val_peek(2).tree, ID);
                }
                break;
                case 56:
//#line 503 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }
                break;
                case 57:
//#line 509 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF");
                    Attribute IF = new Attribute("IF");
                    yyval.tree = new SyntacticTreeIF(val_peek(1).tree, val_peek(0).tree, IF);
                }
                break;
                case 58:
//#line 515 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF - Se espera cuerpo"); }
                break;
                case 59:
//#line 516 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF - Se espera condicion"); }
                break;
                case 60:
//#line 520 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF ELSE - Cuerpo");
                    Attribute CUERPO_IF_ELSE = new Attribute("CUERPO_IF_ELSE");
                    yyval.tree = new SyntacticTreeIFBODY(val_peek(2).tree, val_peek(1).tree, CUERPO_IF_ELSE);
                }
                break;
                case 61:
//#line 527 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Cuerpo");
                    Attribute CUERPO_IF = new Attribute("CUERPO_IF");
                    yyval.tree = new SyntacticTreeIFBODY(val_peek(1).tree, CUERPO_IF);
                }
                break;
                case 62:
//#line 533 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE bloque - Se espera END_IF"); }
                break;
                case 63:
//#line 534 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera END_IF"); }
                break;
                case 64:
//#line 539 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion <");

                    Attribute MENOR = new Attribute("<");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, MENOR);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF < - Incompatibilidad de tipos");
                    }
                }
                break;
                case 65:
//#line 552 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +":Sentencia IF - Condicion >.");

                    Attribute MAYOR = new Attribute(">");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, MAYOR);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF > - Incompatibilidad de tipos");
                    }
                }
                break;
                case 66:
//#line 564 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion ==.");

                    Attribute IGUAL = new Attribute("==");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, IGUAL);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF == - Incompatibilidad de tipos");
                    }
                }
                break;
                case 67:
//#line 576 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion >=.");

                    Attribute MAYOR_IGUAL = new Attribute(">=");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, MAYOR_IGUAL);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF >= - Incompatibilidad de tipos");
                    }
                }
                break;
                case 68:
//#line 588 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion <=.");

                    Attribute MENOR_IGUAL = new Attribute("<=");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, MENOR_IGUAL);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF <= - Incompatibilidad de tipos");
                    }
                }
                break;
                case 69:
//#line 600 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion !=.");

                    Attribute DISTINTO = new Attribute("!=");

                    yyval.tree = new SyntacticTreeIFCMP(val_peek(3).tree, val_peek(1).tree, DISTINTO);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF != - Incompatibilidad de tipos");
                    }
                }
                break;
                case 70:
//#line 612 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 71:
//#line 613 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 72:
//#line 614 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 73:
//#line 615 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 74:
//#line 616 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 75:
//#line 617 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
                break;
                case 76:
//#line 619 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 77:
//#line 620 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 78:
//#line 621 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 79:
//#line 622 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 80:
//#line 623 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 81:
//#line 624 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
                break;
                case 82:
//#line 626 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera comparador"); }
                break;
                case 83:
//#line 628 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera condicion"); }
                break;
                case 84:
//#line 633 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Bloque de sentencias");
                    Attribute attribute = new Attribute("BLOQUE THEN");
                    yyval.tree = new SyntacticTreeIFTHEN(val_peek(1).tree, attribute);
                }
                break;
                case 85:
//#line 641 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("BLOQUE THEN");
                    yyval.tree = new SyntacticTreeIFTHEN(val_peek(0).tree, attribute);
                }
                break;
                case 86:
//#line 646 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera } finalizacion BLOQUE IF");}
                break;
                case 87:
//#line 647 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera cuerpo_ejecutable");}
                break;
                case 88:
//#line 651 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia IF ELSE - bloque de sentencias ELSE");
                    Attribute attribute = new Attribute("BLOQUE ELSE");
                    yyval.tree = new SyntacticTreeIFELSE(val_peek(1).tree, attribute);
                }
                break;
                case 89:
//#line 657 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("BLOQUE ELSE");
                    yyval.tree = new SyntacticTreeIFELSE(val_peek(0).tree, attribute);
                }
                break;
                case 90:
//#line 662 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE - Se espera bloque de sentencias");}
                break;
                case 91:
//#line 663 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE - Se espera }");}
                break;
                case 92:
//#line 667 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Bloque de sentencias");
                    yyval.tree = val_peek(2).tree;
                }
                break;
                case 93:
//#line 672 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 94:
//#line 676 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera ;");}
                break;
                case 95:
//#line 677 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera } ");}
                break;
                case 96:
//#line 678 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera cuerpo_ejecutable ");}
                break;
                case 97:
//#line 682 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 98:
//#line 686 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("SENTENCIA");
                    yyval.tree = new SyntacticTreeBODY(val_peek(1).tree, val_peek(0).tree, attribute);
                }
                break;
                case 99:
//#line 694 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Asignacion");
                    Attribute attribute = new Attribute("=");

                    yyval.tree = new SyntacticTreeASIG(val_peek(2).tree, val_peek(0).tree, attribute);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion - Incompatibilidad de tipos");
                    }

                }
                break;
                case 100:
//#line 706 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera expresion lado derecho");}
                break;
                case 101:
//#line 707 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera =");}
                break;
                case 102:
//#line 708 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera ID lado izquierdo");}
                break;
                case 103:
//#line 713 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR");
                    Attribute headFor = new Attribute("INICIO FOR");
                    Attribute FOR = new Attribute("FOR");
                    Attribute bodyFor = new Attribute("CUERPO FOR");
                    SyntacticTree node = new SyntacticTreeFORBODY(val_peek(0).tree, val_peek(2).tree, bodyFor);
                    yyval.tree = new SyntacticTreeFORHEAD(val_peek(6).tree, new SyntacticTreeFOR(val_peek(4).tree, node, FOR), headFor);
                }
                break;
                case 104:
//#line 722 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera bloque de sentencias");}
                break;
                case 105:
//#line 723 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ')'");}
                break;
                case 106:
//#line 724 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera incremento/decremento");}
                break;
                case 107:
//#line 725 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ';' entre la comparacion y el incremento/decremento");}
                break;
                case 108:
//#line 726 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera comparación");}
                break;
                case 109:
//#line 727 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ';' entre la asignacion y la comparacion");}
                break;
                case 110:
//#line 728 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera asignacion");}
                break;
                case 111:
//#line 729 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera '('");}
                break;
                case 112:
//#line 733 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": ASIGNACION_FOR");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute ID = this.checkID(val_peek(2).attributes, lexeme);

                    if(ID == null){
                        ID = new Attribute(lexeme);
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion FOR - No se encuentra variable lado izquierdo al alcance");
                    }

                    Attribute NRO_ULONGINT = val_peek(0).attributes.get(val_peek(0).attributes.size()-1);
                    Attribute ASIGNACION = new Attribute("=");

                    yyval.tree = new SyntacticTreeFORASIG(new SyntacticTreeLeaf(null, null, ID), new SyntacticTreeLeaf(null, null, NRO_ULONGINT), ASIGNACION);

                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion - Incompatibilidad de tipos");
                    }
                }
                break;
                case 113:
//#line 757 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera NRO_ULONGINT lado derecho"); }
                break;
                case 114:
//#line 758 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera ="); }
                break;
                case 115:
//#line 759 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera ID lado izquierdo"); }
                break;
                case 116:
//#line 763 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion <.");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR < - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);

                    ID.setFlag();
                    Attribute MENOR = new Attribute("<");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, MENOR);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR < - Incompatibilidad de tipos");
                    }
                }
                break;
                case 117:
//#line 788 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion >");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR > - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);

                    ID.setFlag();
                    Attribute MAYOR = new Attribute(">");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, MAYOR);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR > - Incompatibilidad de tipos");
                    }
                }
                break;
                case 118:
//#line 813 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion ==");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR == - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);


                    ID.setFlag();
                    Attribute MENOR_IGUAL = new Attribute("==");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, MENOR_IGUAL);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR == - Incompatibilidad de tipos");
                    }
                }
                break;
                case 119:
//#line 839 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion >=");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR >= - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);

                    ID.setFlag();
                    Attribute MENOR_IGUAL = new Attribute(">=");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, MENOR_IGUAL);

                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR >= - Incompatibilidad de tipos");
                    }
                }
                break;
                case 120:
//#line 864 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion <=");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR <= - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);

                    ID.setFlag();
                    Attribute MENOR_IGUAL = new Attribute("<=");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, MENOR_IGUAL);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR <= - Incompatibilidad de tipos");
                    }
                }
                break;
                case 121:
//#line 888 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion !=");
                    String lexeme = val_peek(2).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(2).attributes, lexeme);

                    Attribute ID = null;

                    if(attribute == null){
                        ID = new Attribute("Error");
                        ID.setType(Type.ERROR);
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR != - No se encuentra variable lado izquierdo al alcance");
                    }else
                        ID = val_peek(2).attributes.get(val_peek(2).attributes.size()-1);

                    ID.setFlag();
                    Attribute DISTINTO = new Attribute("!=");

                    yyval.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), val_peek(0).tree, DISTINTO);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR != - Incompatibilidad de tipos");
                    }
                }
                break;
                case 122:
//#line 912 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 123:
//#line 913 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 124:
//#line 914 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 125:
//#line 915 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 126:
//#line 916 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 127:
//#line 917 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
                break;
                case 128:
//#line 919 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera comparador"); }
                break;
                case 129:
//#line 921 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 130:
//#line 922 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 131:
//#line 923 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 132:
//#line 924 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 133:
//#line 925 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 134:
//#line 926 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
                break;
                case 135:
//#line 930 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("UP");
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.tree  = new SyntacticTreeFORUP(new SyntacticTreeLeaf(null, null, val_peek(0).attributes.get(0)), attribute);
                }
                break;
                case 136:
//#line 936 "G08 - Gramatica - 25102020.y"
                {
                    Attribute attribute = new Attribute("DOWN");
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.tree  = new SyntacticTreeFORDOWN(new SyntacticTreeLeaf(null, null, val_peek(0).attributes.get(0)), attribute);
                }
                break;
                case 137:
//#line 942 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR decremento - Se espera NRO_ULONGINT"); }
                break;
                case 138:
//#line 943 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR incremento - Se espera NRO_ULONGINT"); }
                break;
                case 139:
//#line 944 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR incre/decre - Se espera UP/DOWN"); }
                break;
                case 140:
//#line 950 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Sentencia OUT");
                    val_peek(1).attributes.get(0).setFlag();
                    Attribute cadena = val_peek(1).attributes.get(0);
                    Attribute OUT = new Attribute("IMPRIMIR");
                    yyval.tree  = new SyntacticTreeOUT(new SyntacticTreeLeaf(null, null, cadena), OUT);
                }
                break;
                case 141:
//#line 958 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera ')'."); }
                break;
                case 142:
//#line 959 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera '('."); }
                break;
                case 143:
//#line 960 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera una cadena de caracteres luego de '('."); }
                break;
                case 144:
//#line 961 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - falta cadena"); }
                break;
                case 145:
//#line 962 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera '('."); }
                break;
                case 146:
//#line 968 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Conversion explicita");

                    Attribute attribute = new Attribute("CONVERSION");
                    yyval.tree = new SyntacticTreeCONV(val_peek(1).tree, attribute);

                    if(!this.checkType(val_peek(1).tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Conversion explicita - Incompatibilidad de tipos");
                        yyval.tree.getAttribute().setType(Type.ERROR);
                    }else{
                        if(!val_peek(1).tree.getType().getName().equals("DOUBLE"))
                            yyval.tree.getAttribute().setType(Type.DOUBLE);
                        else{
                            addError("Error Semántico en linea "+ la.getNroLinea() +": Conversion explicita - Se quiere convertir a un mismo tipo");
                            yyval.tree.getAttribute().setType(Type.ERROR);
                        }
                    }
                }
                break;
                case 147:
//#line 987 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera ')'."); }
                break;
                case 148:
//#line 988 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera expresion.");}
                break;
                case 149:
//#line 989 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera tipo DOUBLE.");}
                break;
                case 150:
//#line 995 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Suma");
                    Attribute attribute = new Attribute("+");
                    yyval.tree = new SyntacticTreeADD(val_peek(2).tree, val_peek(0).tree, attribute);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Suma - Incompatibilidad de tipos");
                    }
                }
                break;
                case 151:
//#line 1004 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Resta");
                    Attribute attribute = new Attribute("-");
                    yyval.tree = new SyntacticTreeSUB(val_peek(2).tree, val_peek(0).tree, attribute);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Resta - Incompatibilidad de tipos");
                    }
                }
                break;
                case 152:
//#line 1013 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 153:
//#line 1017 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Suma - Se espera un termino luego del '+'."); }
                break;
                case 154:
//#line 1018 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Suma - Se espera una expresion antes del '+'."); }
                break;
                case 155:
//#line 1019 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Resta - Se espera un termino luego del '-'."); }
                break;
                case 156:
//#line 1020 "G08 - Gramatica - 25102020.y"
                { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Resta - Se espera una expresion antes del '-'."); }
                break;
                case 157:
//#line 1024 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Multiplicacion");
                    Attribute attribute = new Attribute("*");
                    yyval.tree = new SyntacticTreeMUL(val_peek(2).tree, val_peek(0).tree, attribute);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Multiplicacion - Incompatibilidad de tipos");
                    }
                }
                break;
                case 158:
//#line 1033 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": Division");
                    Attribute attribute = new Attribute("/");
                    yyval.tree = new SyntacticTreeDIV(val_peek(2).tree, val_peek(0).tree, attribute);
                    if(!this.checkType(yyval.tree)){
                        addError("Error Semántico en linea "+ la.getNroLinea() +": Division - Incompatibilidad de tipos");
                    }
                }
                break;
                case 159:
//#line 1043 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 160:
//#line 1047 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Multiplicacion - Se espera un factor luego de * ");}
                break;
                case 161:
//#line 1048 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Division - Se espera un factor luego de /");}
                break;
                case 162:
//#line 1049 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Multiplicacion - Se espera un termino antes de * ");}
                break;
                case 163:
//#line 1050 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Division - Se espera un termino antes de /");}
                break;
                case 164:
//#line 1054 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": NRO_ULONGINT.");
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.tree  = new SyntacticTreeLeaf(null, null, val_peek(0).attributes.get(val_peek(0).attributes.size()-1));
                }
                break;
                case 165:
//#line 1060 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": NRO_DOUBLE.");
                    val_peek(0).attributes.get(val_peek(0).attributes.size()-1).setFlag();
                    yyval.tree  = new SyntacticTreeLeaf(null, null, val_peek(0).attributes.get(val_peek(0).attributes.size()-1));
                }
                break;
                case 166:
//#line 1067 "G08 - Gramatica - 25102020.y"
                {
                    String lexeme = "-" + val_peek(0).attributes.get(0).getLexeme();
                    String scope = "D" + SemanticAction.counter;
                    SemanticAction.counter++;
                    Attribute attribute = new Attribute(lexeme, scope,"NRO_DOUBLE", Type.DOUBLE, Use.constante);
                    attribute.setFlag();
                    boolean check = la.checkNegativeDouble(lexeme);
                    if(check){
                        addError("Error Sintáctico en línea "+ la.getNroLinea() +": DOUBLE fuera de rango.");
                    }else{
                        addRule("Linea "+ la.getNroLinea() +": NRO_DOUBLE negativo.");
                        if(la.checkDouble(lexeme))
                            la.addSymbolTable(lexeme, attribute);
                        else{
                            String oldScope = la.getAttributeScope(lexeme);
                            attribute.setScopePROC(oldScope);
                            SemanticAction.counter--;
                        }

                        val_peek(0).attributes.get(0).decreaseAmount();
                        String positiveLexeme = val_peek(0).attributes.get(0).getLexeme();
                        int amount = la.getAttribute(positiveLexeme).get(0).getAmount();
                        if(amount == 0){
                            la.getSt().deleteSymbolTableEntry(positiveLexeme);
                        }
                    }

                    yyval.tree  = new SyntacticTreeLeaf(null, null, attribute);
                }
                break;
                case 167:
//#line 1098 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 168:
//#line 1103 "G08 - Gramatica - 25102020.y"
                {
                    yyval.tree = val_peek(0).tree;
                }
                break;
                case 169:
//#line 1109 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": ID PUNTO_PUNTO ID");

                    String lexemeIz = val_peek(2).attributes.get(0).getLexeme();

                    String lexemeDer = val_peek(0).attributes.get(0).getLexeme();

                    val_peek(2).attributes.get(val_peek(2).attributes.size()-1).setUse(Use.llamado_procedimiento_variable);

                    List<Parameter> parameters = this.checkIDPROC(val_peek(2).attributes, lexemeIz);

                    this.deleteSTEntry(lexemeIz, Use.llamado_procedimiento_variable);

                    Attribute attribute = this.checkID(val_peek(0).attributes, lexemeDer);

                    String scopePROC = val_peek(2).attributes.get(val_peek(2).attributes.size()-1).getScope();
                    List<Attribute> aux = getListUse(val_peek(0).attributes, Use.variable);
                    aux.addAll(getListUse(val_peek(0).attributes, Use.nombre_parametro));

                    Type type = this.checkIDdospuntosID(scopePROC, lexemeDer, aux);

                    if(attribute == null){
                        attribute = new Attribute(lexemeDer);
                        attribute.setType(Type.ERROR);
                    }

                    attribute.setFlag();
                    yyval.tree  = new SyntacticTreeLeaf(null, null, attribute);
                }
                break;
                case 170:
//#line 1139 "G08 - Gramatica - 25102020.y"
                {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Tipo ID - Se espera ID luego de ::");}
                break;
                case 171:
//#line 1142 "G08 - Gramatica - 25102020.y"
                {
                    addRule("Linea "+ la.getNroLinea() +": ID");

                    String lexeme = val_peek(0).attributes.get(0).getLexeme();

                    Attribute attribute = this.checkID(val_peek(0).attributes, lexeme);

                    if(attribute == null){
                        attribute = new Attribute(lexeme);
                        attribute.setType(Type.ERROR);
                    }

                    attribute.setFlag();
                    yyval.tree  = new SyntacticTreeLeaf(null, null, attribute);
                }
                break;
//#line 2505 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
            }//switch
            //#### Now let's reduce... ####
            if (yydebug) debug("reduce");
            state_drop(yym);             //we just reduced yylen states
            yystate = state_peek(0);     //get new state
            val_drop(yym);               //corresponding value drop
            yym = yylhs[yyn];            //select next TERMINAL(on lhs)
            if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
            {
                if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
                yystate = YYFINAL;         //explicitly say we're done
                state_push(YYFINAL);       //and save it
                val_push(yyval);           //also save the semantic value of parsing
                if (yychar < 0)            //we want another character?
                {
                    yychar = yylex();        //get next character
                    if (yychar<0) yychar=0;  //clean, if necessary
                    if (yydebug)
                        yylexdebug(yystate,yychar);
                }
                if (yychar == 0)          //Good exit (if lex returns 0 ;-)
                    break;                 //quit the loop--all DONE
            }//if yystate
            else                        //else not done yet
            {                         //get next state and push, for next yydefred[]
                yyn = yygindex[yym];      //find out where to go
                if ((yyn != 0) && (yyn += yystate) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
                    yystate = yytable[yyn]; //get new state
                else
                    yystate = yydgoto[yym]; //else go to new defred
                if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
                state_push(yystate);     //going again, so push state & val...
                val_push(yyval);         //for next action
            }
        }//main loop
        return 0;//yyaccept!!
    }
//## end of method parse() ######################################



//## run() --- for Thread #######################################
    /**
     * A default run method, used for operating this parser
     * object in the background.  It is intended for extending Thread
     * or implementing Runnable.  Turn off with -Jnorun .
     */
    public void run()
    {
        yyparse();
    }
//## end of method run() ########################################



//## Constructors ###############################################
    /**
     * Default constructor.  Turn off with -Jnoconstruct .

     */
    public Parser()
    {
        //nothing to do
    }


    /**
     * Create a parser, setting the debug to true or false.
     * @param debugMe true for debugging, false for no debug.
     */
    public Parser(boolean debugMe)
    {
        yydebug=debugMe;
    }
//###############################################################



}
//################### END OF CLASS ##############################
