package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Descartar el token porque terminamos de leer un comentario
public class SemanticAction9 extends SemanticAction{
	
	public SemanticAction9() { }

	@Override
	public void execute(char character, LexerAnalyzer la) {

		String comment = "Linea: " + la.getNroLinea() + " Comentario: " + la.getLexeme();
		la.addComments(comment);
		la.setLexeme("");
		la.setPos(la.getPos() + 1);
		State state = la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());

	}

}
