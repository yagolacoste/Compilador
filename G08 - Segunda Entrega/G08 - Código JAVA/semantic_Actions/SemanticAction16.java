package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
//Cualquier caracter que no sea un igual
public class SemanticAction16 extends SemanticAction{
    @Override
    public void execute(char character, LexerAnalyzer la) {

        String lexeme = la.getLexeme();
        int idNumber = la.getIdReservedWord(lexeme); 
        la.setToken(idNumber,"");
		la.addRecognizedTokens(lexeme);
        State state = la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());
    }
}
