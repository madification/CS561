

import java.util.ArrayList;
import java.util.Hashtable;

public class Unification {

    public static Hashtable<String, String> tempUnifiedVars = new Hashtable<>();
    public static Hashtable<String, String> permUnifiedVars = new Hashtable<>();

    public Unification() {

    }

    public boolean unificationCheck(Literal fromkbSentence, Literal query) {
        boolean unifyForResolution = false;
        boolean done = false;
        ArrayList<ArrayList<String>> predOperands;
        ArrayList<String> availableVars = new ArrayList<>();
        ArrayList<String> availableConstants = new ArrayList<>();
        ArrayList<String> orderingKB = new ArrayList<>();
        ArrayList<ArrayList<String>> queryOperands;
        ArrayList<String> queryConstants = new ArrayList<>();
        ArrayList<String> queryVariables = new ArrayList<>();
        ArrayList<String> orderingQuery = new ArrayList<>();

        // let's figure out what our predicate from the kb operates on
        predOperands = fromkbSentence.processPredicate();
        if (predOperands.size() > 3) {
            System.out.println("ISSUE: wound up with more than a constants and variables list after processing a predicates");
        }
        else {
            if (!predOperands.get(0).isEmpty() && !Literal.isCapital(predOperands.get(0).get(0).substring(0, 1))) {
                // we found the list of constants
                availableVars.addAll(predOperands.get(0));
            }
            if (!predOperands.get(1).isEmpty() && Literal.isCapital(predOperands.get(1).get(0).substring(0, 1))) {
                // we got some variables
                availableConstants.addAll(predOperands.get(1));
            }
            orderingKB.addAll(predOperands.get(2));
        }

        // now lets find out what the query wants
        queryOperands = query.processPredicate();
        if (queryOperands.size() > 3) {
            System.out.println("ISSUE: wound up with more than a constants, a variables, and an ordering list after processing a predicates");
        }
        else {
            if (!queryOperands.get(0).isEmpty()) {
                // the list at index 0 contains stuff, check to see if it's constants or vars
                if (!Literal.isCapital(queryOperands.get(0).get(0).substring(0, 1))) {
                    queryVariables.addAll(queryOperands.get(0));
                }
                else {
                    queryConstants.addAll(queryOperands.get(0));
                }
            }
            // the list at 0 was empty or were there variables, check the list at 1
            if (!queryOperands.get(1).isEmpty()) {
                if (Literal.isCapital(queryOperands.get(1).get(0).substring(0, 1))) {
                    queryConstants.addAll(queryOperands.get(1));
                }
                else {
                    queryVariables.addAll(queryOperands.get(1));
                }
            }
            orderingQuery.addAll(queryOperands.get(2));
        }

        //holy moley, now we're finally ready to try to unify
        // the easiest thing to check is to see if the two have the same number of operands
        if (orderingKB.size() == orderingQuery.size()) {
            // the next easiest thing to check is if there is a constant in the predicate from the kb that is not in the query

            // if we made it here, then we can now try to address each operand in the query
            for (int i = 0; i < orderingQuery.size(); i++) {
                // search through each of the constants in the query and see if we can match it to one in the predicate from the kb
                // or see if we can unify it to a variable
                String toMatch = orderingQuery.get(i);
                // if the constant already matches the operand of the predicate, set our return variable to true
                if (orderingKB.get(i).equals(toMatch)) unifyForResolution = true;
                // if kb position is a constant and we have a constant, they didn't match so we cannot unify
                else if(Literal.isCapital(orderingKB.get(i).substring(0, 1)) && Literal.isCapital(orderingQuery.get(i).substring(0, 1))){
                    unifyForResolution = false;
                    return unifyForResolution;
                }
                // if the kb position has a variable and we have a constant
                else if (!Literal.isCapital(orderingKB.get(i).substring(0, 1)) && Literal.isCapital(toMatch.substring(0, 1))) {
                    // our current location in the predicate from the kb is a variable
                    // check to see if we can use it
                    if (!tempUnifiedVars.containsKey(orderingKB.get(i)) && !permUnifiedVars.containsKey(orderingKB.get(i))) {
                        // it's not in either of our hash tables
                        tempUnifiedVars.put(orderingKB.get(i), toMatch);
                        unifyForResolution = true;
                    }
                    else {
                        // the variable has already been unified, see if the constant it's been assigned to matches the one we need to match
                        if (permUnifiedVars.containsKey(orderingKB.get(i))) {
                            if (!toMatch.equals(permUnifiedVars.get(orderingKB.get(i)))) {
                                // we've already permanently unified this variable and it isn't mapped to the constant we need,
                                // thus we can't get the constant we need in the position we need therefore we cannot unify/resolve
                                unifyForResolution = false;
                                return unifyForResolution;
                            }
                            // this variable has already been unified and it was unified to the constant we need! amazing!
                            else unifyForResolution = true;
                        }
                        else if (tempUnifiedVars.containsKey(orderingKB.get(i))) {
                            if (!toMatch.equals(tempUnifiedVars.get(orderingKB.get(i)))) {
                                unifyForResolution = false;
                                return unifyForResolution;
                            }
                            else unifyForResolution = true;
                        }
//                else if (orderingKB.contains(toMatch)) {
//                    // the predicate from the kb contains the constant we're trying to match, but not at the current location
//                    // get the location of the constant in the predicate
//                    int index = orderingKB.indexOf(toMatch);
//                    // get the constant from the same location in the query
//                    String shouldMatch = orderingQuery.get(index);
//                    // if these don't match, then we can't unify/resolve
//                    if (!shouldMatch.equals(toMatch)) {
//                        unifyForResolution = false;
//                        return unifyForResolution;
//                    }
//                }
                        else {
                            // I think that covers all the possible options here otherwise
                            System.out.println("ISSUE: I think I broke Java... Unification Line 126");
                        }
                    }
                }
                // if the kb is a constant and we are a variable
                else if(Literal.isCapital(orderingKB.get(i).substring(0, 1)) && !Literal.isCapital(toMatch.substring(0, 1))){
                    String tempConstant = orderingKB.get(i);
                    String tempVariable = toMatch;
                    // check to see if we can use it
                    if (!tempUnifiedVars.containsKey(tempVariable) && !permUnifiedVars.containsKey(tempVariable)) {
                        // it's not in either of our hash tables
                        tempUnifiedVars.put(tempVariable, tempConstant);
                        unifyForResolution = true;
                    }
                    else {
                        // the variable has already been unified, see if the constant it's been assigned to matches the one we need to match
                        if (permUnifiedVars.containsKey(tempVariable)) {
                            if (!tempConstant.equals(permUnifiedVars.get(tempVariable))) {
                                // we've already permanently unified this variable and it isn't mapped to the constant we need,
                                // thus we can't get the constant we need in the position we need therefore we cannot unify/resolve
                                unifyForResolution = false;
                                return unifyForResolution;
                            }
                            // this variable has already been unified and it was unified to the constant we need! amazing!
                            else unifyForResolution = true;
                        }
                        else if (tempUnifiedVars.containsKey(tempVariable)) {
                            if (!tempConstant.equals(tempUnifiedVars.get(tempVariable))) {
                                unifyForResolution = false;
                                return unifyForResolution;
                            }
                            else unifyForResolution = true;
                        }
                        else {
                            // I think that covers all the possible options here otherwise
                            System.out.println("ISSUE: I think I broke Java... Unification Line 126");
                        }
                    }
                }
                else {
                    //both variables so yes we can definitely unify
                    unifyForResolution = true;
                }
            }
        }
        else {
            // the two predicates don't have the same number of operands so they can't be resolved
            // or the query had variables in it
            unifyForResolution = false;
            return unifyForResolution;
        }
        // if we made it here, then we've looped through every operand of the query and figured out how to deal with it! supposedly...
        return unifyForResolution;
    }
    // end unificationCheck

