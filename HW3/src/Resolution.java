import java.util.ArrayList;
import java.util.Hashtable;

public class Resolution {


    public static Hashtable<String, ArrayList<Integer>> constantsHash = new Hashtable<>();
    public static Hashtable<String, ArrayList<Integer>> predicatesHash = new Hashtable<>();
    public static Hashtable<String, ArrayList<Integer>> variablesHash = new Hashtable<>();
    public static Hashtable<String, ArrayList<Sentence>> loopHash = new Hashtable<>();
    public static Hashtable<String, ArrayList<Sentence>> resolvedHash = new Hashtable<>();


    InputInfo inputInfo;

    public Resolution(InputInfo inputInfo) {
        this.inputInfo = inputInfo;

    }

    public OutputInfo resolve() {
        OutputInfo outputInfo = new OutputInfo(this.inputInfo.numQueries);

        String[] stringArray = new String[this.inputInfo.numQueries];


        // for every query
        for (int index = 0; index < inputInfo.numQueries; index++) {
            // get the next query
            Literal query = inputInfo.queries.get(index);


            // we now have a list of sentences from the kb that relate to our query, attempt to resolve by refutation
            String result = refute(query);
            stringArray[index] = result;


            //TODO this is where I need to implement forward chaining?

        }
        outputInfo.results = stringArray;
        return outputInfo;
    }
    // end resolve


