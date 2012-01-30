package gnb.gnbc;

import gnb.gnbc.exception.LexicalAnalyzerException;
import java.util.ArrayList;
import java.util.Arrays;

public class LexicalAnalyzer {
    public static ArrayList< Lexeme > parse(String src) throws LexicalAnalyzerException {
    
        LexicalAnalyzer instance = new LexicalAnalyzer();
        
        return instance.getLexemes(src);
    } 
    
    private enum MachineState {
        START, NOT, NOT_EQUAL, LESS, LESS_OR_EQUAL, MORE, MORE_OR_EQUAL, ASSIGNMENT, ASSIGNMENT_FINAL, WORD, NUMBER, ACCEPTED, REJECTED, EQUAL;
    }
    
    private interface MachineAction {
       public void runAction(LexicalAnalyzer analyzer);
    }
 
    
    public MachineState[][] jumps;
    public MachineAction[][] actions;
    public MachineState currentState;
    public ArrayList< Lexeme > currentLexemeChain;
    public int currentIndex;
    public int currentLine,currentColumn;
    public char[] currentChain;
    public String buffer;
    public boolean[][] differentLexemes;
    
    public LexicalAnalyzer() {
        initJumps();
    }
    
    public char getCurrentChar() {
        if (currentIndex < currentChain.length) {
            return currentChain[currentIndex];
        }

        return 0;
    }
    
    public void addLexemeToChain(Lexeme l) {
        l.startsAtLine = currentLine;
        l.startsAtColumn = currentColumn;
        
        currentLexemeChain.add(l);
    }
    