    // if we're calling this function, then we've been given the go ahead to take the literal and replace any variables
// contained in the tempUnifiedVars hash with their mapped constants
    public Literal unifyPredicate(Literal toUnify) {
        // because we don't know if these substitutions are going to hold for the whole sentence, create a
        // temporary literal to preserve the original
        Literal literal = new Literal(toUnify.literalType, toUnify.literalString);
        if (toUnify.not) literal.not = true;
        String replacement = new String();
        replacement = replacement.concat(toUnify.literalString.substring(0, 1 + toUnify.literalString.indexOf("(")));

        // if this has been called on something that's not a predicate, we can't unify so just return a copy of the thing passed in
        if (literal.literalType == Literal.type.PREDICATE) {

            ArrayList<ArrayList<String>> operandLists = literal.processPredicate();
            ArrayList<String> vars = new ArrayList<>();
            ArrayList<String> constants = new ArrayList<>();
            ArrayList<String> operands = new ArrayList<>();

            if (!operandLists.get(0).isEmpty() && !Literal.isCapital(operandLists.get(0).get(0).substring(0, 1))) {
                vars.addAll(operandLists.get(0));
            }
            if (!operandLists.get(1).isEmpty() && Literal.isCapital(operandLists.get(1).get(0).substring(0, 1))) {
                constants.addAll(operandLists.get(1));
            }
            operands.addAll(operandLists.get(2));

            for (int i = 0; i < operands.size(); i++) {
                String operand = operands.get(i);

                if (Literal.isCapital(operand.substring(0, 1))) {

                    // if we found a constant, we just want to add that right into our replacement string
                    replacement = replacement.concat(operand);
                }
                else  {

                    // see if that var has been unified
                    if (tempUnifiedVars.containsKey(operand)) {
                        //replace it in the literal
                        replacement = replacement.concat(tempUnifiedVars.get(operand));
                    }
                    else {
                        // we haven't unified this variable, so just put it back in as is
                        replacement = replacement.concat(operand);
                    }
                }

                // check if we're done, if not add a comma before the next operand
                if (i + 1 < operands.size()) replacement = replacement.concat(",");
            }

            // we made it through all the operands, lets close off our replacement string
            replacement = replacement.concat(")");

            literal.literalString = replacement;
        }

        return literal;
    }
    //end unifyPredicate


