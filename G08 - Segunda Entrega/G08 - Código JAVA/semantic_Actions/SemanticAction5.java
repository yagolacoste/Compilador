package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Agrega letra al lexema que venimos armando
public class SemanticAction5 extends SemanticAction{

	public SemanticAction5() {}

	@Override
	public void execute(char character, LexerAnalyzer la) {
		la.setLexeme(la.getLexeme() + character);
		la.setPos(la.getPos() + 1);
		State state=la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());
	}
	
}
