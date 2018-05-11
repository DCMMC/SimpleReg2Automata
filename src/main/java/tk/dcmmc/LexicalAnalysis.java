package main.java.tk.dcmmc;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.lang.System.out;

/**
 * symbol table of a morpheme
 */
class SymbolTable {
    String regex;
    int attributionValue;
    // 标识符, 无符号整数, 保留字, 运算符, 界符, 注释
    String typeName;

    SymbolTable(String regex, int attributionValue, String typeName) {
        this.regex = regex;
        this.attributionValue = attributionValue;
        this.typeName = typeName;
    }
}

/**
 * Pascal-Like Lexical Analyzer
 *
 * steps: regular exp => NFA => DFA
 * we can also use lexical-analyzer generator(e.g. lex) to auto generate analyzer using high-level
 * lexical pattern info.
 *
 * @since 1.8
 * @author DCMMC
 */
public class LexicalAnalysis {
    // Notes:
    //
    // lexical analysis is the first period of compiler.
    // Tasks:
    // 1. filter and remove space characters 'cause of pascal free-format.
    // 2. associate the error info with location of source file.
    // Two phase:
    // 1. scan: remove comments and compress multi space characters.
    // 2. analyzer: generator word units.
    //
    // attributions of a morpheme(词素) unit always contain type name, location, word, etc.
    //
    // handle morpheme errors:
    // addition to judge id's legality in phase grammar analysis, one simple error recover strategy is
    // called "panic mode", which try to add, remove, change just *one* character or change one pair characters
    // to watch if it became legal.
    //
    // as for multi characters morpheme such as '->', there always are two pointers: 'lexemeBegin' indicates the
    // position of first character of the morpheme, and then use point 'forward' to scan sequential until match any
    // pattern. After match any match, pointer 'forward' will go back one position, and if we use buffers(e.g.
    // 4096 buffer) to read from file, it is better to use 'eof' as sentinel(哨兵).
    // double buffer can solve long morpheme.
    //
    // for some language that not have reserved key word like PL/I, there is Ambiguity(二义性) when encounter using key
    // word as identifier. In such case, grammar analysis should inquire(查询) from symbol table(符号表).
    // besides, '"' also has ambiguity 'cause of left '"' is differ from right '"'
    //
    // string:
    // empty string: $\epsilon$
    // length of string 's': |s|
    // prefix(not include $\epsilon$ if true-prefix, same as suffix), suffix, substring(string remove prefix or suffix)
    // sub-sequence is string remove 0 or more characters
    //
    // three basic operation of regular expression(also is string operations):
    // concatenation: xy (if x and y are string, Left combination)
    // or: x|y (lowest priority, Left combination)
    // (Kleene) closure: x* (highest priority, Left combination)
    //
    // parentheses: regular set
    //
    // exponent: x^n (n piece of x concat), x^0 = $\epsilon$
    // positive closure: x+
    //
    // some useful extends:
    // ?, {lower, upper}, [elements_list], [begin_element-end_element], ., ^(not)
    //
    // complicate regular exp may recursive defined by some simple sub regular expressions.
    //
    // alpha table: $\Sigma$
    //
    // some simple equivalent properties seen P77
    //
    // regular definition(应该就是正则文法):
    // [new regular element] -> [regular exp using elements in $\Sigma$ and previous new regular element]
    // e.g. $\Sigma$ = {all alpha}
    // letter_ -> A | B | ... | a | ... | z | _
    // digit -> 0 | 1 | ... | 9
    // id -> letter_(letter_ | digit)*
    //
    // Ex 3.3.3
    // (4) $\sum_{i = 0}^n \sum_{j = 0}^{n - i}$
    // (5) $\sum_{i = 0}^n C_{n}^i$
    // Ex 3.3.5
    // (3) /\*([^*\"]|\".\"|\*+[^/])*\*/
    static {
        // Ex 3.3.5
//        String comment = "/* some comments\"inner*/\"*/", wrong = "/* /*some comments \"*\\\"inner*/*/";
//        out.println("comment: " + comment.matches("/\\*([^*\"]|\".*\"|\\*+[^/])*\\*/") +
//                ", wrong: " + wrong.matches("/\\*([^*\"]|\".*\"|\\*+[^/])*\\*/"));
    }


