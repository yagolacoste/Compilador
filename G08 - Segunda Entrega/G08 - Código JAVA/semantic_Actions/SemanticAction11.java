package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;

//Se reconoce la cadena de string
public class SemanticAction11 extends SemanticAction{

    @Override
    public void execute(char character, LexerAnalyzer la) {

        la.setLexeme(la.getLexeme() + character);
        la.setPos(la.getPos() + 1);
        String lexeme = la.getLexeme();
        lexeme = lexeme.substring(1, lexeme.length()-1);
        String scope = "S" + SemanticAction.counterString;
        SemanticAction.counterString++;
        Attribute attribute = new Attribute(lexeme,scope,"CADENA", Type.STRING, Use.cadena);
        la.addSymbolTable(lexeme, attribute);
        int idNumber = la.getNumberId(lexeme);
        la.setToken(idNumber, lexeme);
		la.addRecognizedTokens("Cadena de String : " + lexeme);
        State state = la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());

    }
}