    private String refute(Literal queryToResolve) {
        Unification unification = new Unification();
        int backtrack = 0;
        ArrayList<Sentence> allSentences = new ArrayList<>();
        // if we go through all related sentences and this is unchanged, then we know those sentences cannot resolve the query
        boolean nothingResolved = true;
        // optionsExhausted indicates whether we've searched through all the sentences
        boolean optionsExhausted = false;
        // no special sentence selection occurred, ask normally
        boolean normalAsk = false;
        boolean loopDetected = false;
        // unified will let us know if we need to unify variables in our whole kb
        boolean unified = false;
        boolean unifiedResolved = false;
        boolean unifiable = false;
        //temporary holder for selection of which sentence in the kb to use
        ArrayList<Literal> selected = new ArrayList<>();
        ArrayList<Literal> toAdd = new ArrayList<>();
        ArrayList<Literal> prevResolved = new ArrayList<>();
        ArrayList<Literal> loopTracking = new ArrayList<>();
        ArrayList<Literal> unifiedLiterals = new ArrayList<>();
        ArrayList<Sentence> newSentences = new ArrayList<>();
        // fill this with results of our refutation/resolution. This must be empty for us to return TRUE
        // this i believe is the disjunction of literals ie technically each entry in this list would be ORed
        // this also means we don't separate a constant if it's passed into a predicate though leave as Cat(Joe)
        ArrayList<Literal> toResolve = new ArrayList<>();

        // 'not' the query to refute
        queryToResolve.not = !queryToResolve.not;

        // add to our list of things we need to resolve.
        // This list must be empty for us to declare the kb entails the query
        toResolve.add(queryToResolve);
        String result = "FALSE";

        //grab the first literal to resolve
        Literal resolve = toResolve.get(toResolve.size() - 1);

        // identify sentences that are related to the literal we must resolve
        ArrayList<Sentence> kbSentences = ask(resolve);
//        for (int v = kbSentences.size()-1; v >= 0; v--){
//            newSentences.add(kbSentences.get(v));
//            allSentences.add(kbSentences.get(v));
//        }

        newSentences.addAll(kbSentences);
        allSentences.addAll(kbSentences);


        for (int kbIndex = kbSentences.size() - 1; kbIndex >= 0; kbIndex--) {
            // for each sentence in the kb we've identified is related
            Sentence known;

            known = kbSentences.get(kbIndex);


            while (!toResolve.isEmpty() && !optionsExhausted) {

                resolve = toResolve.get(toResolve.size() - 1);
                if (normalAsk) {
                    newSentences = ask(resolve);
                    backtrack = newSentences.size();
                    allSentences.addAll(newSentences);
                    known = newSentences.get(newSentences.size() - 1);
                    normalAsk = false;
                }

                //loop through the sentence's literals
                for (int sIndex = 0; sIndex < known.djncOfLiterals.size(); sIndex++) {
                    Literal literal = known.djncOfLiterals.get(sIndex);

                    // first check if we just blatantly have a direct match b/c that's easy
                    if (resolve.literalString.equals(literal.literalString) && resolve.not != literal.not) {
                        // that was easy, it was just exactly in the sentence we pulled from the kb in the form we needed it in

                        prevResolved.add(toResolve.get(0));
                        // this current sentence from the kb that we're looking at is the one we want
                        int currSize = selected.size();
                        selected.addAll(known.djncOfLiterals);
                        // remove the literal that matches our current thing to resolve
                        selected.remove(sIndex + currSize);
                        nothingResolved = false;
                        break;
                    }
                    // check if our thing to resolve and the current literal from the sentence are predicates
                    else if (resolve.literalType == Literal.type.PREDICATE && literal.literalType == Literal.type.PREDICATE) {
                        // let's look at just the premise of the predicate, not what it operates on
                        int lparenthesis = literal.literalString.indexOf("(");
                        int rparenthesis = resolve.literalString.indexOf("(");
                        String lpremise = literal.literalString.substring(0, lparenthesis);
                        String rpremise = resolve.literalString.substring(0, rparenthesis);
                        // lets see if they're the same predicate and are opposites (so they can resolve)
                        if (lpremise.equals(rpremise) && resolve.not != literal.not) {
                            // now we know we're looking at the same predicates that have the potential to resolve
                            // now we can see if the literal operates on a variable and can be unified!
                            unifiable = unification.unificationCheck(literal, resolve);

                            if (unifiable) {
                                // let replace the variables with their unified constants and see if we can resolve
                                Literal unifiedPredicate = unification.unifyPredicate(literal);
                                Literal unifiedQuery = unification.unifyPredicate(resolve);
                                boolean varUnified = unification.unificationCheck(unifiedPredicate, unifiedQuery);
                                if ((unifiedPredicate.literalString.equals(unifiedQuery.literalString) | varUnified) && unifiedPredicate.not != unifiedQuery.not) {
                                    // excellent, we've unified such that the current literal we're looking at can be resolved
                                    unifiedLiterals.add(unifiedPredicate);

                                    if (!resolve.literalString.equals(unifiedQuery.literalString)) {
                                        unifiedResolved = true;
                                    }
                                    unified = true;
                                    prevResolved.add(resolve);
                                    loopTracking.add(resolve);

                                    // this current sentence from the kb that we're looking at is the one we want
                                    int currSize = selected.size();
                                    selected.addAll(known.djncOfLiterals);

                                    // remove the literal that matches our current thing to resolve
                                    selected.remove(sIndex + currSize);
                                    nothingResolved = false;
                                    break;

                                }
                                System.out.println("unified");
                            }
                        }
                    }
                }
                // we've now either resolved one literal or looked at all of them w/o being able to resolve
                if (!nothingResolved) {
                    // if we're here, we've found something that resolves the current literal
                    toResolve.remove(toResolve.size() - 1);

                    // make sure we were able to resolve something from the last sentence and check that the leftovers
                    // are not all things we've resolved before
                    if (unified) {
                        // if we unified something, unify the rest of the sentence
                        selected = unification.unifySentence(selected);
                    }
                    if (unifiedResolved) {
                        ArrayList<Literal> unifiedResolves = unification.unifySentence(toResolve);
                        toResolve.clear();
                        toResolve.addAll(unifiedResolves);

                    }

                    //check for loop
                    int loop = 0;

                    boolean first = false;
                    boolean second = false;
                    for (int re = 0; re < toResolve.size(); re++) {
                        for (int pr = 0; pr < loopTracking.size(); pr++) {
                            if (loopTracking.get(pr).literalString.equals(toResolve.get(re).literalString) && loopTracking.get(pr).not == toResolve.get(re).not) {
                                // we've already resolved everything that we're about to add to toResolve
                                loop++;
                            }
                        }
                    }
                    //true if every element in resolve was found in loopTracking
                    if (!toResolve.isEmpty() && loop == toResolve.size()) first = true;
                    loop = 0;
                    for (int sl = 0; sl < selected.size(); sl++) {
                        for (int pr = 0; pr < loopTracking.size(); pr++) {
                            if (loopTracking.get(pr).literalString.equals(selected.get(sl).literalString) && loopTracking.get(pr).not == selected.get(sl).not) {
                                // we've already resolved everything that we're about to add to toResolve
                                loop++;
                            }
                        }
                    }
                    //true if every element in selected was found in loopTracking
                    if (!selected.isEmpty() && loop == selected.size()) second = true;

                    // if both are true, then we have nothing new to explore
                    if (first && second) loopDetected = true;

                    if (loopDetected) {
                        backtrack--;
                        newSentences.remove(newSentences.size() - 1);
                        allSentences.remove(allSentences.size() - 1);
                        if (newSentences.isEmpty()) {
                            newSentences.clear();
                            allSentences.remove(allSentences.size() - 1);
                            newSentences.add(allSentences.get(allSentences.size() - 1));
                            if (backtrack != 0)
                                System.out.println("ISSUE: reached newSentences is empty w/o having backtracked completely");
                            backtrack = newSentences.size();
                            toResolve.add(prevResolved.get(prevResolved.size() - 1));
                            prevResolved.remove(prevResolved.size() - 1);
                        }
                        known = newSentences.get(newSentences.size() - 1);
                        unification.resetHashTables();
                        loopDetected = false;

                    }
                    else {


                        // if the resolution results in fewer things left to resolve,
                        // clear the toAdd list and place the new list of things to resolve
                        toAdd.clear();
                        toAdd.addAll(selected);
                        // check each entry of toAdd to see if it's already in toResolve
                        for (int i = 0; i < toAdd.size(); i++) {
                            // if the entry is already in toResolve, we don't want to add it again
                            for (int j = 0; j < toResolve.size(); j++) {
                                if (toResolve.get(j).literalString.equals(toAdd.get(i).literalString)) {
                                    if (toResolve.get(j).not == toAdd.get(i).not) {
                                        toAdd.remove(i);
                                    }
                                    else {
                                        // the strings are equal and they are opposites so they resolve!
                                        toAdd.remove(i);
                                        toResolve.remove(j);
                                    }
                                }
                                // do nothing if the strings don't match
                            }
                        }


                        // add the remaining literals from the sentence to things that need to be resolved
                        toResolve.addAll(toAdd);

                        if (toAdd.isEmpty()) {
                            // we resolved our thing and didn't have any left overs to add so we can just move on to our next thing we need to resolve
                            // and we can ask our kb normally about that next thing we're going to resolve
                            normalAsk = true;
                        }
                        else {
                            // get the next thing we'd need to look at
                            for (int f = 0; f < toAdd.size(); f++) {
                                newSentences = ask(toAdd.get(toAdd.size() - 1));
                                if (!newSentences.isEmpty()) break;
                            }

                            if (newSentences.isEmpty()) {
                                // we just looked through every literal in toAdd and nothing was in the kb for any of them
                                // use a different sentence to resolve our thing

                                if (backtrack == 0) {
                                    //there are no sentences left for the thing we're trying to resolve, so we need to go back to the last thing we were trying to resolve and try a different path
                                    // so reset resolve to the last thing we resolved and try the next sentence down for that
                                    toResolve.remove(resolve); // we can't resolve this, remove it
                                    toResolve.add(prevResolved.get(prevResolved.size() - 1)); // re add the last thing we resolved
                                    prevResolved.remove(prevResolved.size() - 1);

                                    allSentences.remove(allSentences.size() - 1); // get rid of the sentence that was used to resolve the last thing we resolved (it got us here)

                                    ArrayList<Sentence> temp = ask(toResolve.get(toResolve.size() - 1));
                                    backtrack = temp.size();
                                    if (allSentences.isEmpty()) break;
                                    else newSentences.add(allSentences.get(allSentences.size() - 1));
                                }
                                else {
                                    // last sentence got us here, get rid of it
                                    allSentences.remove(allSentences.size() - 1);
                                    backtrack--;
                                    // we didn't backtrack through everything of the last thing we were trying to resolve
                                    // so we need to get those sentences back into newSentences since we just over wrote them
                                    // but now we know the path we took was back and we need to backtrack

                                    for (int b = 0; b < backtrack; b++) {
                                        newSentences.add(allSentences.get(allSentences.size() - 1));
                                    }

                                    newSentences.add(allSentences.get(allSentences.size() - 1));
                                    backtrack = newSentences.size();

                                }
                            }
                            else if (allSentences.containsAll(newSentences)) {
                                // this means we've already resolved this literal,
                                // and that we need to use a different sentence than we already used
                                String key;
                                if (toAdd.get(toAdd.size() - 1).not) {
                                    key = "~";
                                    key = key.concat(toAdd.get(toAdd.size() - 1).literalString);
                                }
                                else key = toAdd.get(toAdd.size() - 1).literalString;
                                if (loopHash.containsKey(key)) {
                                    // get the sentences for this that haven't yet been used to resolve this literal
                                    ArrayList<Sentence> unused = loopHash.get(key);
                                    if (unused.isEmpty()) break;
                                    // assume we used the last one last time so remove it
                                    unused.remove(unused.size() - 1);
                                    // now replace it in the table
                                    loopHash.replace(key, unused);
                                    // now add these sentences to our list of things to explore
                                    allSentences.addAll(unused);
                                    //the number of times we can back track before we're run out of things to resolve this sentence
                                    backtrack = unused.size();
                                    newSentences.clear();
                                    newSentences.addAll(unused);
                                }
                                else {
                                    // get all the sentences that can be used for this thing we're trying to resolve
                                    ArrayList<Sentence> available = ask(toAdd.get(toAdd.size() - 1));
                                    // assume we used the last one to get here
                                    available.remove(available.size() - 1);
                                    // now lets add this to the loopHash table to indicate we've hit a loop with this literal
                                    loopHash.put(key, available);
                                    // now add the remaining sentences to be explored to allSentences
                                    newSentences.clear();
                                    newSentences.addAll(available);
                                    allSentences.addAll(newSentences);
                                    backtrack = newSentences.size();
                                }
                            }
                            else {
//                                if (!resolvedHash.containsKey(prevResolved.get(prevResolved.size() - 1).literalString)) {
//                                    resolvedHash.put(prevResolved.get(prevResolved.size() - 1).literalString, ask(prevResolved.get(prevResolved.size() - 1)));
//                                }
                                allSentences.addAll(newSentences);
                                backtrack = newSentences.size();
                            }
                            if (newSentences.isEmpty()) {


///////////////////////////////////////////
                                if (allSentences.isEmpty()) {
                                    optionsExhausted = true;
                                }
                                else {
                                    //there are no sentences left in newSentences that were related to this thing we were trying to resolve
                                    // so reset resolve to the last thing we resolved and try the next sentence down for that
//                                    toResolve.remove(resolve); // we can't resolve this, remove it
                                    // allSentences has all the sentences in it that were related to this literal, we need to remove them

                                    ArrayList<Sentence> temp = ask(resolve);

                                    for (int t = 0; t < temp.size(); t++) {
                                        allSentences.remove(allSentences.size() - 1);
                                        // this should never run beyond empty

                                    }


                                    if (!allSentences.isEmpty()) {


                                        // get rid of the sentence that was used to resolve the last thing we resolved (it got us here)
                                        allSentences.remove(allSentences.size() - 1);
                                        if (!allSentences.isEmpty()) {
                                            // now the last thing in allSentences is something we haven't looked at yet, so let's try it
                                            newSentences.add(allSentences.get(allSentences.size() - 1));
                                            known = newSentences.get(newSentences.size() - 1);
//                                            temp = ask(toResolve.get(toResolve.size() - 1));
                                            backtrack = newSentences.size() - backtrack;
                                            unification.resetHashTables();
                                        }
                                        else optionsExhausted = true;

                                    }
                                    // allSentences is empty or only had the last sentence in it that got us to the current bad spot
                                    else optionsExhausted = true;
                                }


                                ///////////////////////////

                            }
                            else {
                                known = newSentences.get(newSentences.size() - 1);
                                unification.resetHashTables();
                            }
                        }
                    }
                    // reset nothing resolved
                    nothingResolved = true;
                }
                else {
                    //we weren't able to resolve something on our last run

                    // check to see if we have any other sentences to explore
                    if (newSentences.size() > 1) {
                        newSentences.remove(newSentences.size() - 1);
                        allSentences.remove(allSentences.size() - 1);
                        backtrack--;
                        // the sentence we were just on didn't work so we removed it
                        // now lets try the next one
                        known = newSentences.get(newSentences.size() - 1);
                        unification.resetHashTables();
                    }
                    else {
                        newSentences.clear();
                        // we exhausted our supply of new sentences so the thing we were just trying to resolve is a no-go
                        // so we have to go back and re run with a different sentence

                        if (allSentences.isEmpty() | prevResolved.isEmpty()) {
                            optionsExhausted = true;
                        }
                        else {

                            // allSentences has all the sentences in it that were related to this literal, we need to remove them

//                            ArrayList<Sentence> temp = ask(resolve);

//                            for (int t = 0; t < temp.size(); t++) {
//                                allSentences.remove(allSentences.size() - 1);
//                                // this should never run beyond empty
//                            }

//                            if (!allSentences.isEmpty()) {

//                                ask(prevResolved.get(prevResolved.size() - 1));


                            //there are no sentences left in newSentences that were related to this thing we were trying to resolve
                            // so reset resolve to the last thing we resolved and try the next sentence down for that
                            toResolve.remove(resolve); // we can't resolve this, remove it

                            for(int h = 1; h <= prevResolved.size(); h++){
                                if (!resolvedHash.containsKey(prevResolved.get(prevResolved.size() - h).literalString)) {
                                    ArrayList<Sentence> temp = ask(prevResolved.get(prevResolved.size() - h));
                                    resolvedHash.put(prevResolved.get(prevResolved.size() - h).literalString, temp);
                                }
                            }

                            boolean exit = false;
                            int n = 0;
                            while (!exit) {
                                n++;
                                if (n >= prevResolved.size()) break;
                                if (resolvedHash.get(prevResolved.get(prevResolved.size() - n).literalString).size() > 1) {
                                    exit = true;
                                }
                            }
                            if (n >= prevResolved.size()) {
                                break;
                            }
                            ArrayList<Sentence> removedSentences = new ArrayList<>();
                            for (int p = 0; p < n-1; p++) {
                                for(int rm = 0; rm < resolvedHash.get(prevResolved.get(prevResolved.size() - 1).literalString).size(); rm++) {
                                    // get rid of the sentence that was used to resolve the last thing we resolved (it got us here)
                                    removedSentences.add(allSentences.get(allSentences.size()-1));
                                    allSentences.remove(allSentences.size() - 1);
                                }
                                prevResolved.remove(prevResolved.size() - 1);

                            }



                            // now the last sentence in allSentences was what was used to get us on our current path
                            // we know it's no good now, so go back one more sentence but first let's make sure the literal
                            // we just added from preResolved had more than one sentence to use, otherwise, we need to
                            // go one step further back in preResolved
//                            if (!prevResolved.isEmpty() && ask(prevResolved.get(prevResolved.size() - 1)).size() <= 1) { //
//                                // the last thing resolved only had one sentence related to it
//                                // remove that thing we resolved and remove the sentence related to it
//                                prevResolved.remove(prevResolved.size() - 1);
//                                allSentences.remove(allSentences.size() - 1);
//                            }
                            if (!prevResolved.isEmpty() && allSentences.size() > 1) {
//                                toResolve = cleanUpResolveList(removedSentences, toResolve);
                                toResolve.clear();
//                                toResolve.add(queryToResolve);
                                toResolve.add(prevResolved.get(prevResolved.size() - 1)); // re add the last thing we resolved

                                if (prevResolved.isEmpty() || resolvedHash.get(prevResolved.get(prevResolved.size() - 1).literalString).size() <= 1) break;

                                newSentences = resolvedHash.get(prevResolved.get(prevResolved.size() - 1).literalString);
                                newSentences.remove(newSentences.size() - 1);
                                resolvedHash.replace(toResolve.get(toResolve.size() - 1).literalString, newSentences);
                                // then make it no longer resolved (remove from preResolved)
                                prevResolved.remove(prevResolved.size() - 1);

                                // now the last thing in allSentences is something we haven't looked at yet, so let's try it
//                                newSentences.add(allSentences.get(allSentences.size() - 1));

                                known = newSentences.get(newSentences.size() - 1);
//                                    temp = ask(toResolve.get(toResolve.size() - 1));
                                backtrack = newSentences.size();
                                unification.resetHashTables();
                            }
                            else optionsExhausted = true;
//                            }
//                            // allSentences is empty or only had the last sentence in it that got us to the current bad spot
//                            else optionsExhausted = true;
                        }
                    }
                }
                selected.clear();
                if (toResolve.isEmpty()) {
                    result = "TRUE";
                    // no need to go to next sentence
                    break;
                }

            }

            // if we've made it here, that means we exhausted our options from the last sentence, let's reset for the next one
            optionsExhausted = false;

            // before moving onto the next sentence, reset the hash tables since the variables for each sentence are unique
            unification.resetHashTables();

        }

        return result;
    }
    // end refute


