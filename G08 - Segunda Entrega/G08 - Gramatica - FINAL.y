%{

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


%}

%token	ID ULONGINT IF THEN ELSE END_IF FOR OUT PROC RETURN DOUBLE MENOR_IGUAL MAYOR_IGUAL IGUAL DISTINTO PUNTO_PUNTO UP DOWN CADENA NA NRO_DOUBLE NRO_ULONGINT

%left '+' '-'
%left '*' '/'
%start programa

/* Comienzo del programa */

%%
programa				: lista_sentencias_declarativas lista_sentencias_ejecutables
						{
							syntacticTree = $2.tree;
						}
						|lista_sentencias_ejecutables
						{
							syntacticTree = $1.tree;
						}
						; 


lista_sentencias_declarativas		: sent_declarativa
									| lista_sentencias_declarativas sent_declarativa 									
									;

lista_sentencias_ejecutables		: sent_ejecutable 
									{
										$$.tree = $1.tree;
									}
									| lista_sentencias_ejecutables sent_ejecutable 
									{
										Attribute attribute = new Attribute("LISTA SENTENCIAS");
										$$.tree = new SyntacticTreeSentence($1.tree, $2.tree, attribute);
									}
									;


sent_declarativa		:tipo lista_variables ';'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia declarativa - Variable/s.");
							$$ = $2;
		                    Type type = new Type($1.type.getName());
		                    for(String lexeme : $$.attributesSetteable){
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
							$$ = $2;	
						}

						|procedimiento ';'
						{
							this.decreaseScope();
							this.counter--;
							this.PROCtreesAux.get(this.counter).setLeft($1.tree);
							$$.tree = null;
							
		                    this.PROCtrees.add(this.PROCtreesAux.get(this.PROCtreesAux.size()-1));
		                    this.PROCtreesAux.remove(this.PROCtreesAux.size()-1);

						}

						| error lista_variables ';' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Falta definir el tipo de la/s VARIABLE/S."); }
						
						| tipo error ';' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Falta definir la/s VARIABLE/S."); }
						;

