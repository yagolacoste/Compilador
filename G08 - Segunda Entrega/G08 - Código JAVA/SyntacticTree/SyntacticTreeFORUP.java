package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

public class SyntacticTreeFORUP extends SyntacticTree{

    public SyntacticTreeFORUP(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeFORUP(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeFORUP(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String register = resgisterContainer.getRegister();
        assembler += "MOV " + register + ", _" + SyntacticTreeFOR.ID.getScope() + '\n';
        assembler += "ADD " + register + ", _" + this.getLeft().getAttribute().getScope() + '\n';
        assembler += "MOV _" + SyntacticTreeFOR.ID.getScope() + ", " + register + '\n';
        resgisterContainer.setAverableRegister(register);

        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }
}
