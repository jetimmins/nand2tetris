package assembler;

public class AssemblerMainline {
    public static void main(String[] args) {
        String in = args[0];
        Assembler assembler = new Assembler(in);
        assembler.assemble();
    }

}
