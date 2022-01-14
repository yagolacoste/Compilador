package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Reconocemos saltos de lineas \n en el comentario
public class SemanticAction8 extends SemanticAction{
	
	public SemanticAction8() { }

	@Override
	public void execute(char character, LexerAnalyzer la) {
		la.setNroLinea(la.getNroLinea()+1);
		la.setPos(la.getPos()+1);
		State state = la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());

	}

}