    public ArrayList<Literal> unifySentence(ArrayList<Literal> disjunctionOfLiterals) {
        ArrayList<Literal> toReturn = new ArrayList<>();

        for (int i = 0; i < disjunctionOfLiterals.size(); i++) {
            // get the first literal
            Literal currLiteral = disjunctionOfLiterals.get(i);
            // check to see if it's a predicate
            if (currLiteral.literalType == Literal.type.PREDICATE) {
                String replacement = new String();
                // get the front part of the predicate, we'll add the operands as appropriate below
                if (currLiteral.not) {
                    replacement = "~";
                    replacement = replacement.concat(currLiteral.literalString.substring(0, 1 + currLiteral.literalString.indexOf("(")));
                }
                else replacement = currLiteral.literalString.substring(0, 1 + currLiteral.literalString.indexOf("("));
                // find out what it operates on
                ArrayList<ArrayList<String>> operandLists = currLiteral.processPredicate();
                ArrayList<String> operands = operandLists.get(2);
                // loop through the operands of this predicate
                for (int j = 0; j < operands.size(); j++) {
                    // check the first letter of each operand to see if it's a variable
                    if (!Literal.isCapital(operands.get(j).substring(0, 1))) {
                        // if it's a variable, check to see if it has been unified
                        if (tempUnifiedVars.containsKey(operands.get(j))) {
                            // now we need to replace the variable with the constant it's been unified to
                            replacement = replacement.concat(tempUnifiedVars.get(operands.get(j)));
                        }
                        else {
                            // the variable hasn't been unified, so we need to just keep it as it is
                            replacement = replacement.concat(operands.get(j));
                        }
                    }
                    else {
                        // the current operand is not a variable, just add it directly to our new string
                        replacement = replacement.concat(operands.get(j));
                    }
                    if (j + 1 < operands.size()) replacement = replacement.concat(",");

                }
                // now we've looped through all the operands of this predicate,
                // so let's close the string and add our new literal to the list to return
                replacement = replacement.concat(")");
                Literal unifiedLiteral = new Literal(currLiteral.literalType, replacement);
                toReturn.add(unifiedLiteral);

            }
            // if it's not a predicate, just add it straight to the list to return
            else toReturn.add(currLiteral);
        }

        // now let's just really quickly check if we have anything with variables that can be reduced into one
        toReturn = reduceOrStandardize(toReturn);
        return toReturn;
    }
    // end unifySentence


