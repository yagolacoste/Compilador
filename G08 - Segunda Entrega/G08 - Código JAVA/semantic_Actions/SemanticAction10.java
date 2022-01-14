package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
//Reconoce condiciones
public class SemanticAction10 extends SemanticAction{

	public SemanticAction10() { }

	@Override
	public void execute(char character, LexerAnalyzer la) {
		la.setLexeme(la.getLexeme()+character);
		String lexeme = la.getLexeme();
		//int idnumber = la.getIdReservedWord(lexeme);
		int idnumber=0;
		if(lexeme.equals("<=")) {
			idnumber = la.getIdReservedWord("MENOR_IGUAL");
		}else {
			if (lexeme.equals(">=")) {
				idnumber = la.getIdReservedWord("MAYOR_IGUAL");
			} else {
				if (lexeme.equals("!=")) {
					idnumber = la.getIdReservedWord("DISTINTO");
				} else {
					if (lexeme.equals("==")) {
						idnumber = la.getIdReservedWord("IGUAL");
					} else {
						if (lexeme.equals("::")) {
							idnumber = la.getIdReservedWord("PUNTO_PUNTO");
						}
					}
				}
			}
		}

		la.setToken(idnumber, lexeme);
		la.setPos(la.getPos()+1);
		la.addRecognizedTokens( lexeme);
		State state = la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());

	}

}