/* PROCEDIMIENTO */
procedimiento 			:encabezado cuerpo_procedimiento
						{
							addRule("Linea "+ la.getNroLinea() +": Procedimiento");
							this.sa.deleteNA();
							Attribute attribute = new Attribute("INICIO PROCEDIMIENTO");
							attribute.setUse(Use.cuerpo_procedimiento);
							$$.tree = new SyntacticTreePROCHEAD($2.tree, attribute);
						}

						|encabezado error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera CUERPO del PROCEDIMIENTO"); }
						|error cuerpo_procedimiento {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ENCABEZADO del PROCEDIMIENTO"); }
						;

encabezado 				:encabezado_PROC parametro_PROC asignacion_NA
						{
							addRule("Linea "+ la.getNroLinea() +": Encabezado procedimiento");
						}

						|encabezado_PROC parametro_PROC error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ASIGNACION NA"); }
						|encabezado_PROC error asignacion_NA {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se esperan '(' parametro ')' "); }
						|error parametro_PROC asignacion_NA {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera PROC ID"); }
						;

encabezado_PROC			:PROC ID
						{
							addRule("Linea "+ la.getNroLinea() +": PROC ID");

							$$.attributes = $2.attributes;
							String lexeme = $2.attributes.get(0).getLexeme();

							List<Attribute> attributes = la.getAttribute(lexeme);
		                    attributes.get(attributes.size() - 1).setUse(Use.nombre_procedimiento);
		                    attributes.get(attributes.size() - 1).setScope(this.globalScope);

		                    $2.attributes = getListUse($2.attributes, Use.nombre_procedimiento);
							this.isRedeclared($2.attributes, lexeme, "Sentencia declarativa - Redefinicion de ID procedimiento");

		                    this.globalScope += "@" + lexeme;

		                    Attribute attribute = $2.attributes.get($2.attributes.size()-1);
		                   	SyntacticTree root = new SyntacticTreePROCHEAD(null, attribute);
		                   	this.PROCtreesAux.add(root);
		                   	this.counter++;
						}
						|PROC error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Procedimiento - Se espera ID"); }
						;

parametro_PROC			:'(' parametro ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Procedimiento - un parametro");

							this.setScopeProcParam($2.attributesSetteable);

							String[] scope = this.globalScope.split("@");
							String lexeme = scope[scope.length - 1];
							List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

							List<Parameter> parameters = new ArrayList<>(); 

							parameters.add(new Parameter($2.attributes.get($2.attributes.size()-1).getScope(), $2.attributes.get($2.attributes.size()-1).getType()));

							attributes.get(attributes.size()-1).setParameters(parameters);
						}

						|'(' parametro ',' parametro ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Procedimiento - dos parametros");

							this.setScopeProcParam($2.attributesSetteable);
							this.setScopeProcParam($4.attributesSetteable);
							String lexemeParam1 = $2.attributes.get(0).getLexeme();
							String lexemeParam2 = $4.attributes.get(0).getLexeme();

							if(lexemeParam1.equals(lexemeParam2)){
								addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Redefinicion de parametro"); 
							}

							String[] scope = this.globalScope.split("@");
							String lexeme = scope[scope.length - 1];
							List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

							List<Parameter> parameters = new ArrayList<>(); 

							parameters.add(new Parameter($2.attributes.get($2.attributes.size()-1).getScope(), $2.attributes.get($2.attributes.size()-1).getType()));
							parameters.add(new Parameter($4.attributes.get($4.attributes.size()-1).getScope(), $4.attributes.get($4.attributes.size()-1).getType()));
							
							attributes.get(attributes.size()-1).setParameters(parameters);
						}

						|'(' parametro ',' parametro ',' parametro ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Procedimiento - dos parametros");

							this.setScopeProcParam($2.attributesSetteable);
							this.setScopeProcParam($4.attributesSetteable);
							this.setScopeProcParam($6.attributesSetteable);
							String lexemeParm1 = $2.attributes.get(0).getLexeme();
							String lexemeParm2 = $4.attributes.get(0).getLexeme();
							String lexemeParm3 = $6.attributes.get(0).getLexeme();

							if(lexemeParm1.equals(lexemeParm2) || lexemeParm1.equals(lexemeParm3) || lexemeParm2.equals(lexemeParm3)){
								addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia declarativa - Redefinicion de parametro"); 
							}

							String[] scope = this.globalScope.split("@");
							String lexeme = scope[scope.length - 1];
							List<Attribute> attributes = getListUse(la.getSt().getSymbolTable().get(lexeme), Use.nombre_procedimiento);

							List<Parameter> parameters = new ArrayList<>(); 

							parameters.add(new Parameter($2.attributes.get($2.attributes.size()-1).getScope(), $2.attributes.get($2.attributes.size()-1).getType()));
							parameters.add(new Parameter($4.attributes.get($4.attributes.size()-1).getScope(), $4.attributes.get($4.attributes.size()-1).getType()));
							parameters.add(new Parameter($6.attributes.get($6.attributes.size()-1).getScope(), $6.attributes.get($6.attributes.size()-1).getType()));
							
							attributes.get(attributes.size()-1).setParameters(parameters);
						}

						| '(' ')'
						;

asignacion_NA  			:NA '=' NRO_ULONGINT 
						{
							addRule("Linea "+ la.getNroLinea() +": Procedimiento - Asignacion NA");
							
							int pos = sa.checkNA(this.globalScope);
							if(pos != -1){
								String ID_PROC = sa.errorNA(pos, this.globalScope);
								addError("Error Semántico en linea "+ la.getNroLinea() +": Sentencia declarativa - Se supera numero de anidamiento en PROC " + ID_PROC);
							}

							sa.addNA(Integer.valueOf($3.attributes.get(0).getScope()));

						}

						|NA '=' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion NA procedimiento - Se espera NRO_ULONGINT"); }
						|NA error NRO_ULONGINT {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion NA procedimiento - Se espera ="); }
						;

cuerpo_procedimiento	:'{' bloque_procedimiento '}'
						{
							$$.tree = $2.tree;
						}

						|'{' bloque_procedimiento error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Cuerpo procedimiento - Se espera }"); }
						|error bloque_procedimiento '}' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Cuerpo procedimiento - Se espera }"); }
						; 

bloque_procedimiento	:sent_ejecutable 
						{
							$$.tree = $1.tree;
						}
						|bloque_procedimiento sent_ejecutable
						{
							Attribute attribute = new Attribute("SENTENCIA EJECUTABLE");
							attribute.setUse(Use.cuerpo_procedimiento);
							$$.tree = new SyntacticTreeBODY($1.tree, $2.tree, attribute);
						}
						|sent_declarativa
						{
							$$.tree = $1.tree;
						}
						|bloque_procedimiento sent_declarativa
						{
							Attribute attribute = new Attribute("SENTENCIA DECLARATIVA");
							attribute.setUse(Use.cuerpo_procedimiento);
							$$.tree = new SyntacticTreeBODY($1.tree, $2.tree, attribute);
						}
						;

