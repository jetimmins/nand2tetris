package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static compiler.TokenRegex.*;
import static compiler.TokenType.*;
import static java.util.stream.Stream.concat;

public class JackTokeniser implements AutoCloseable {
    private final BufferedReader reader;
    private final Deque<String> tokens = new ArrayDeque<>();
    private String currentToken;

    public JackTokeniser(Path src) {
        Stream<String> tokenStream = Stream.of();
        String line;

        try {
            reader = new BufferedReader(new FileReader(src.toFile()));
            while ((line = reader.readLine()) != null) {
                tokenStream = concat(tokenStream, tokeniserRegex()
                        .matcher(removeInlineComment(line))
                        .results()
                        .map(MatchResult::group));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error opening or reading from source code file", e);
        }

        Deque<String> withComments = tokenStream.collect(Collectors.toCollection(ArrayDeque::new));
        if (withComments.isEmpty()) {
            return;
        }

        while (!withComments.isEmpty()) {
            clearBlockComments(withComments);
            if (!withComments.isEmpty()) {
                tokens.add(withComments.pop());
            }
        }
    }

    private void clearBlockComments(Deque<String> withComments) {
        if (!withComments.isEmpty()) {
            currentToken = withComments.peek();
            if (commentBlockRegex().matcher(currentToken).matches()) {
                currentToken = withComments.pop(); // discard block open
                currentToken = withComments.pop(); // load next
                while (!commentBlockRegex().matcher(currentToken).matches()) {
                    currentToken = withComments.pop(); // discard contents until block close
                }
                clearBlockComments(withComments);
            }
        }
    }

    private String removeInlineComment(String line) {
        int i = line.indexOf("//");
        return i >= 0 ? line.substring(0, i) : line;
    }

    public Token token() {
        if (symbolRegex().matcher(currentToken).matches()) {
            String symbol = switch (currentToken) {
                case "<" -> "&lt;";
                case ">" -> "&gt;";
                case "&" -> "&amp;";
                default -> currentToken;
            };

            return new Token(SYMBOL, symbol);
        } else if (stringRegex().matcher(currentToken).matches()) {
            return new Token(STRING_CONSTANT, currentToken.substring(1, currentToken.length() - 1));
        } else if (numberRegex().matcher(currentToken).matches()) {
            return new Token(INTEGER_CONSTANT, currentToken);
        } else if (keywordRegex().matcher(currentToken).matches()) {
            return new Token(KEYWORD, Keyword.fromJack(currentToken).toString().toLowerCase());
        } else if (idRegex().matcher(currentToken).matches()) {
            return new Token(IDENTIFIER, currentToken);
        }

        return new Token(ERROR, "Invalid token!");
    }

    public void advance() {
        if (!hasMoreTokens()) {
            throw new RuntimeException("Tried to advance when no more symbols remain");
        }

        currentToken = tokens.pop();
    }

    public boolean hasMoreTokens() {
        return !tokens.isEmpty();
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing source code file", e);
        }
    }
}
