import java.util.ArrayList;

public class Sentence {


    public String wholeString;
    public boolean or;
    public ArrayList<Literal> djncOfLiterals = new ArrayList<>();


    public Sentence(boolean or, String sentence) {
        this.or = or;
        this.wholeString = sentence;

        this.djncOfLiterals = this.processSentence();
    }


    public ArrayList<Literal> processSentence(){
        ArrayList<Literal> literalList = new ArrayList<>();
        int startOfPart = 0;

        // pull the sentence apart. Each entry in the literalList is one part of the sentence between ORs
        for(int index = 0; index < this.wholeString.length(); index++){

            // grab a letter
            String letter = this.wholeString.substring(index, index+1);

            if (letter.equals("|")){
                // set end substring index-1 to current index because substring is exclusive and we don't want to include the '|' or the preceding space in our string
                String part = this.wholeString.substring(startOfPart, index-1);
                Literal literal = Literal.identifyLiteral(part);
                literalList.add(literal);
                // if we're not at the end of the string, we want to start looking at the next segment, ie two characters after '|' or one character after the space following '|'
                startOfPart = index+2;
            }
        }

        // if there were no '|' then start should be 0 and we'll grab everything
        // if there were '|' then start will be the character after the last '|' and we'll grab the rest of the characters remaining
        String part = this.wholeString.substring(startOfPart, this.wholeString.length());

        // find out what the part is and then add it to our list of literals
        Literal literal = Literal.identifyLiteral(part);
        literalList.add(literal);

        return literalList;
    }


    public void processKB(int kbIndex) {
        // this method does not update the Constants hash table if there is a constant in a Predicate

        // this pulls all the individual parts of the sentence and updates the hash tables using them
        this.djncOfLiterals.forEach((literalEntry) -> Resolution.updateHshTbles(kbIndex, literalEntry));
    }




}