/* PARAMETROS */
parametro 				:tipo ID
						{
							$$ = $2;

							$$.attributesSetteable = new ArrayList<>(); 
							String lexeme = $2.attributes.get(0).getLexeme();
							$$.attributesSetteable.add(lexeme);

							Type type = new Type($1.type.getName());
							$2.attributes.get($2.attributes.size()-1).setType(type);
							$2.attributes.get($2.attributes.size()-1).setUse(Use.nombre_parametro);
							$2.attributes.get($2.attributes.size()-1).setFlag();
 						}

 						|error ID {addError("Error Sintactico en linea "+ la.getNroLinea() +": Parametros - Se espera tipo parametro procedimiento"); }
						;


sent_ejecutable			:sentencia_if ';'
						{
							$$.tree = $1.tree;
						}
						|sentencia_if error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable IF - Falta ;"); }
						
						|sentencia_control
						{
							$$.tree = $1.tree;
						}

						|asignacion ';'
						{
							$$.tree = $1.tree;
						}
						|asignacion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable asignacion - Falta ;"); }

						|imprimir ';'
						{
							$$.tree = $1.tree;
						}
						|imprimir error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable OUT - Falta ;"); }

						|llamado_PROC ';'
						{
							$$.tree = $1.tree;
						}

						|llamado_PROC error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta ;"); }
						;


/* Sentencias declarativas */

lista_variables 		: ID
						{ 
							$$.attributesSetteable = new ArrayList<>(); 
							String lexeme = $1.attributes.get(0).getLexeme();
							$1.attributes.get($1.attributes.size()-1).setFlag();
							$$.attributesSetteable.add(lexeme);
                        }

						| lista_variables ',' ID
						{ 
							$$ = $1; 
							String lexeme = $3.attributes.get(0).getLexeme();
							$3.attributes.get($3.attributes.size()-1).setFlag();
							$$.attributesSetteable.add(lexeme);
						}
						;

tipo					: ULONGINT 
						{
							$$.type = Type.ULONGINT;
							Attribute attribute = new Attribute("ULONGINT");
							attribute.setType(Type.ULONGINT);
							$$.tree  = new SyntacticTreeCONV(null, null, attribute);

						}					
						| DOUBLE
						{
							$$.type = Type.DOUBLE;
							Attribute attribute = new Attribute("DOUBLE");
							attribute.setType(Type.DOUBLE);
							$$.tree = new SyntacticTreeCONV(null, null, attribute);
						}
						;