    private ArrayList<Sentence> ask(Literal queryLiteral) {
        ArrayList<Sentence> forResolution = new ArrayList<>();

        String query;
        // when asking, we want to find sentences that allow us to resolve,
        // therefore we want to find sentences that contain the opposite of our query F(x) for queryLiteral ~F(x)
        if (queryLiteral.not) {
            query = queryLiteral.literalString;
        }
        else {
            query = "~";
            query = query.concat(queryLiteral.literalString);
        }

        // we've grabbed the literal: check if it's a constant, predicate, or if it's something else, we're probably in trouble.
        if (queryLiteral.literalType == Literal.type.CONSTANT) {
            if (constantsHash.containsKey(query)) {
                // success! we've found a constant in our kb that is used in our query, get all the sentences that contain that constant
                ArrayList<Integer> indicesList = constantsHash.get(query);

                indicesList.forEach((entry) -> forResolution.add(this.inputInfo.knowledgeBase.get(entry)));

            }
        }
        else if (queryLiteral.literalType == Literal.type.PREDICATE) {
            String predKey = query.substring(0, query.indexOf("("));
            if (predicatesHash.containsKey(predKey)) {
                ArrayList<Integer> indicesList = predicatesHash.get(predKey);

                indicesList.forEach((entry) -> forResolution.add(this.inputInfo.knowledgeBase.get(entry)));

            }
        }
        else if (queryLiteral.literalType == Literal.type.VARIABLE) {
            System.out.println("ISSUE: query is apparently just a variable...");
        }
        else if (queryLiteral.literalType == Literal.type.UNITITIALIZED) {
            System.out.println("ERROR: query type returned as uninitialized");
        }

        // return the list of indexes of all the sentences in the KB that have so related part
        return forResolution;
    }
// end ask


//    private ArrayList<Literal> cleanUpResolveList(ArrayList<Sentence> removedSentences, ArrayList<Literal> resolveList){
//
//
//    }