    public ArrayList<Literal> reduceOrStandardize(ArrayList<Literal> disjunctionOfLiterals) {
        ArrayList<Literal> toReturn = new ArrayList<>();

        String reductionString = new String();
        String standardization = new String();

        int p = 1;
        if(disjunctionOfLiterals.size()>1) {
            for (int i = 0; i < disjunctionOfLiterals.size(); i++) {
                Literal currLiteral = disjunctionOfLiterals.get(i);
                ArrayList<ArrayList<String>> currLists = currLiteral.processPredicate();
                if(i+p >= disjunctionOfLiterals.size()) break;
                Literal next = disjunctionOfLiterals.get(i+p);
                ArrayList<ArrayList<String>> nextlists = next.processPredicate();
                String pred = currLiteral.literalString.substring(0, currLiteral.literalString.indexOf("("));

//                for (int n = i + p; n < disjunctionOfLiterals.size(); n++) {
//                    Literal next = disjunctionOfLiterals.get(n);
//                    ArrayList<ArrayList<String>> nextlists = next.processPredicate();
                    if (next.literalType == Literal.type.PREDICATE && pred.equals(next.literalString.substring(0, next.literalString.indexOf("(")))) {
                        reductionString = reductionString.concat(next.literalString.substring(0, 1 + currLiteral.literalString.indexOf("(")));

                        for (int l = 0; l < nextlists.get(2).size(); l++) {
                            if (!Literal.isCapital(nextlists.get(2).get(l).substring(0, 1)) && !currLists.get(2).get(l).equals(nextlists.get(2).get(l))) {
                                // they are variables in the same space but are not equal
                                // add/keep the variable from the current Literal
                                reductionString = reductionString.concat(currLists.get(2).get(l));
                                standardization = standardization.concat(currLists.get(2).get(l));
                            }
                            else if(!Literal.isCapital(nextlists.get(2).get(l).substring(0, 1)) && currLists.get(2).get(l).equals(nextlists.get(2).get(l))){
                                // they are variables and are equal so let's make one not equal
                                standardization = standardization.concat(next.literalString.substring(0, 1 + currLiteral.literalString.indexOf("(")));
                                standardization = standardization.concat("poo");
                            }
                            else reductionString = reductionString.concat(nextlists.get(2).get(l));

                            if (l + 1 < nextlists.get(2).size()) reductionString = reductionString.concat(",");
                        }
                        reductionString = reductionString.concat(")");
                        standardization = standardization.concat(")");
                    }
                    toReturn.add(currLiteral);
                    if (!reductionString.equals(currLiteral.literalString)) {
                        toReturn.add(next);
                        reductionString = "";
                        i++;
                    }
                    else {
                        Literal stand = new Literal(next.literalType, standardization);
                        if(next.not) stand.not = true;
                        toReturn.add(stand);
                        standardization = "";
                    }
//                }
                p++;
            }
        }
        else toReturn.addAll(disjunctionOfLiterals);


        return toReturn;
    }




//        // the query can be a predicate with multiple inputs
//        // let's look through them by starting at the open paren and looking to each comma
//        int qstart = 1 + query.literalString.indexOf("(");
//        int qend;
//        if (query.literalString.contains(",")){
//            qend = query.literalString.indexOf(",", qstart);
//        }
//        else qend = query.literalString.indexOf(")");

