import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APLCAssignment1PartC {
	public static String[] DATA_TYPES = {"int", "double", "String", "char", "long"};//etc
	public static String[] KEY_WORDS = {"if", "while", "for", "static", "class", "public", "private", "protected"};
	public static String[] FUNCTIONS = {"length", "print", "println"};
	public static String[] DELIMITERS = {"(", ")", "{", "}", "[", "]", ";", ":", ",", "."};
	public static String[] OPERATORS = {
			"+", "++", "--", "-", "*", "/",
			"=", "+=", "-=", "*=", "/=",
			"==", ">=", "<=", "<", ">",
			"&&", "||", "!="};
	public static String IDENTIFIER_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]*$";
	public static String NUMERICAL_LITERAL_PATTERN = "^[0-9]+(\\.[0-9]+)?$";

	public static String[][] SYNTAX_PATTERNS = {
			{//variable pattern
				"DATA_TYPE",
				"IDENTIFIER",
				"OPERATOR",
				"NUMERICAL_LITERAL",
				"DELIMITER"
			},
			//more to come eventually
			{//for loop statment 1 pattern
				"KEY_WORD",//for
				"DELIMITER", //(
				"DATA_TYPE",//int
				"IDENTIFIER",//i
				"OPERATOR",//=
				"NUMERICAL_LITERAL",//0
				"DELIMITER", //;
				"IDENTIFIER",//i
				"OPERATOR",//> or < or >= or <= or ==
				"IDENTIFIER",//nums
				"DELIMITER", //;
				"IDENTIFIER",//i
				"OPERATOR",//++ or --
				"DELIMITER", //;
				"DELIMITER",//)
				"DELIMITER",//{
			},
			{//statement closer
				"DELIMITER", //) or } or ;
			},
			//terminal process
			{
				"CLASS",//System
				"DELIMITER", //.
				"DELIMITER", //out
				"DELIMITER", //.
				"DELIMITER", //println or print
				"DELIMITER", //(

			}
	};

	//debug
	public static int data_type_count = 0;
	public static int key_word_count = 0;
	public static int function_count = 0;
	public static int delimiter_count = 0;
	public static int operator_count = 0;
	public static int id_count = 0;
	public static int num_count = 0;
	public static int unknown_count = 0;

	public static void main(String[] args) {
		//		String[] code_line = {"int", "x", "=", "4", ";"};//int x = 4;
		//int[] nums = {5, 8, 12, 4, 10}; double sum = 0; int nums_count = 0; double average = 0; for (int i = 0; i < nums.length; i++) { sum += nums[i]; nums_count++; } System.out.println("Average: " + (average = sum / nums_count));
		//get the line of code and print it all out
		String input = readUserInput();
		String[] code_line = splitCode(input);
		boolean valid_program = true;
		for (int i = 0; i < code_line.length; i++) {
			String line = code_line[i];
		    System.out.println("\nLine " + (i + 1) + ": " + line);
		    String[] tokens = tokenizeInput(line);
		    String[] token_types = new String[tokens.length];
		    
		    for (int j = 0; j < tokens.length; j++) {
		        token_types[j] = getTokenType(tokens[j]);
		        System.out.println(tokens[j] + " -> " + token_types[j]);
		    }
		    // Check syntax AFTER all tokens have been identified
		    if (checkSyntax(line, token_types)) System.out.println("Syntax: VALID");
		    else {
		        System.out.println("Syntax: INVALID");
		        valid_program = false;
		    }
		}
		System.out.println("\n--------------------");
	    if (valid_program) {
	        System.out.println("Entire program has valid syntax.");
	    } else {
	        System.out.println("Entire program has invalid syntax.");
	    }

		//check to see how many of each token there is
		System.out.println("\nTOKEN COUNT:");
		System.out.println("Data types: " +  data_type_count);
		System.out.println("Key words: " +  key_word_count);
		System.out.println("Functions: " +  function_count);
		System.out.println("Operators: " +  operator_count);
		System.out.println("Delimiters: " +  delimiter_count);
		System.out.println("Identifiers: " +  id_count);
		System.out.println("Numberical literals: " +  num_count);
		System.out.println("Unknowns: " +  unknown_count);
	}

	public static String readUserInput() {
		//if i type in
		//int x = 4;
		//into the scanner, then the custom line of code would
		//code_line = ["int", "x", "=", "4", ";"];
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter a line of code:");
		return scanner.nextLine();
	}

	public static String[] splitCode(String input) {
		ArrayList<String> lines = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		int parentheses = 0;
	    int array_braces = 0;
	    boolean in_string = false;
	    boolean in_char = false;
	    boolean escaped = false;
	    
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			current.append(c);
			
			if (escaped) {
				escaped = false;
				continue;
			}
			
			if ((in_string || in_char) && c == '\\') {
	            escaped = true;
	            continue;
	        }
	        if (!in_char && c == '"') {
	            in_string = !in_string;
	            continue;
	        }
	        if (!in_string && c == '\'') {
	            in_char = !in_char;
	            continue;
	        }
	        if (in_string || in_char) {
	            continue;
	        }
	        if (c == '(') parentheses++;
	        if (c == ')') parentheses--;
	        // An opening brace after '=' is treated as
	        // an array initializer in this assignment.
	        if (c == '{') {
	            String before_brace = current.substring(
	                0, current.length() - 1
	            ).trim();

	            if (before_brace.endsWith("=")) {
	                array_braces++;
	            } else {
	                lines.add(current.toString().trim());
	                current.setLength(0);
	            }
	        }

	        if (c == '}' && array_braces > 0) {
	            array_braces--;
	        } else if (c == '}') {
	            if (!current.toString().trim().isEmpty()) {
	                lines.add(current.toString().trim());
	            }

	            current.setLength(0);
	            lines.add("}");
	        }

	        // A semicolon ends a statement only when it is
	        // outside parentheses and array initializers.
	        if (c == ';' && parentheses == 0
	                && array_braces == 0) {
	            lines.add(current.toString().trim());
	            current.setLength(0);
	        }
	    }

	    // Keep any remaining text.
	    if (!current.toString().trim().isEmpty()) {
	        lines.add(current.toString().trim());
	    }

	    return lines.toArray(new String[0]);
	}
	
	public static String[] tokenizeInput(String input) {
	    ArrayList<String> tokens = new ArrayList<>();

	    Pattern pattern = Pattern.compile(
	        "\"(?:\\\\.|[^\"\\\\])*\"" +
	        "|'(?:\\\\.|[^'\\\\])*'" +
	        "|\\+\\+|--|\\+=|-=|\\*=|/=" +
	        "|==|>=|<=|&&|\\|\\||!=" +
	        "|[a-zA-Z_][a-zA-Z0-9_]*" +
	        "|[0-9]+(?:\\.[0-9]+)?" +
	        "|[(){}\\[\\];:,.+\\-*/=<>]"
	    );

	    Matcher matcher = pattern.matcher(input);

	    while (matcher.find()) {
	        tokens.add(matcher.group());
	    }

	    return tokens.toArray(new String[0]);
	}
	public static String getTokenType(String token) {
	    for (String type : DATA_TYPES) {
	        if (token.equals(type)) {
	            data_type_count++;
	            return "DATA_TYPE";
	        }
	    }
	    for (String keyword : KEY_WORDS) {
	        if (token.equals(keyword)) {
	            key_word_count++;
	            return "KEYWORD";
	        }
	    }
	    for (String function : FUNCTIONS) {
	        if (token.equals(function)) {
	            function_count++;
	            return "FUNCTION";
	        }
	    }
	    for (String operator : OPERATORS) {
	        if (token.equals(operator)) {
	            operator_count++;
	            return "OPERATOR";
	        }
	    }
	    for (String delimiter : DELIMITERS) {
	        if (token.equals(delimiter)) {
	            delimiter_count++;
	            return "DELIMITER";
	        }
	    }
	    if (token.matches("\"(?:\\\\.|[^\"\\\\])*\"")) {
	        return "STRING_LITERAL";
	    }
	    if (token.matches("'(?:\\\\.|[^'\\\\])*'")) {
	        return "CHAR_LITERAL";
	    }
	    if (token.matches(NUMERICAL_LITERAL_PATTERN)) {
	        num_count++;
	        return "NUMERICAL_LITERAL";
	    }
	    if (token.matches(IDENTIFIER_PATTERN)) {
	        id_count++;
	        return "IDENTIFIER";
	    }

	    unknown_count++;
	    return "UNKNOWN";
	}
	public static boolean checkSyntax(String line, String[] token_types) {
	    line = line.trim();
	    // Empty statements are not useful input.
	    if (line.isEmpty()) {
	        return false;
	    }
	    // Reject unrecognized tokens.
	    for (String type : token_types) {
	        if (type.equals("UNKNOWN")) {
	            return false;
	        }
	    }
	    // Closing brace.
	    if (line.equals("}")) {
	        return true;
	    }
	    // Scalar variable declaration:
	    // int x = 4;
	    // double sum = 0;
	    // String message = "Hello";
	    if (line.matches(
	            "(int|double|String|char|long)\\s+" +
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*" +
	            "(\"(?:\\\\.|[^\"\\\\])*\"|" +
	            "'(?:\\\\.|[^'\\\\])*'|" +
	            "[0-9]+(?:\\.[0-9]+)?)\\s*;")) {
	        return true;
	    }
	    // Integer array declaration:
	    // int[] nums = {5, 8, 12};
	    if (line.matches(
	            "int\\s*\\[\\s*\\]\\s+" +
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*" +
	            "\\{\\s*[0-9]+(\\s*,\\s*[0-9]+)*\\s*}\\s*;")) {
	        return true;
	    }
	    // For-loop header:
	    // for (int i = 0; i < nums.length; i++) {
	    if (line.matches(
	            "for\\s*\\(\\s*int\\s+" +
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*[0-9]+\\s*;" +
	            "\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*" +
	            "(<|>|<=|>=|==|!=)\\s*" +
	            "[a-zA-Z_][a-zA-Z0-9_]*\\.length\\s*;" +
	            "\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(\\+\\+|--)\\s*" +
	            "\\)\\s*\\{")) {
	        return true;
	    }
	    // Increment statement:
	    // nums_count++;
	    if (line.matches(
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*(\\+\\+|--)\\s*;")) {
	        return true;
	    }
	    // Assignment with an array element:
	    // sum += nums[i];
	    if (line.matches(
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*" +
	            "(\\+=|-=|\\*=|/=|=)\\s*" +
	            "[a-zA-Z_][a-zA-Z0-9_]*\\s*" +
	            "\\[\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*]\\s*;")) {
	        return true;
	    }
	    // Basic System.out.print/println statement.
	    if (line.matches(
	            "System\\.out\\.(print|println)\\s*\\(.*\\)\\s*;")) {
	        return true;
	    }
	    return false;
	}}
