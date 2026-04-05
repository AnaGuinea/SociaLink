public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();

        lexer.analyzeFile("SociaLink/src/test2.txt");

        System.out.println("Liste des tokens reconnus :\n");
        lexer.printTokens();
        
    }
}