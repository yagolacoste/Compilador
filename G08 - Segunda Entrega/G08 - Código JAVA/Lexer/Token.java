package Lexer;

public class Token {
		 private int id;//devuelve posicion de esta palabra
		 private String lexeme;// devuelve palabra encontrada
		 
		 public Token(int id,String lexeme){
			this.id=id;
			this.lexeme=lexeme;
		 }

		 public Token() {
			 this.id=0;
			 this.lexeme=new String();
		 }
		 
		 public void setId(int id){
			 this.id=id;	 
		 }
		 
		 public void setLexema(String lexema){
			 this.lexeme=lexema;
		 }
		 
		 public String getLexema(){
			 return this.lexeme;
		 }
		 
		 public int getId(){
			 return this.id;
		 }
}
