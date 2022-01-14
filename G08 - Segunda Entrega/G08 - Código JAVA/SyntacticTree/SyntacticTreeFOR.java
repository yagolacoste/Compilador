package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;

import java.util.Stack;

public class SyntacticTreeFOR extends SyntacticTree{

    public static Stack<String> jLabel = new Stack<>();
    private static int counter = 0;
    public static Attribute ID;

    public SyntacticTreeFOR(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeFOR(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeFOR(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String label =  SyntacticTreeFOR.jLabel.pop();
        assembler += label + ":" + '\n';
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        return assembler;
    }
}