/* Sentencias Ejecutables */
llamado_PROC 			: ID '(' ID ')' 
						{ 
							addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con un parametro");
							String lexeme = $1.attributes.get(0).getLexeme();
							String lexemeParam = $3.attributes.get(0).getLexeme();

							$1.attributes.get($1.attributes.size()-1).setUse(Use.llamado_procedimiento);

			                List<Parameter> formalParameters = checkIDPROC($1.attributes, lexeme);

			                String scope = $1.attributes.get(0).getLexeme() + this.globalScope;
			               	$1.attributes.get($1.attributes.size()-1).setScopePROC(scope);

			                List<Type> types = new ArrayList<>();

			                lexeme = $3.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($3.attributes, lexeme);
			                Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

                        	this.checkParameters(formalParameters, types);

                        	List<Parameter> parameters = new ArrayList<>();
                        	parameters.add(ID1);
			                $1.attributes.get($1.attributes.size()-1).setParameters(parameters);

                        	Attribute ID = $1.attributes.get($1.attributes.size()-1);
                        	ID.setScopePROC(this.PROCscope);
							$$.tree = new SyntacticTreeCALL($1.tree, ID, formalParameters);
						}

						|error '(' ID ')'  {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }

						|ID '(' ID ',' ID ')' 
						{ 
							addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con parametros");
							String lexeme = $1.attributes.get(0).getLexeme();

			                $1.attributes.get($1.attributes.size()-1).setUse(Use.llamado_procedimiento);

			                List<Parameter> formalParameters = checkIDPROC($1.attributes, lexeme);

			                String scope = $1.attributes.get(0).getLexeme() + this.globalScope;
			               	$1.attributes.get($1.attributes.size()-1).setScopePROC(scope);

			                List<Type> types = new ArrayList<>();

			                Attribute attribute = this.checkID($3.attributes, $3.attributes.get(0).getLexeme());
			                Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

                    		attribute = this.checkID($5.attributes, $5.attributes.get(0).getLexeme());
                    		Parameter ID2 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

                        	this.checkParameters(formalParameters, types);

                        	List<Parameter> parameters = new ArrayList<>();
			               	parameters.add(ID1);
			               	parameters.add(ID2);
			                $1.attributes.get($1.attributes.size()-1).setParameters(parameters);
							Attribute ID = $1.attributes.get($1.attributes.size()-1);
							ID.setScopePROC(this.PROCscope);
							$$.tree = new SyntacticTreeCALL($1.tree, ID, formalParameters);
						}

						|error '(' ID ',' ID ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }

						|ID '(' ID ',' ID ',' ID ')' 
						{ 
							addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento con parametros");
							String lexeme = $1.attributes.get(0).getLexeme();

			                $1.attributes.get($1.attributes.size()-1).setUse(Use.llamado_procedimiento);
			                List<Parameter> formalParameters = checkIDPROC($1.attributes, lexeme);

			                String scope = $1.attributes.get(0).getLexeme() + this.globalScope;
			               	$1.attributes.get($1.attributes.size()-1).setScopePROC(scope);

			                List<Type> types = new ArrayList<>();

			                Attribute attribute = this.checkID($3.attributes, $3.attributes.get(0).getLexeme());
			                Parameter ID1 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

                    		attribute = this.checkID($5.attributes, $5.attributes.get(0).getLexeme());
                    		Parameter ID2 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

                        	attribute = this.checkID($7.attributes, $7.attributes.get(0).getLexeme());
                        	Parameter ID3 = new Parameter(attribute.getScope(), attribute.getType());
                    		if(attribute != null)
                        		types.add(attribute.getType());

			                this.checkParameters(formalParameters, types);

			               	List<Parameter> parameters = new ArrayList<>();
			               	parameters.add(ID1);
			               	parameters.add(ID2);
			               	parameters.add(ID3);
			                $1.attributes.get($1.attributes.size()-1).setParameters(parameters);
							Attribute ID = $1.attributes.get($1.attributes.size()-1);
							ID.setScopePROC(this.PROCscope);

							$$.tree = new SyntacticTreeCALL($1.tree, ID, formalParameters);

						}
						|error '(' ID ',' ID ',' ID ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }

						| ID '(' ')' 
						{ 
							addRule("Linea "+ la.getNroLinea() +": Llamado a procedimiento sin parametros");
							String lexeme = $1.attributes.get(0).getLexeme();

			                $1.attributes.get($1.attributes.size()-1).setUse(Use.llamado_procedimiento);
			                List<Parameter> formalParameters = checkIDPROC($1.attributes, lexeme);

			                String scope = $1.attributes.get(0).getLexeme() + this.globalScope;
			               	$1.attributes.get($1.attributes.size()-1).setScopePROC(scope);

			                List<Type> types = new ArrayList<>();

			                this.checkParameters(formalParameters, types);

			                Attribute ID = $1.attributes.get($1.attributes.size()-1);
			                ID.setScopePROC(this.PROCscope);
							$$.tree = new SyntacticTreeCALL($1.tree, ID);	
						}
						|error '(' ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia ejecutable llamado a procedimiento - Falta nombre procedimiento"); }
						;

/* IF */

sentencia_if			:IF condicion_IF cuerpo 
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF");
							Attribute IF = new Attribute("IF");
							$$.tree = new SyntacticTreeIF($2.tree, $3.tree, IF);
						}

						|IF condicion_IF error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF - Se espera cuerpo"); }
						|IF error cuerpo {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF - Se espera condicion"); }		
						;

cuerpo 					:bloque_IF bloque_else END_IF
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF ELSE - Cuerpo");
							Attribute CUERPO_IF_ELSE = new Attribute("CUERPO_IF_ELSE");
							$$.tree = new SyntacticTreeIFBODY($1.tree, $2.tree, CUERPO_IF_ELSE);
						}

						|bloque_IF END_IF
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Cuerpo");
							Attribute CUERPO_IF = new Attribute("CUERPO_IF");
							$$.tree = new SyntacticTreeIFBODY($1.tree, CUERPO_IF);
						}

						|bloque_IF bloque_else error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE bloque - Se espera END_IF"); }
						|bloque_IF error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera END_IF"); }
						;