    /**
     * Lab 1 helper method
     * judging if c is alpha or '_' for integer literal or identifier detecting.
     *
     * FIXME I *ASSUME* that single '0' is legal while multi-bit integer begin with '0' like '0110' is illegal
     *
     * @param c char to be tested
     * @return if c is digit, alpha or '_'
     */
    private static boolean isDigitAlpha(final char c) {
        return (c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A') || (c <= '9' && c >= '0') || c == '_';
    }

    /**
     * Lab 1 helper method
     * judge if c is digit, i.e. 0 ~ 9
     */
    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Lab 1 helper method
     * operation may have two characters, such as '<=', '=='
     * @param c char to be tested
     */
    private static int twoCharOpFirst(final char c) {
        switch (c) {
            case '<' : return 1;
            case '>' : return 2;
            case '=' : return 3;
            // match comments: //, /* and */
            case '/' : return 4;
            case '*' : return 5;
            default: return -1;
        }
    }

    /**
     * Lab 1 helper method
     * second character of operation that has two characters
     */
    private static boolean isTwoCharOpSecond(final char c, final int type) {
        switch (type) {
            case 1 : return c == '=' || c == '>';
            case 2 :
            case 3 : return c == '=';
            case 4 : return c == '*' || c == '/';
            case 5 : return c == '/';
            default: return false;
        }
    }

    /**
     * test if c is single character operation or bound char
     */
    private static boolean isSingleCharOp(final char c) {
        return "+-(),;".indexOf(c) > -1;
    }

    /**
     * test if c is white space char
     */
    private static boolean isWhiteChar(final char c) {
        return Character.isWhitespace(c);
    }

    /**
     * report error
     */
    private static void reportError(StringBuilder sb, int startRow, int startCol, String reason) {
        String error = "ERROR encounter: Start from (" + startRow + ", " + startCol + "), Reason: " + reason + "\n";
        out.println(error);
        sb.append(error);
    }

    /**
     * generate output String
     */
    private static String generateOutput(final SymbolTable symbol, int startRow, int startCol, int endRow,
                                         int endCol, String s) {
        return String.format("(%6d, %.8s)\t%6d\t%6d\t%6d\t%6d\t%.21s", symbol.attributionValue, symbol.typeName, startRow,
                startCol, endRow, endCol, s);
//        return String.format("(%6d, %6s)\t%6d\t%6d\t%.21s", symbol.attributionValue, 0, startRow, startCol, s);
    }

    /**
     * generate output String
     */
    private static String generateOutput(final SymbolTable symbol, int startRow, int startCol, int endRow,
                                         int endCol, char c) {
        return String.format("(%6d, %.8s)\t%6d\t%6d\t%6d\t%6d\t%.21s", symbol.attributionValue, symbol.typeName, startRow,
                startCol, endRow, endCol, c);
//        return String.format("(%6d, %6s)\t%6d\t%6d\t%.21s", symbol.attributionValue, 0, startRow, startCol, c);
    }

    /**
     *
     * Lab 1 simple analyzer
     *
     * strict:
     * 1. free-format, one line may contain more than one statements.
     * 2. comments are C-style(one line and multiline comments, and ***multiline not allowed nested comments***)
     * 3. lexical rules:
     * (1). identifier: start with '_' or alpha and be followed by alphas, digits and '_', no more than 20 chars.
     * (2). unsigned integers literal: decimal base, first digits never be '0'(??? why do so ???), no +/- sign
     * (3). reserved key words: procedure、def、if、else、while、call、begin、end、and、or (case-insensitive)
     * (4). operators: ＋, －, *, /, =, <, >, <=, >=, <>, ==
     * (5). bound character: (, ), ',', ;
     *
     * input: source text file
     * output: a file, and each line contain a word, and the format is:
     * ([lexical_attribution], []) row_num col_num [word]
     *
     */
    private static void simpleAnalyzer(final Path sourceFile) {
        // Symbol Table
        // case-insensitive
        final SymbolTable IDENTIFIER = new SymbolTable("[A-Za-z_]{1,1}[A-Za-z0-9_]{0, 19}", 1,
                "标识符");
        final SymbolTable DIGITS = new SymbolTable("[1-9]+[0-9]*", 2, "无符号整数");
        final SymbolTable RESERVES = new SymbolTable("(?i)procedure|def|if|else|while|call|begin|end|and|or",
                3, "保留字");
        // operations
        final SymbolTable PLUS = new SymbolTable("\\+", 4, "运算符");
        final SymbolTable MINUS = new SymbolTable("-", 5, "运算符");
        final SymbolTable TIMES = new SymbolTable("\\*", 6, "运算符");
        final SymbolTable DIVIDE = new SymbolTable("/", 7, "运算符");
        final SymbolTable ASSIGN = new SymbolTable("=", 8, "运算符");
        final SymbolTable LESS = new SymbolTable("<", 9, "运算符");
        final SymbolTable GREAT = new SymbolTable(">", 10, "运算符");
        final SymbolTable LESS_EQUAL = new SymbolTable("<=", 11, "运算符");
        final SymbolTable GREAT_EQUAL = new SymbolTable(">=", 12, "运算符");
        final SymbolTable NOT_EQUAL = new SymbolTable("<>", 13, "运算符");
        final SymbolTable EQUAL = new SymbolTable("==", 14, "运算符");
        // bound character
        final SymbolTable LEFT_PARENTHESES = new SymbolTable("\\(", 15, "界符");
        final SymbolTable RIGHT_PARENTHESES = new SymbolTable("\\)", 16, "界符");
        final SymbolTable COMMA = new SymbolTable(",", 17, "界符");
        final SymbolTable SEMICOLON = new SymbolTable(";", 18, "界符");
        // comments
        // final SymbolTable SINGLE_COMMENT = new SymbolTable("(?m)//.*$", 19, "注释");
        // allow '/*' in /**/ but not allow '*/' in /**/
        // final SymbolTable MULTI_COMMENTS = new SymbolTable("/\\*[^(\\*/)]*\\*/", 20, "注释");

        StringBuilder output = new StringBuilder();
        try (InputStream in = Files.newInputStream(sourceFile);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {

            String line;
            int startRow = 0, startCol = 0, endCol, lineNum = 0;
            // flags: [two char Op][digit alpha _][comment / close symbol]
            char flags = 0b000;
            // output line
            String outputOneLine = "";
            // one word token
            StringBuilder token = new StringBuilder();
            // type of two character operation
            int type = -1;

//            String topLine = String.format("%6s\t%4s\t%4s\t%4s\t%.21s\n", "类型代号", "类别代号", "起始行号", "起始列号",
//                    "词素");
            String topLine = String.format("(类型代号, %.6s)\t起始行号\t起始列号\t结束行号\t结束列号\t%.21s\n", "类型类别",
                    "词素");
            out.println(topLine);
            output.append(topLine);
            while ((line = reader.readLine()) != null) {
                lineNum++;

                endCol = 1;
                // single line comment
                if (flags == 0b001 && lineNum == startRow + 1 && token.substring(0, 2).equals("//")) {
                    // end of one line comment
                    flags = 0b000;
                }
                while (endCol <= line.length()) {
                    char c = line.charAt(endCol - 1);
                    switch (flags) {
                        // match first character of new morpheme
                        case 0b000 :
                            // clear token
                            token.delete(0, token.length());
                            if (isWhiteChar(c)) {
                                endCol++;
                                break;
                            }
                            else if (isDigitAlpha(c)) {
                                startRow = lineNum;
                                startCol = endCol;
                                flags = 0b010;
                                token.append(c);
                            } else if(isSingleCharOp(c)){
                                startRow = lineNum;
                                startCol = endCol;
                                // out
                                switch (c) {
                                    case '+' : outputOneLine = generateOutput(PLUS, startRow, startCol, startRow,
                                                startCol, c); break;
                                    case '-' : outputOneLine = generateOutput(MINUS, startRow, startCol, startRow,
                                                startCol, c); break;
                                    case '(' : outputOneLine = generateOutput(LEFT_PARENTHESES, startRow, startCol,
                                                startRow, startCol, c); break;
                                    case ')' : outputOneLine = generateOutput(RIGHT_PARENTHESES, startRow, startCol,
                                                startRow, startCol, c); break;
                                    case ',' : outputOneLine = generateOutput(COMMA, startRow, startCol,
                                            startRow, startCol, c); break;
                                    case ';' : outputOneLine = generateOutput(SEMICOLON, startRow, startCol,
                                            startRow, startCol, c); break;
                                }
                                out.println(outputOneLine);
                                output.append(outputOneLine).append("\n");
                            } else {
                                type = twoCharOpFirst(c);
                                if (type > -1) {
                                    startRow = lineNum;
                                    startCol = endCol;
                                    flags = 0b100;
                                    // token[0] is the first character of two character operation
                                    token.append(c);
                                } else {
                                    // error
                                    // in the case, ':', ''', '?', '`', '~', '!', '@', '#', '$', '%' are all illegal
                                    reportError(output, lineNum, endCol, "unknown single character '" + c + "'");
                                }
                            }
                            endCol++;
                            break;
                        case 0b100 :
                            // match two or one character operation
                            if (isTwoCharOpSecond(c, type)) {
                                switch (token.toString() + c) {
                                    case "<=" : outputOneLine = generateOutput(LESS_EQUAL, startRow, startCol, lineNum,
                                            endCol, token.toString() + c);
                                            flags = 0b000; break;
                                    case ">=" : outputOneLine = generateOutput(GREAT_EQUAL, startRow, startCol, lineNum,
                                            endCol, token.toString() + c);
                                            flags = 0b000; break;
                                    case "<>" : outputOneLine = generateOutput(NOT_EQUAL, startRow, startCol, lineNum,
                                            endCol, token.toString() + c);
                                            flags = 0b000; break;
                                    case "==" : outputOneLine = generateOutput(EQUAL, startRow, startCol, lineNum,
                                            endCol, token.toString() + c);
                                            flags = 0b000; break;
                                    case "//" :
                                    case "/*" :
                                            token.append(c);
                                            flags = 0b001; break;
                                }
                                endCol++;
                            } else {
                                // single character operation, i.e., <, >, *, /, =
                                switch (token.charAt(0)) {
                                    case '<' : outputOneLine = generateOutput(LESS, startRow, startCol, startRow,
                                                startCol, token.charAt(0));
                                        flags = 0b000; break;
                                    case '>' : outputOneLine = generateOutput(GREAT, startRow, startCol, startRow,
                                            startCol, token.charAt(0));
                                        flags = 0b000; break;
                                    case '*' : outputOneLine = generateOutput(TIMES, startRow, startCol, startRow,
                                            startCol, token.charAt(0));
                                        flags = 0b000; break;
                                    case '/' : outputOneLine = generateOutput(DIVIDE, startRow, startCol, startRow,
                                            startCol, token.charAt(0));
                                        flags = 0b000; break;
                                    case '=' : outputOneLine = generateOutput(ASSIGN, startRow, startCol, startRow,
                                            startCol, token.charAt(0));
                                        flags = 0b000; break;
                                }
                                // don't plus endCol!
                            }
                            if (flags != 0b001) {
                                out.println(outputOneLine);
                                output.append(outputOneLine).append("\n");
                            }
                            break;
                        case 0b010 :
                            // match unsigned integer or identifier
                            if (isDigitAlpha(c)) {
                                token.append(c);
                                endCol++;
                            } else {
                                // end of one integer or identifier morpheme
                                if (token.charAt(0) == '0' && token.length() > 1) {
                                    // e.g. 00111 or 01
                                    reportError(output, startRow, startCol, "integer literal \"" + token.toString()
                                        + "\" is illegal because of it begin with '0' and its length more than one.");
                                    // endCol not plus 1!
                                    flags = 0b000;
                                    break;
                                } else if (isDigit(token.charAt(0))) {
                                    // integer literal
                                    outputOneLine = generateOutput(DIGITS, startRow, startCol, lineNum, endCol,
                                            token.toString());
                                } else if(token.toString().matches(RESERVES.regex)) {
                                    // reserves
                                    outputOneLine = generateOutput(RESERVES, startRow, startCol, lineNum, endCol,
                                            token.toString());
                                } else if(token.length() <= 20) {
                                    // identifier
                                    outputOneLine = generateOutput(IDENTIFIER, startRow, startCol, lineNum, endCol,
                                            token.toString());
                                } else {
                                    // identifier but length more than 20
                                    outputOneLine = "ERROR encounter: Start from (" + startRow + ", "
                                            + startCol + "), Reason: " + "identifier length must <= 20" + "\n";
                                }
                                flags = 0b000;
                                out.println(outputOneLine);
                                output.append(outputOneLine).append("\n");
                                // endCol not plus 1!
                            }
                            break;
                        case 0b001 :
                            // comment
                            // token.length() must more than 3 to cover "/*/* sth but not response */"
                            if (token.length() >= 3 && c == '/' && token.charAt(token.length() - 1) == '*') {
                                // end of multi comments
                                // skip it simply
                                endCol++;
                                flags = 0b000;
                            } else {
                                if (endCol++ == 1)
                                    token.append('\n');
                                token.append(c);
                            }
                            break;
                    } // end of flags switch
                }
            }
            // handle multi line comments error, e.g. /* sth but not have */ response
            if (flags == 0b001) {
                reportError(output, startRow, startCol, "error multi line comments: \n```java\n" + token.toString()
                        + "\n```");
            }
        } catch (IOException x) {
            x.printStackTrace();
        }

        // output to a file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(sourceFile.getParent().toString(),
                sourceFile.getFileName().toString() + ".out"))) {
            writer.write(output.toString(), 0, output.length());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * test entry
     * @param args
     *      args[0] indicates test source file in root dir of classpath
     */
    public static void main(String[] args) {
        // Lab 1
        URL path = LexicalAnalysis.class.getClassLoader().getResource(args[0]);
        if(path != null)
            simpleAnalyzer(Paths.get(path.getPath()));
    }

}///~
