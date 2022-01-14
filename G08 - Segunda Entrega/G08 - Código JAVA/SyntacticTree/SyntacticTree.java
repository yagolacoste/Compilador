package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;

public abstract class SyntacticTree {
    private SyntacticTree left = null;
    private SyntacticTree rigth = null;
    private Attribute attribute = null;
    private int cant = 0;
    private String printTree = "";
    public static String assemblerData = "";
    public static int counterVar = 0;

    public SyntacticTree(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        this.left = left;
        this.rigth = rigth;
        this.attribute = attribute;
    }

    public SyntacticTree(SyntacticTree left, SyntacticTree rigth) {
        this.left = left;
        this.rigth = rigth;
    }

    public SyntacticTree(SyntacticTree left, Attribute attribute) {
        this.left = left;
        this.attribute = attribute;
    }

    public SyntacticTree getLeft() {
        return left;
    }

    public void setLeft(SyntacticTree left) {
        this.left = left;
    }

    public SyntacticTree getRight() {
        return rigth;
    }

    public void setRigth(SyntacticTree rigth) {
        this.rigth = rigth;
    }

    public String getLexeme() {
        return attribute.getLexeme();
    }

    public void setLexeme(String lexeme) {
        this.attribute.setLexeme(lexeme);
    }

    public Attribute getAttribute(){
        return this.attribute;
    }

    public Attribute setAttribute(Attribute attribute){
        this.attribute = attribute;
        return this.attribute;
    }

    public String getPrintTree(){
        return this.printTree;
    }

    public void printTree(SyntacticTree node, String hijo) {
        if (node != null) {
            if (node.isLeaf()) {
                this.printTree += tab(cant, hijo+node.attribute.getLexeme(), node.attribute.getType(), node.attribute.getUse());
                return;
            }
            this.printTree += tab(cant, hijo+node.getLexeme(), node.getType(), node.attribute.getUse()); // mostrar datos del nodo
            cant++;
            printTree(node.getLeft(), "Hijo izquierdo: "); //recorre subarbol izquierdo
            printTree(node.getRight(), "Hijo derecho: "); //recorre subarbol derecho
            cant--;
        }
    }

    public void setType(Type type) {
        this.attribute.setType(type);
    }

    public Type getType() {
        return this.attribute.getType();
    }

    public boolean isLeaf(){
        return (this.getLeft() == null && this.getRight() == null);
    }

    public String getAssemblerData(){
        return this.assemblerData;
    }

    private String tab(int cant, String lexeme, Type type, Use use){
        for(int i=cant; i>0; i--){
            lexeme = '\t' + lexeme;
        }
        return lexeme + '\n';
    }

    public boolean checkType(SyntacticTree root){
        if (root != null && !root.isLeaf()) {
            if(root.left == null){
                root.attribute.setType(Type.ERROR);
                return false;
            }
            if(root.rigth == null){
                root.attribute.setType(Type.ERROR);
                return false;
            }
            if(root.left.attribute.getType().getName().equals(root.rigth.attribute.getType().getName())){
                root.attribute.setType(root.left.attribute.getType());
                return true; //Tipos compatibles
            }else{
                root.attribute.setType(Type.ERROR);
                return false; //Tipos incompatibles
            }
        }
        if(root == null) {
            root.attribute.setType(Type.ERROR);
            return false;
        }else
            return true; //Tipos compatibles
    }

    public void deleteChildren(SyntacticTree root){
        root.setLeft(null);
        root.setRigth(null);
    }

    public void deleteLeftChildren(SyntacticTree root){
        root.setLeft(null);
    }

    public void replaceRoot(SyntacticTree root, Attribute attribute){
        root.setAttribute(attribute);
    }

    public boolean checkChildrenUse() {
        if(this.getLeft().getAttribute().getFlag() == 1 && this.getRight().getAttribute().getFlag() == 1)
                return true;
        return false;
    }

    public abstract String generateAssemblerCodeRegister(RegisterContainer resgisterContainer);
    public abstract String generateAssemblerCodeVariable(RegisterContainer resgisterContainer);

    public String assemblerTechnique(RegisterContainer resgisterContainer){
        String assembler = "";
        //Casos especiales de nodos intermedios que no tienen tipo y el assembler se genera siempre igual
        //invocamos generateAssemblerCodeRegister y dejamos sin código generateAssemblerCodeVariable
        if(this.getAttribute().getType() == null){
            assembler += generateAssemblerCodeRegister(resgisterContainer);
            return assembler;
        }

        if(this.getAttribute().getType().equals(Type.DOUBLE))
            assembler += generateAssemblerCodeVariable(resgisterContainer);
        else
            assembler += generateAssemblerCodeRegister(resgisterContainer);
        return assembler;
    }

    public String getAssemblerConditionDOUBLE(){
        String assembler = "";
        switch(this.getLexeme())
        {
            case "<" : assembler = "JGE ";
                break;
            case ">" : assembler = "JLE ";
                break;
            case "==" : assembler = "JNE ";
                break;
            case ">=" : assembler = "JL ";
                break;
            case "<=" : assembler = "JG ";
                break;
            case "!=" : assembler = "JE ";
                break;
            default : assembler = "";
        }
        return assembler;
    }

    public String getAssemblerConditionULONGINT(){
        String assembler = "";
        switch(this.getLexeme())
        {
            case "<" : assembler = "JGE ";
                break;
            case ">" : assembler = "JBE ";
                break;
            case "==" : assembler = "JNE ";
                break;
            case ">=" : assembler = "JB ";
                break;
            case "<=" : assembler = "JG ";
                break;
            case "!=" : assembler = "JE ";
                break;
            default : assembler = "";
        }
        return assembler;
    }
}