        // loop through query to find out what we're looking to match
//        while(!done) {

//            String toMatch = query.literalString.substring(qstart, qend);


        // let's see if the predicate we found in the kb has variables that can be unified
//                for (int var = 0; var < availableVars.size(); var++) {
//                    // alright! there are variables with the potential to be unified. let's look at one
//                    String variable = availableVars.get(var);
//                    // check to see if it's already been unified
//
//                    if (tempUnifiedVars.containsKey(variable) || permUnifiedVars.containsKey(variable)) {
//                        // this variable is already unified
//
//                    }
//                    else {
//                        // this variable is available to unify
//                        tempUnifiedVars.put(operand, toMatch);
//                    }
//
//
//                }
//            }


//        }


        public ArrayList<Sentence> unify (ArrayList < Sentence > kbSentences) {
            ArrayList<Sentence> unifiedSentences = new ArrayList<>();
            ArrayList<Literal> unifiedLiterals = new ArrayList<>();

            permUnifiedVars.putAll(tempUnifiedVars);
            tempUnifiedVars.clear();


            for (int i = 0; i < kbSentences.size(); i++) {

            }


            //make the substitution throughout it's list of literals


            return unifiedSentences;
        }


    public void resetHashTables() {
        tempUnifiedVars.clear();
        permUnifiedVars.clear();
    }


}


//    private boolean unificationCheck(Sentence sentence, Literal query) {
//        boolean unificationRequired = false;
//
//        // the query can be a predicate with multiple inputs
//        int qstart = 1 + query.literalString.indexOf("(");
//        int end = query.literalString.indexOf(",", qstart);
//
//        // loop through query to find out what we're looking to match
//        for (int q = 0; q < query.literalString.length(); q++) {
//
//            // toMatch is a thing the query predicate is operating on and should always be a constant
//            String toMatch = query.literalString.substring(qstart, end);
//
//            // update qstart for next loop
//            int prevstart = qstart;
//            qstart = end + 1;
//
//            // update end for next loop
//            if (query.literalString.substring(end + 1, query.literalString.length()).contains(",")) {
//                // check to see if there's another ',' present or if we're on our last operand
//                end = query.literalString.indexOf(",", prevstart);
//            }
//            else {
//                // no commas left means we're now on the last constant the query predicate operates on
//                end = query.literalString.indexOf(")");
//            }
//
//
//            // then search through each literal from the sentence
//            for (int index = 0; index < sentence.djncOfLiterals.size(); index++) {
//                Literal literal = sentence.djncOfLiterals.get(index);
//
//                if (literal.literalType.equals(Literal.type.PREDICATE)) {
//                    int lstart = literal.literalString.indexOf("(");
//                    int lend = literal.literalString.indexOf(",", lstart);
//                    String operandStg = literal.literalString.substring(lstart, lend);
//                }
//                else if (literal.literalType.equals(Literal.type.CONSTANT)) {
////                    if(literal.literalString.equals(toMatch)){
//                    //one of the literals from the sentence is a direct match
//                    // but I don't think we care... a constant in the knowledge base would only help us resolve
//                    // constants that were OR'ed into other sentences
//                    // our queries will always be predicates so finding a constant that matches the query's operand
//                    // I guess doesn't necessarily help us
////                    }
//
//                    // we don't look at constants because they do not require unification and because of the above comments
//                    unificationRequired = false;
//
//                }
//                else if (literal.literalType.equals(Literal.type.VARIABLE)) {
//                    System.out.println("ISSUE: literal from a sentence was labeled as a variable.");
//                }
//
//            }
//        }
//
//        return true;
//    }


