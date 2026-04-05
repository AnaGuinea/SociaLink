import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {

    private List<Token> tokens = new ArrayList<>();

    public void analyzeFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            int lineNumber = 1;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (!line.isEmpty()) {
                    System.out.println("Ligne " + lineNumber + ": " + line);
                    analyzeLine(line);
                }

                lineNumber++;
            }

            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Erreur : fichier introuvable.");
        }
    }

    private void analyzeLine(String line) {
        String[] parts = line.split("\s+");

        if (parts.length == 0) {
            return;
        }

        if (!parts[0].startsWith("@")) {
            System.out.println("Erreur: mot-clé sans préfixe @");
            return;
        }

        String command = parts[0];

        if (!isKeyword(command)) {
            System.out.println("Erreur: mot-clé invalide -> " + command);
            return;
        }

        tokenize(parts);

        switch (command) {
            case "@connect":
            case "@follow":
            case "@unfollow":
            case "@block":
                validateArrowCommand(parts);
                break;

            case "@post":
                validatePost(parts);
                break;

            case "@like":
                validateLike(parts);
                break;

            case "@status":
                validateStatus(parts);
                break;

            case "@message":
                validateMessage(parts);
                break;

            case "@tag":
                validateTag(parts);
                break;

            case "@delete":
                validateDelete(parts);
                break;
        }
    }

    private void tokenize(String[] parts) {
        for (String word : parts) {
            if (isKeyword(word)) {
                tokens.add(new Token(TokenType.KEYWORD, word));
            }
            else if (isIdentifier(word)) {
                tokens.add(new Token(TokenType.IDENTIFIER, word));
            }
            else if (isString(word)) {
                tokens.add(new Token(TokenType.STRING, word));
            }
            else if (isHashtag(word)) {
                tokens.add(new Token(TokenType.HASHTAG, word));
            }
            else if (word.equals("->")) {
                tokens.add(new Token(TokenType.ARROW, word));
            }
            else if (isStatus(word)) {
                tokens.add(new Token(TokenType.STATUS_VAL, word));
            }
            else if (isSeparator(word)) {
                tokens.add(new Token(TokenType.SEPARATOR, word));
            }
            else {
                tokens.add(new Token(TokenType.UNKNOWN, word));
            }
        }
    }

    private void validateArrowCommand(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Erreur: format attendu -> @cmd IDENTIFIER -> IDENTIFIER");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant source invalide");
        }

        if (!parts[2].equals("->")) {
            System.out.println("Erreur: opérateur -> manquant");
        }

        if (!isIdentifier(parts[3])) {
            System.out.println("Erreur: identifiant destination invalide");
        }
    }

    private void validatePost(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Erreur: format attendu -> @post IDENTIFIER STRING HASHTAG+");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant invalide");
        }

        if (!isString(parts[2])) {
            System.out.println("Erreur: string invalide ou guillemets manquants");
        }

        for (int i = 3; i < parts.length; i++) {
            if (!isHashtag(parts[i])) {
                System.out.println("Erreur: hashtag invalide -> " + parts[i]);
            }
        }
    }

    private void validateLike(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Erreur: format attendu -> @like IDENTIFIER : IDENTIFIER");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant utilisateur invalide");
        }

        if (!parts[2].equals(":")) {
            System.out.println("Erreur: séparateur : manquant");
        }
    }

    private void validateStatus(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Erreur: format attendu -> @status IDENTIFIER = STATUS_VAL");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant utilisateur manquant ou invalide");
        }

        if (!parts[2].equals("=")) {
            System.out.println("Erreur: séparateur = manquant");
        }

        if (!isStatus(parts[3])) {
            System.out.println("Erreur: status invalide");
        }
    }

    private void validateMessage(String[] parts) {
        if (parts.length != 5) {
            System.out.println("Erreur: format attendu -> @message IDENTIFIER -> IDENTIFIER STRING");
            return;
        }

        if (!parts[2].equals("->")) {
            System.out.println("Erreur: opérateur -> manquant");
        }

        if (!isString(parts[4])) {
            System.out.println("Erreur: message invalide ou guillemets manquants");
        }
    }

    private void validateTag(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Erreur: format attendu -> @tag IDENTIFIER HASHTAG+");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant du post invalide");
        }

        for (int i = 2; i < parts.length; i++) {
            if (!isHashtag(parts[i])) {
                System.out.println("Erreur: hashtag invalide -> " + parts[i]);
            }
        }
    }

    private void validateDelete(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Erreur: format attendu -> @delete IDENTIFIER ;");
            return;
        }

        if (!isIdentifier(parts[1])) {
            System.out.println("Erreur: identifiant invalide");
        }

        if (!parts[2].equals(";")) {
            System.out.println("Erreur: symbole ; obligatoire à la fin");
        }
    }

    private boolean isKeyword(String word) {
        return word.matches("@(connect|post|follow|unfollow|like|status|block|tag|message|delete)");
    }

    private boolean isIdentifier(String word) {
        return word.matches("[a-z][a-z0-9_]*");
    }

    private boolean isString(String word) {
        return word.matches("\"[a-z0-9_]+\"");
    }

    private boolean isHashtag(String word) {
        return word.matches("#[a-z][a-z0-9]*");
    }

    private boolean isStatus(String word) {
        return word.matches("ACTIF|EN_ATTENTE|FERME");
    }

    private boolean isSeparator(String word) {
        return word.matches("[:=;]");
    }

    public void printTokens() {
        System.out.println("Liste des tokens reconnus:");

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
