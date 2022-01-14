package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

public class SyntacticTreeCONV extends SyntacticTree{
    public SyntacticTreeCONV(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeCONV(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeCONV(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";

        assembler += "FILD _" + this.getLeft().getAttribute().getScope()+'\n'; //DOUBLE(expresion)
        String auxVar = "@aux" + this.counterVar;
        assembler += "FSTP _" + auxVar +'\n';

        this.assemblerData += "_" + auxVar + " DQ ?" + '\n';

        Attribute attribute = new Attribute(auxVar, auxVar, Use.variable);
        this.deleteLeftChildren(this);
        this.replaceRoot(this, attribute);
        this.counterVar++;
        return assembler;
    }
}
