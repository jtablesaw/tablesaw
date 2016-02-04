package com.deathrayresearch.outlier.lang;// Generated from /Users/larrywhite/IdeaProjects/ColumnStorm/src/main/resources/Smalltalk.g42 by ANTLR 4.5
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SmalltalkLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SEPARATOR=1, STRING=2, COMMENT=3, BLOCK_START=4, BLOCK_END=5, CLOSE_PAREN=6, 
		OPEN_PAREN=7, PIPE=8, PERIOD=9, SEMI_COLON=10, BINARY_SELECTOR=11, LT=12, 
		GT=13, MINUS=14, RESERVED_WORD=15, IDENTIFIER=16, CARROT=17, COLON=18, 
		ASSIGNMENT=19, HASH=20, DOLLAR=21, EXP=22, HEX=23, LITARR_START=24, DYNDICT_START=25, 
		DYNARR_END=26, DYNARR_START=27, DIGIT=28, HEXDIGIT=29, KEYWORD=30, BLOCK_PARAM=31, 
		CHARACTER_CONSTANT=32;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"SEPARATOR", "STRING", "COMMENT", "BLOCK_START", "BLOCK_END", "CLOSE_PAREN", 
		"OPEN_PAREN", "PIPE", "PERIOD", "SEMI_COLON", "BINARY_SELECTOR", "LT", 
		"GT", "MINUS", "RESERVED_WORD", "IDENTIFIER", "CARROT", "COLON", "ASSIGNMENT", 
		"HASH", "DOLLAR", "EXP", "HEX", "LITARR_START", "DYNDICT_START", "DYNARR_END", 
		"DYNARR_START", "DIGIT", "HEXDIGIT", "KEYWORD", "BLOCK_PARAM", "CHARACTER_CONSTANT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, "'['", "']'", "')'", "'('", "'|'", "'.'", "';'", 
		null, "'<'", "'>'", "'-'", null, null, "'^'", "':'", "':='", "'#'", "'$'", 
		"'e'", "'16r'", "'#('", "'#{'", "'}'", "'{'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "SEPARATOR", "STRING", "COMMENT", "BLOCK_START", "BLOCK_END", "CLOSE_PAREN", 
		"OPEN_PAREN", "PIPE", "PERIOD", "SEMI_COLON", "BINARY_SELECTOR", "LT", 
		"GT", "MINUS", "RESERVED_WORD", "IDENTIFIER", "CARROT", "COLON", "ASSIGNMENT", 
		"HASH", "DOLLAR", "EXP", "HEX", "LITARR_START", "DYNDICT_START", "DYNARR_END", 
		"DYNARR_START", "DIGIT", "HEXDIGIT", "KEYWORD", "BLOCK_PARAM", "CHARACTER_CONSTANT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SmalltalkLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Smalltalk.g42"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\"\u00be\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\3\3\3\3\7\3H\n\3\f\3\16\3K\13\3\3\3\3\3\3\4\3\4\7\4Q\n\4\f"+
		"\4\16\4T\13\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n"+
		"\3\13\3\13\3\f\3\f\3\f\6\fi\n\f\r\f\16\fj\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u0088\n\20\3\21\6\21\u008b\n"+
		"\21\r\21\16\21\u008c\3\21\7\21\u0090\n\21\f\21\16\21\u0093\13\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\5!\u00bd\n!\4IR\2\"\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"\3\2\t\5\2\13\f\17\17\"\"\t\2\'\',.\61\61>@BB^^\u0080\u0080\5\2((/"+
		"/AA\4\2C\\c|\6\2\62;C\\aac|\3\2\62;\5\2\62;CHch\u00c9\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2"+
		"\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2"+
		"\2?\3\2\2\2\2A\3\2\2\2\3C\3\2\2\2\5E\3\2\2\2\7N\3\2\2\2\tW\3\2\2\2\13"+
		"Y\3\2\2\2\r[\3\2\2\2\17]\3\2\2\2\21_\3\2\2\2\23a\3\2\2\2\25c\3\2\2\2\27"+
		"h\3\2\2\2\31l\3\2\2\2\33n\3\2\2\2\35p\3\2\2\2\37\u0087\3\2\2\2!\u008a"+
		"\3\2\2\2#\u0094\3\2\2\2%\u0096\3\2\2\2\'\u0098\3\2\2\2)\u009b\3\2\2\2"+
		"+\u009d\3\2\2\2-\u009f\3\2\2\2/\u00a1\3\2\2\2\61\u00a5\3\2\2\2\63\u00a8"+
		"\3\2\2\2\65\u00ab\3\2\2\2\67\u00ad\3\2\2\29\u00af\3\2\2\2;\u00b1\3\2\2"+
		"\2=\u00b3\3\2\2\2?\u00b6\3\2\2\2A\u00b9\3\2\2\2CD\t\2\2\2D\4\3\2\2\2E"+
		"I\7)\2\2FH\13\2\2\2GF\3\2\2\2HK\3\2\2\2IJ\3\2\2\2IG\3\2\2\2JL\3\2\2\2"+
		"KI\3\2\2\2LM\7)\2\2M\6\3\2\2\2NR\7$\2\2OQ\13\2\2\2PO\3\2\2\2QT\3\2\2\2"+
		"RS\3\2\2\2RP\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7$\2\2V\b\3\2\2\2WX\7]\2\2"+
		"X\n\3\2\2\2YZ\7_\2\2Z\f\3\2\2\2[\\\7+\2\2\\\16\3\2\2\2]^\7*\2\2^\20\3"+
		"\2\2\2_`\7~\2\2`\22\3\2\2\2ab\7\60\2\2b\24\3\2\2\2cd\7=\2\2d\26\3\2\2"+
		"\2ei\t\3\2\2fi\5\21\t\2gi\t\4\2\2he\3\2\2\2hf\3\2\2\2hg\3\2\2\2ij\3\2"+
		"\2\2jh\3\2\2\2jk\3\2\2\2k\30\3\2\2\2lm\7>\2\2m\32\3\2\2\2no\7@\2\2o\34"+
		"\3\2\2\2pq\7/\2\2q\36\3\2\2\2rs\7p\2\2st\7k\2\2t\u0088\7n\2\2uv\7v\2\2"+
		"vw\7t\2\2wx\7w\2\2x\u0088\7g\2\2yz\7h\2\2z{\7c\2\2{|\7n\2\2|}\7u\2\2}"+
		"\u0088\7g\2\2~\177\7u\2\2\177\u0080\7g\2\2\u0080\u0081\7n\2\2\u0081\u0088"+
		"\7h\2\2\u0082\u0083\7u\2\2\u0083\u0084\7w\2\2\u0084\u0085\7r\2\2\u0085"+
		"\u0086\7g\2\2\u0086\u0088\7t\2\2\u0087r\3\2\2\2\u0087u\3\2\2\2\u0087y"+
		"\3\2\2\2\u0087~\3\2\2\2\u0087\u0082\3\2\2\2\u0088 \3\2\2\2\u0089\u008b"+
		"\t\5\2\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u0091\3\2\2\2\u008e\u0090\t\6\2\2\u008f\u008e\3\2"+
		"\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\"\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7`\2\2\u0095$\3\2\2\2\u0096"+
		"\u0097\7<\2\2\u0097&\3\2\2\2\u0098\u0099\7<\2\2\u0099\u009a\7?\2\2\u009a"+
		"(\3\2\2\2\u009b\u009c\7%\2\2\u009c*\3\2\2\2\u009d\u009e\7&\2\2\u009e,"+
		"\3\2\2\2\u009f\u00a0\7g\2\2\u00a0.\3\2\2\2\u00a1\u00a2\7\63\2\2\u00a2"+
		"\u00a3\78\2\2\u00a3\u00a4\7t\2\2\u00a4\60\3\2\2\2\u00a5\u00a6\7%\2\2\u00a6"+
		"\u00a7\7*\2\2\u00a7\62\3\2\2\2\u00a8\u00a9\7%\2\2\u00a9\u00aa\7}\2\2\u00aa"+
		"\64\3\2\2\2\u00ab\u00ac\7\177\2\2\u00ac\66\3\2\2\2\u00ad\u00ae\7}\2\2"+
		"\u00ae8\3\2\2\2\u00af\u00b0\t\7\2\2\u00b0:\3\2\2\2\u00b1\u00b2\t\b\2\2"+
		"\u00b2<\3\2\2\2\u00b3\u00b4\5!\21\2\u00b4\u00b5\5%\23\2\u00b5>\3\2\2\2"+
		"\u00b6\u00b7\5%\23\2\u00b7\u00b8\5!\21\2\u00b8@\3\2\2\2\u00b9\u00bc\5"+
		"+\26\2\u00ba\u00bd\5;\36\2\u00bb\u00bd\5+\26\2\u00bc\u00ba\3\2\2\2\u00bc"+
		"\u00bb\3\2\2\2\u00bdB\3\2\2\2\13\2IRhj\u0087\u008c\u0091\u00bc\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}