condicion_IF 			:'(' expresion '<' expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion <");

			                Attribute MENOR = new Attribute("<");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, MENOR);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF < - Incompatibilidad de tipos");
							}
						}

						|'(' expresion '>' expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +":Sentencia IF - Condicion >.");

			                Attribute MAYOR = new Attribute(">");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, MAYOR);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF > - Incompatibilidad de tipos");
							}
						}
						|'('expresion IGUAL expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion ==.");

			                Attribute IGUAL = new Attribute("==");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, IGUAL);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF == - Incompatibilidad de tipos");
							}
						}
						|'('expresion MAYOR_IGUAL expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion >=.");

			                Attribute MAYOR_IGUAL = new Attribute(">=");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, MAYOR_IGUAL);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF >= - Incompatibilidad de tipos");
							}
						}
						|'('expresion MENOR_IGUAL expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion <=.");

			                Attribute MENOR_IGUAL = new Attribute("<=");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, MENOR_IGUAL);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF <= - Incompatibilidad de tipos");
							}
						}
						|'('expresion DISTINTO expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Condicion !=.");
							
			                Attribute DISTINTO = new Attribute("!=");

			                $$.tree = new SyntacticTreeIFCMP($2.tree, $4.tree, DISTINTO);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion IF != - Incompatibilidad de tipos");
							}
						}

						|'(' expresion '<' expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
						|'(' expresion '>' expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
						|'(' expresion IGUAL expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
						|'(' expresion MAYOR_IGUAL expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
						|'(' expresion MENOR_IGUAL expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }
						|'(' expresion DISTINTO expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera )"); }

						|'(' expresion '<' error ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
						|'(' expresion '>' error ')'{addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
						|'(' expresion IGUAL error ')'{addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
						|'(' expresion MAYOR_IGUAL error ')'{addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
						|'(' expresion MENOR_IGUAL error ')'{addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }
						|'(' expresion DISTINTO error ')'{addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera expresion derecha"); }

						|'(' expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera comparador"); }

						|'(' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF condicion - Se espera condicion"); }
						;


bloque_IF 				:'{' cuerpo_ejecutable '}'
						{ 
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF - Bloque de sentencias");
							Attribute attribute = new Attribute("BLOQUE THEN");
							$$.tree = new SyntacticTreeIFTHEN($2.tree, attribute);
						}


						|sent_ejecutable 
						{
							Attribute attribute = new Attribute("BLOQUE THEN");
							$$.tree = new SyntacticTreeIFTHEN($1.tree, attribute);
						}

						|'{' cuerpo_ejecutable error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera } finalizacion BLOQUE IF");}
						|'{' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF bloque - Se espera cuerpo_ejecutable");}
						;

bloque_else				:ELSE '{' cuerpo_ejecutable '}'
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia IF ELSE - bloque de sentencias ELSE");
							Attribute attribute = new Attribute("BLOQUE ELSE");
							$$.tree = new SyntacticTreeIFELSE($3.tree, attribute);
						}
						|ELSE sent_ejecutable
						{
							Attribute attribute = new Attribute("BLOQUE ELSE");
							$$.tree = new SyntacticTreeIFELSE($2.tree, attribute);
						}

						|ELSE '{'error'}' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE - Se espera bloque de sentencias");}
						|ELSE '{'error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia IF ELSE - Se espera }");}
						;

bloque_FOR 				:'{' cuerpo_ejecutable '}' ';'
						{ 
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Bloque de sentencias");
							$$.tree = $2.tree;
						}
						|sent_ejecutable 
						{
							$$.tree = $1.tree;
						}

						|'{' cuerpo_ejecutable '}' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera ;");}
						|'{' cuerpo_ejecutable error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera } ");}
						|'{' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR bloque - Se espera cuerpo_ejecutable ");}
						;

cuerpo_ejecutable 		:sent_ejecutable 
						{
							$$.tree = $1.tree;
						}
						|cuerpo_ejecutable sent_ejecutable
						{
							Attribute attribute = new Attribute("SENTENCIA");
							$$.tree = new SyntacticTreeBODY($1.tree, $2.tree, attribute);
						}
						;


asignacion 				:tipo_ID '=' expresion 
						{
							addRule("Linea "+ la.getNroLinea() +": Asignacion");
							Attribute attribute = new Attribute("=");
							
							$$.tree = new SyntacticTreeASIG($1.tree, $3.tree, attribute);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion - Incompatibilidad de tipos");
							}

						}

						|tipo_ID '=' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera expresion lado derecho");}
						|tipo_ID error expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera =");}
						|error '=' expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Asignacion - Se espera ID lado izquierdo");}
						;

