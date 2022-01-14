package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

public class SyntacticTreeFORCMP extends SyntacticTree{

    private static int counter = 0;

    public SyntacticTreeFORCMP(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeFORCMP(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeFORCMP(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String register = "";
        Attribute attribute = null;

        register = resgisterContainer.getRegister();
        assembler += "MOV " + register + ", _" + this.getLeft().getAttribute().getScope() + '\n';

        if(this.getRight().getAttribute().getUse().equals(Use.registro)) {
            assembler += "CMP " + register + ", " + this.getRight().getAttribute().getLexeme() + '\n';
            resgisterContainer.setAverableRegister(this.getRight().getAttribute().getLexeme());
        }else
            assembler += "CMP " + register + ", _" + this.getRight().getAttribute().getScope() + '\n';

        resgisterContainer.setAverableRegister(register);

        String label = "FOR_CMP" + ++counter;
        assembler += getAssemblerConditionULONGINT() + label + '\n';
        SyntacticTreeFOR.jLabel.push(label);
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }
}
