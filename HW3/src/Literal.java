import java.util.ArrayList;

public class Literal {

    public enum type {CONSTANT, PREDICATE, VARIABLE, UNITITIALIZED}

    type literalType = type.UNITITIALIZED;
    String literalString;
    boolean not;


    public Literal(type literalType, String string){
        this.literalType = literalType;

        if(string.startsWith("~")) {
            this.not = true;
            this.literalString = string.substring(1, string.length());
        }
        else this.literalString = string;

    }



    public static Literal identifyLiteral(String part){
        Literal.type literalType = Literal.type.UNITITIALIZED;

        // look at every letter in the string
        for(int index = 0; index < part.length(); index++) {

            String letter = part.substring(index, index + 1);


            if(letter.equals("(")){
                // we've found a parenthesis so now we know it's actually a predicate. if statement is for error checking only
                if(literalType == Literal.type.CONSTANT) {
                    literalType = Literal.type.PREDICATE;
                    break;
                }
                else {
                    System.out.println("Found '(' before finding a capital letter");
                }
            }
            else if(isCapital(letter)){
                // if we find a capital letter, we know its at least a constant
                literalType = Literal.type.CONSTANT;
            }
        }

        // we've identified what the thing is by looking at every letter, now we create a literal return it
        Literal literal = new Literal(literalType, part);

        return literal;
    }


    public static Boolean isCapital(String letter) {

        if (letter.equals(letter.toUpperCase())) {
            return Boolean.TRUE;
        }
        else return Boolean.FALSE;
    }


    public ArrayList<ArrayList<String>> processPredicate() {
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<String> constants = new ArrayList<>();
        ArrayList<String> ordering = new ArrayList<>();
        ArrayList<ArrayList<String>> operands = new ArrayList<>();
        boolean lastLoop = false;
        boolean done = false;

        if (this.literalType == type.PREDICATE) {

            // predicate could have multiple inputs
            // let's look through them by starting at the open paren and looking to each comma
            int start = 1 + this.literalString.indexOf("(");
            int end;
            if(this.literalString.contains(",")){
                end = this.literalString.indexOf(",", start);
            }
            else {
                end = this.literalString.indexOf(")");
                lastLoop = true;
            }

            // look at predicate to find all the variables it operates on
            while(!done) {

                // operand is a thing the query predicate is operating on and should always be a constant
                String operand = this.literalString.substring(start, end);
                String firstLetter = operand.substring(0, 1);

                // let's see if the operand we found is a variable; if not, we just move on
                if (!isCapital(firstLetter)) {
                    // found a variable, add it to our list
                    variables.add(operand);
                }
                else{
                    // we found a constant, add to list
                    constants.add(operand);
                }
                // we need to preserve the order of the operands. It matters if P(Cat,Dog) vs P(Dog,Cat)
                ordering.add(operand);

                // update start for next loop
                int prevstart = start;
                start = end + 1;

                if(!lastLoop) {
                    // update end for next loop
                    if (this.literalString.substring(end + 1, this.literalString.length()).contains(",")) {
                        // check to see if there's another ',' present or if we're on our last operand
                        end = this.literalString.indexOf(",", start);
                    }
                    else {
                        // no commas left means we're now on the last constant the query predicate operates on
                        end = this.literalString.indexOf(")");
                        lastLoop = true;
                    }
                }
                else done = true;
            }
        }
        else{
System.out.println("ISSUE: called processPredicate on a non predicate");
        }
        operands.add(variables);
        operands.add(constants);
        operands.add(ordering);
        return operands;
    }




}