    private void tell(Sentence newSentence) {
        this.inputInfo.knowledgeBase.add(newSentence);
    }
// end tell

    public static void updateHshTbles(int kbIndex, Literal literal) {
        String literalKey;
        if (literal.not) {
            literalKey = "~";
            literalKey = literalKey.concat(literal.literalString);
        }
        else literalKey = literal.literalString;

        switch (literal.literalType) {
            case CONSTANT:

                if (constantsHash.containsKey(literalKey)) {
                    constantsHash.get(literalKey).add(kbIndex);
                }
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(kbIndex);
                    constantsHash.put(literalKey, newList);
                }
                break;
            case PREDICATE:
                String predKey = literalKey.substring(0, literalKey.indexOf("("));
                if (predicatesHash.containsKey(predKey)) {
                    predicatesHash.get(predKey).add(kbIndex);
                }
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(kbIndex);
                    predicatesHash.put(predKey, newList);
                }

                // we've found a predicate, lets find all the variables it operates on
                // we're currently ignoring any constants it operates on
                ArrayList<ArrayList<String>> operands = literal.processPredicate();
                ArrayList<String> variables;
                if (!operands.get(0).isEmpty() && !Literal.isCapital(operands.get(0).get(0).substring(0, 1))) {
                    variables = operands.get(0);
                }
                else variables = operands.get(1);

                // now lets add all the variables that this predicate operates on to the variables hash table
                for (int var = 0; var < variables.size(); var++) {
                    if (variablesHash.containsKey(variables.get(var))) {
                        variablesHash.get(variables.get(var)).add(kbIndex);
                    }
                    else {
                        ArrayList<Integer> newList = new ArrayList<>();
                        newList.add(kbIndex);
                        variablesHash.put(variables.get(var), newList);
                    }
                }
                break;
            case VARIABLE:
                System.out.println("ISSUE: somehow got just a variable as a literal in a sentence.");


                break;
            case UNITITIALIZED:
                System.out.println("ISSUE: got uninitialized literal type. Should have at least been updated to VARIABLE");
                break;
        }
    }
    // end updateHashTables

}
