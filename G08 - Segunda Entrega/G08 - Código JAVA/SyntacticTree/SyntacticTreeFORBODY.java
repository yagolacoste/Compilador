package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;

public class SyntacticTreeFORBODY extends SyntacticTree {

    public SyntacticTreeFORBODY(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeFORBODY(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeFORBODY(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgister) {
        String assembler = "";
        String auxLabel = SyntacticTreeFOR.jLabel.pop();
        String label = SyntacticTreeFOR.jLabel.pop();
        assembler += "JMP " + label + '\n';
        SyntacticTreeFOR.jLabel.push(auxLabel);
        return assembler;

    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }
}
