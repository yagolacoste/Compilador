package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;

public class SyntacticTreeFORDOWN extends SyntacticTree{

    public SyntacticTreeFORDOWN(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeFORDOWN(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeFORDOWN(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String register = resgisterContainer.getRegister();
        assembler += "MOV " + register + ", _" + SyntacticTreeFOR.ID.getScope();
        assembler += "SUB " + register + ", _" + this.getLeft().getAttribute().getScope();
        assembler += "MOV _" + SyntacticTreeFOR.ID.getScope() + ", " + register;
        resgisterContainer.setAverableRegister(register);

        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }
}