/* FOR */
sentencia_control		:FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' bloque_FOR
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR");
							Attribute headFor = new Attribute("INICIO FOR");
							Attribute FOR = new Attribute("FOR");
							Attribute bodyFor = new Attribute("CUERPO FOR");
							SyntacticTree node = new SyntacticTreeFORBODY($9.tree, $7.tree, bodyFor);
							$$.tree = new SyntacticTreeFORHEAD($3.tree, new SyntacticTreeFOR($5.tree, node, FOR), headFor);
						}

						|FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera bloque de sentencias");}
						|FOR '(' asignacion_FOR ';' comparacion_FOR ';' incr_decr error bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ')'");}
						|FOR '(' asignacion_FOR ';' comparacion_FOR ';' error ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera incremento/decremento");}
						|FOR '(' asignacion_FOR ';' comparacion_FOR error incr_decr ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ';' entre la comparacion y el incremento/decremento");}
						|FOR '(' asignacion_FOR ';' error ';' incr_decr ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera comparación");}
						|FOR '(' asignacion_FOR error comparacion_FOR ';' incr_decr ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera ';' entre la asignacion y la comparacion");}
						|FOR '(' error ';' comparacion_FOR ';' incr_decr ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera asignacion");}
						|FOR error asignacion_FOR ';' comparacion_FOR ';' incr_decr ')' bloque_FOR {addError("Error Sintactico en linea "+ la.getNroLinea() +": FOR - Se espera '('");}
						;

