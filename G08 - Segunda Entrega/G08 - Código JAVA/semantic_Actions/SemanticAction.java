package semantic_Actions;

import Lexer.LexerAnalyzer;

public abstract class SemanticAction {
	public static int counter = 0;
	public static int counterString = 0;

	public abstract void execute( char character, LexerAnalyzer la) ;
}