    private void executeMachineStep() throws LexicalAnalyzerException {
        MachineState nextState = jumps[currentState.ordinal()][getCurrentChar()];
        MachineAction action = actions[currentState.ordinal()][getCurrentChar()];
        
        if (nextState.equals(MachineState.REJECTED)) {
            throw new LexicalAnalyzerException("error at line " + new Integer(currentLine).toString() + ", column " + new Integer(currentColumn).toString());
        }
        
        if (!differentLexemes[currentState.ordinal()][nextState.ordinal()] && !nextState.equals(MachineState.START)) {
            buffer = buffer.concat(String.valueOf(getCurrentChar()));
        }
        
        if (differentLexemes[currentState.ordinal()][nextState.ordinal()] && !currentState.equals(MachineState.START)){
            
            Lexeme currentLexeme = Lexeme.getLexemeByString(buffer);
            
            if (currentState.equals(MachineState.NUMBER)) {
                currentLexeme = new Lexeme(Lexeme.LexemeType.INT, buffer);
            }
            
            addLexemeToChain(currentLexeme);
            buffer = "";
            
        }
        
        if (differentLexemes[currentState.ordinal()][nextState.ordinal()]) {
            buffer = String.valueOf(getCurrentChar());
        }
        
        if (nextState.equals(MachineState.START)) {
            Lexeme simple = Lexeme.getSimpleLexeme(getCurrentChar());
            
            if (simple != null) {
                addLexemeToChain(simple);
            }
        }
        
        if (action != null) {
            action.runAction(this);
        }
        
        currentIndex++;
        currentColumn++;
        currentState = nextState;
    }
        
    
    private void initJumps() {        
        MachineState[] states = MachineState.values();
        
        jumps = new MachineState[states.length][];
        actions = new MachineAction[states.length][];    
        differentLexemes = new boolean[states.length][states.length];

        for (int i = 0; i < states.length; i++) {
            jumps[i] = new MachineState[256];
            actions[i] = new MachineAction[256];
            
            Arrays.fill(jumps[i], MachineState.REJECTED);
            Arrays.fill(actions[i], null);
            Arrays.fill(differentLexemes[i], true);
            
            differentLexemes[i][i] = false;
        }
        
        //almost for every whitespace we should do nothing just back to start-state
        for (int i = 0; i < states.length; i++) {
            if (states[i] == MachineState.ASSIGNMENT) {
                continue;
            }
            
            jumps[i]['\t'] = MachineState.START;
            jumps[i][' '] = MachineState.START;
            jumps[i]['\n'] = MachineState.START;
            actions[i]['\n'] = new MachineAction() {

                @Override
                public void runAction(LexicalAnalyzer analyzer) {
                    analyzer.currentLine++;
                    analyzer.currentColumn=0;
                }
            } ;
        }
        
        //simple one-characters lexems       
        MachineState statesCouldJumpToSimpleStatesFrom[] = { MachineState.LESS, 
                                                             MachineState.NOT, 
                                                             MachineState.NUMBER, 
                                                             MachineState.START, 
                                                             MachineState.WORD, 
                                                             MachineState.ASSIGNMENT_FINAL,
                                                             MachineState.LESS_OR_EQUAL,
                                                             MachineState.MORE_OR_EQUAL,
                                                           };
        

        for (int i = 0; i < statesCouldJumpToSimpleStatesFrom.length; i++) {
            int hashCode = statesCouldJumpToSimpleStatesFrom[i].ordinal();
            
            for (int j = 0; j < Lexeme.validSimpleLexems.length; j++) {
                jumps[hashCode][Lexeme.validSimpleLexems[j]] = MachineState.START;
            }
        }
        
        //alpha
        char[] alphaChars = new char[26*2];
        for (int i = 0; i < 26; i++) {
            alphaChars[2*i] =  (char)((int)'a' + i);
            alphaChars[2*i+1] =  (char)((int)'A' + i);
        }
        
        for (int i = 0; i < alphaChars.length; i++) {
            char c = alphaChars[i];
            jumps[MachineState.START.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.WORD.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.LESS.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.MORE.ordinal()][c] = MachineState.WORD;   
            jumps[MachineState.LESS_OR_EQUAL.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.MORE_OR_EQUAL.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.NOT_EQUAL.ordinal()][c] = MachineState.WORD;   
            jumps[MachineState.ASSIGNMENT_FINAL.ordinal()][c] = MachineState.WORD; 
            jumps[MachineState.EQUAL.ordinal()][c] = MachineState.WORD;
        }
        
        //digits
        for (int i = 0; i < 10; i++) {
            char c = (char)((int)'0' + i);
            jumps[MachineState.START.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.WORD.ordinal()][c] = MachineState.WORD;
            jumps[MachineState.LESS.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.MORE.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.NUMBER.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.LESS_OR_EQUAL.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.MORE_OR_EQUAL.ordinal()][c] =MachineState.NUMBER;
            jumps[MachineState.NOT_EQUAL.ordinal()][c] = MachineState.NUMBER;
            jumps[MachineState.ASSIGNMENT_FINAL.ordinal()][c] = MachineState.NUMBER; 
            jumps[MachineState.EQUAL.ordinal()][c] =MachineState.NUMBER;
        }
        
        //NOT
        jumps[MachineState.START.ordinal()]['!'] = MachineState.NOT;
        jumps[MachineState.NUMBER.ordinal()]['!'] = MachineState.NOT;
        jumps[MachineState.WORD.ordinal()]['!'] = MachineState.NOT;
        jumps[MachineState.NOT.ordinal()]['='] = MachineState.NOT_EQUAL;
        
        differentLexemes[MachineState.NOT.ordinal()][MachineState.NOT_EQUAL.ordinal()] = false;
        
        //LESS
        jumps[MachineState.START.ordinal()]['<'] = MachineState.LESS;
        jumps[MachineState.NUMBER.ordinal()]['<'] = MachineState.LESS;
        jumps[MachineState.WORD.ordinal()]['<'] = MachineState.LESS;
        jumps[MachineState.LESS.ordinal()]['='] = MachineState.LESS_OR_EQUAL;
        
        differentLexemes[MachineState.LESS.ordinal()][MachineState.LESS_OR_EQUAL.ordinal()] = false;
        
        //MORE
        jumps[MachineState.START.ordinal()]['>'] = MachineState.MORE;
        jumps[MachineState.NUMBER.ordinal()]['>'] = MachineState.MORE;
        jumps[MachineState.WORD.ordinal()]['>'] = MachineState.MORE;
        jumps[MachineState.MORE.ordinal()]['='] = MachineState.MORE_OR_EQUAL;
        
        differentLexemes[MachineState.MORE.ordinal()][MachineState.MORE_OR_EQUAL.ordinal()] = false;
        
        //ASSIGNMENT
        jumps[MachineState.START.ordinal()][':'] = MachineState.ASSIGNMENT;
        jumps[MachineState.WORD.ordinal()][':'] = MachineState.ASSIGNMENT;
        jumps[MachineState.ASSIGNMENT.ordinal()]['='] = MachineState.ASSIGNMENT_FINAL;
        
        differentLexemes[MachineState.ASSIGNMENT.ordinal()][MachineState.ASSIGNMENT_FINAL.ordinal()] = false;
        
        //=
        jumps[MachineState.START.ordinal()]['='] = MachineState.EQUAL;
        jumps[MachineState.WORD.ordinal()]['='] = MachineState.EQUAL;
        jumps[MachineState.NUMBER.ordinal()]['='] = MachineState.EQUAL;
        
        //accept
        jumps[MachineState.START.ordinal()][0] = MachineState.ACCEPTED;
        
    }
    
    private void initMachine(String src) {
        currentChain = src.toCharArray();
        currentColumn = 1; 
        currentLine = 1;
        currentIndex = 0;
        currentState = MachineState.START;
        currentLexemeChain = new ArrayList<Lexeme>();
        buffer = "";
    }
    
    public ArrayList< Lexeme > getLexemes(String src) throws LexicalAnalyzerException {
        initMachine(src);
        
        while (currentIndex <= currentChain.length) {
            executeMachineStep();
        }
        
        currentLexemeChain.add(new Lexeme(Lexeme.LexemeType.EOF));
        
        return currentLexemeChain;
    }
}