asignacion_FOR 			:ID '=' NRO_ULONGINT
						{
							addRule("Linea "+ la.getNroLinea() +": ASIGNACION_FOR");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute ID = this.checkID($1.attributes, lexeme);

			                if(ID == null){
			                	ID = new Attribute(lexeme);
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion FOR - No se encuentra variable lado izquierdo al alcance");
			                }

			                Attribute NRO_ULONGINT = $3.attributes.get($3.attributes.size()-1);
			                Attribute ASIGNACION = new Attribute("=");

							$$.tree = new SyntacticTreeFORASIG(new SyntacticTreeLeaf(null, null, ID), new SyntacticTreeLeaf(null, null, NRO_ULONGINT), ASIGNACION);

							$3.attributes.get($3.attributes.size()-1).setFlag();

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Asignacion - Incompatibilidad de tipos");
							}
						}

						|ID '=' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera NRO_ULONGINT lado derecho"); }
						|ID error NRO_ULONGINT {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera ="); }
						|error '=' NRO_ULONGINT {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR asignacion - Se espera ID lado izquierdo"); }
						;

comparacion_FOR			:ID '<' expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion <.");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;

			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR < - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);

			                ID.setFlag();
			                Attribute MENOR = new Attribute("<");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, MENOR);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR < - Incompatibilidad de tipos");
							}
						}
						|ID '>' expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion >");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;
			                
			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR > - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);

			                ID.setFlag();
			                Attribute MAYOR = new Attribute(">");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, MAYOR);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR > - Incompatibilidad de tipos");
							}
						}
						|ID IGUAL expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion ==");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;
			                
			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR == - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);
			                

			                ID.setFlag();
			                Attribute MENOR_IGUAL = new Attribute("==");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, MENOR_IGUAL);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR == - Incompatibilidad de tipos");
							}
						}
						|ID MAYOR_IGUAL expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion >=");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;
			                
			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR >= - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);

			                ID.setFlag();
			                Attribute MENOR_IGUAL = new Attribute(">=");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, MENOR_IGUAL);

							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR >= - Incompatibilidad de tipos");
							}
						}
						|ID MENOR_IGUAL expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion <=");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;
			                
			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR <= - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);

			                ID.setFlag();
			                Attribute MENOR_IGUAL = new Attribute("<=");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, MENOR_IGUAL);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR <= - Incompatibilidad de tipos");
							}
						}
						|ID DISTINTO expresion
						{
							addRule("Linea "+ la.getNroLinea() +": Sentencia FOR - Condicion !=");
							String lexeme = $1.attributes.get(0).getLexeme();

			                Attribute attribute = this.checkID($1.attributes, lexeme);

			                Attribute ID = null;
			                
			                if(attribute == null){
			                	ID = new Attribute("Error");
		                    	ID.setType(Type.ERROR);
		                    	addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR != - No se encuentra variable lado izquierdo al alcance");
			                }else
			                	ID = $1.attributes.get($1.attributes.size()-1);
		                    
			                ID.setFlag();
			                Attribute DISTINTO = new Attribute("!=");

							$$.tree = new SyntacticTreeFORCMP(new SyntacticTreeLeaf(null, null, ID), $3.tree, DISTINTO);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Condicion FOR != - Incompatibilidad de tipos");
							}
						}

						|ID '<' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
						|ID '>' error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
						|ID IGUAL error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
						|ID MAYOR_IGUAL error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
						|ID MENOR_IGUAL error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }
						|ID DISTINTO error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera expresion lado derecho comparacion"); }

						|ID error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera comparador"); }

						|error '<' expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						|error '>' expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						|error IGUAL expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						|error MAYOR_IGUAL expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						|error MENOR_IGUAL expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						|error DISTINTO expresion {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR condicion - Se espera ID lado izquierdo comparacion"); }
						;

incr_decr				: UP NRO_ULONGINT
						{
							Attribute attribute = new Attribute("UP");
							$2.attributes.get($2.attributes.size()-1).setFlag();
							$$.tree  = new SyntacticTreeFORUP(new SyntacticTreeLeaf(null, null, $2.attributes.get(0)), attribute);
						}
						| DOWN NRO_ULONGINT
						{
							Attribute attribute = new Attribute("DOWN");
							$2.attributes.get($2.attributes.size()-1).setFlag();
							$$.tree  = new SyntacticTreeFORDOWN(new SyntacticTreeLeaf(null, null, $2.attributes.get(0)), attribute);
						}

						|DOWN error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR decremento - Se espera NRO_ULONGINT"); }
						|UP error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR incremento - Se espera NRO_ULONGINT"); }
						|error NRO_ULONGINT {addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia FOR incre/decre - Se espera UP/DOWN"); }
						;

/* IMPRIMIR */

imprimir 				: OUT '(' CADENA ')' 
						{	
							addRule("Linea "+ la.getNroLinea() +": Sentencia OUT");
							$3.attributes.get(0).setFlag();
							Attribute cadena = $3.attributes.get(0);
							Attribute OUT = new Attribute("IMPRIMIR");
							$$.tree  = new SyntacticTreeOUT(new SyntacticTreeLeaf(null, null, cadena), OUT);
						}

						| OUT '(' CADENA error  { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera ')'."); }
						| OUT CADENA ')' { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera '('."); }
						| OUT '(' error { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera una cadena de caracteres luego de '('."); }
						| OUT '(' ')' error { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - falta cadena"); }
						| OUT error { addError("Error Sintactico en linea "+ la.getNroLinea() +": Sentencia OUT - Se espera '('."); }
						;

/* CONVERSION */

conversion_explicita	:DOUBLE '(' expresion ')'
						{
							addRule("Linea "+ la.getNroLinea() +": Conversion explicita");	

							Attribute attribute = new Attribute("CONVERSION");
							$$.tree = new SyntacticTreeCONV($3.tree, attribute);

							if(!this.checkType($3.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Conversion explicita - Incompatibilidad de tipos");
								$$.tree.getAttribute().setType(Type.ERROR);
							}else{
								if(!$3.tree.getType().getName().equals("DOUBLE"))
									$$.tree.getAttribute().setType(Type.DOUBLE);
								else{
									addError("Error Semántico en linea "+ la.getNroLinea() +": Conversion explicita - Se quiere convertir a un mismo tipo");
									$$.tree.getAttribute().setType(Type.ERROR);
								}
							}
						}

						|DOUBLE '(' expresion error {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera ')'."); }
						|DOUBLE '(' error ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera expresion.");}
						|error '(' expresion ')' {addError("Error Sintactico en linea "+ la.getNroLinea() +": Conversion explicita - Se espera tipo DOUBLE.");} 
						;

/* EXPRESION, TERMINO, FACTOR, CONSTANTE */

expresion 				: expresion '+' termino 
						{
							addRule("Linea "+ la.getNroLinea() +": Suma");
							Attribute attribute = new Attribute("+");
							$$.tree = new SyntacticTreeADD($1.tree, $3.tree, attribute);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Suma - Incompatibilidad de tipos");
							}
						}
						| expresion '-' termino 
						{
							addRule("Linea "+ la.getNroLinea() +": Resta");
							Attribute attribute = new Attribute("-");
							$$.tree = new SyntacticTreeSUB($1.tree, $3.tree, attribute);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Resta - Incompatibilidad de tipos");
							}
						}
						|termino 
						{
							$$.tree = $1.tree;
						}

						| expresion '+' error { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Suma - Se espera un termino luego del '+'."); }
						| error '+' termino { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Suma - Se espera una expresion antes del '+'."); }
				        | expresion '-' error  { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Resta - Se espera un termino luego del '-'."); }
				        | error '-' termino { addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Resta - Se espera una expresion antes del '-'."); }
						;

termino 				:termino '*' factor 
						{
							addRule("Linea "+ la.getNroLinea() +": Multiplicacion");
							Attribute attribute = new Attribute("*");
							$$.tree = new SyntacticTreeMUL($1.tree, $3.tree, attribute);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Multiplicacion - Incompatibilidad de tipos");
							}
						}
						|termino '/' factor 
						{
							addRule("Linea "+ la.getNroLinea() +": Division");
							Attribute attribute = new Attribute("/");
							$$.tree = new SyntacticTreeDIV($1.tree, $3.tree, attribute);
							if(!this.checkType($$.tree)){
								addError("Error Semántico en linea "+ la.getNroLinea() +": Division - Incompatibilidad de tipos");
							}
						}

						|factor 
						{
							$$.tree = $1.tree;
						}

						| termino '*' error {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Multiplicacion - Se espera un factor luego de * ");}
						| termino '/' error {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Division - Se espera un factor luego de /");}
						| error '*' factor {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Multiplicacion - Se espera un termino antes de * ");}
						| error '/' factor {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Division - Se espera un termino antes de /");}
						;

factor 					:NRO_ULONGINT 
						{
							addRule("Linea "+ la.getNroLinea() +": NRO_ULONGINT.");
							$1.attributes.get($1.attributes.size()-1).setFlag();
							$$.tree  = new SyntacticTreeLeaf(null, null, $1.attributes.get($1.attributes.size()-1));
						}
						|NRO_DOUBLE
						{
							addRule("Linea "+ la.getNroLinea() +": NRO_DOUBLE.");
							$1.attributes.get($1.attributes.size()-1).setFlag();
							$$.tree  = new SyntacticTreeLeaf(null, null, $1.attributes.get($1.attributes.size()-1));
						}

						|'-' NRO_DOUBLE 
						{
							String lexeme = "-" + $2.attributes.get(0).getLexeme();
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

								$2.attributes.get(0).decreaseAmount();
								String positiveLexeme = $2.attributes.get(0).getLexeme();
								int amount = la.getAttribute(positiveLexeme).get(0).getAmount();
								if(amount == 0){
									la.getSt().deleteSymbolTableEntry(positiveLexeme);
								}
							}

							$$.tree  = new SyntacticTreeLeaf(null, null, attribute);
						}

						| tipo_ID 
						{
							$$.tree = $1.tree;
						}

						|conversion_explicita
						{
							$$.tree = $1.tree;
						}
						;

tipo_ID					:ID PUNTO_PUNTO ID
						{
							addRule("Linea "+ la.getNroLinea() +": ID PUNTO_PUNTO ID");

							String lexemeIz = $1.attributes.get(0).getLexeme();

							String lexemeDer = $3.attributes.get(0).getLexeme();

			                $1.attributes.get($1.attributes.size()-1).setUse(Use.llamado_procedimiento_variable);
			                
			                List<Parameter> parameters = this.checkIDPROC($1.attributes, lexemeIz);

			                this.deleteSTEntry(lexemeIz, Use.llamado_procedimiento_variable);

			                Attribute attribute = this.checkID($3.attributes, lexemeDer);

			                String scopePROC = $1.attributes.get($1.attributes.size()-1).getScope();
			                List<Attribute> aux = getListUse($3.attributes, Use.variable);
			                aux.addAll(getListUse($3.attributes, Use.nombre_parametro));
			                
			                Type type = this.checkIDdospuntosID(scopePROC, lexemeDer, aux);

			                if(attribute == null){
		                    	attribute = new Attribute(lexemeDer);
		                    	attribute.setType(Type.ERROR);
		                    }

		                    attribute.setFlag();
			             	$$.tree  = new SyntacticTreeLeaf(null, null, attribute);
						}

						|ID PUNTO_PUNTO error {addError("Error Sintáctico en linea "+ la.getNroLinea() + ": Tipo ID - Se espera ID luego de ::");}

						|ID 
						{
							addRule("Linea "+ la.getNroLinea() +": ID");

							String lexeme = $1.attributes.get(0).getLexeme();

		                    Attribute attribute = this.checkID($1.attributes, lexeme);

		                    if(attribute == null){
		                    	attribute = new Attribute(lexeme);
		                    	attribute.setType(Type.ERROR);
		                    }

		                    attribute.setFlag();
			                $$.tree  = new SyntacticTreeLeaf(null, null, attribute);
						}
						;

 
%%

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
