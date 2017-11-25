/* The following code was generated by JFlex 1.6.1 */

package lexer;
/**
* Imp Lexical analyser
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>lexer/LexicalAnalyzer.flex</tt>
 */
public class LexicalAnalyzer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\2\4\1\0\2\4\22\0\1\4\7\0\1\1\1\3\1\2"+
    "\1\21\1\0\1\20\1\0\1\22\12\6\1\16\1\15\1\34\1\17"+
    "\1\33\2\0\32\5\6\0\1\31\1\7\1\5\1\14\1\10\1\23"+
    "\1\11\1\25\1\12\2\5\1\26\1\36\1\13\1\30\1\40\1\5"+
    "\1\32\1\27\1\24\2\5\1\35\1\5\1\37\1\5\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uff95\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7"+
    "\5\6\1\10\1\1\1\11\1\12\1\13\1\14\5\6"+
    "\1\15\1\16\2\6\2\1\1\17\1\20\1\6\1\21"+
    "\2\6\1\22\1\6\1\23\1\24\3\6\1\25\1\26"+
    "\2\6\1\27\1\30\1\31\2\6\2\0\1\32\1\6"+
    "\1\33\1\6\1\34\1\6\1\35\2\6\1\36\5\6"+
    "\1\37\1\40\1\41\1\42\1\43\2\6\1\44\1\45"+
    "\1\46\1\47";

  private static int [] zzUnpackAction() {
    int [] result = new int[81];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\41\0\102\0\143\0\204\0\102\0\245\0\306"+
    "\0\347\0\u0108\0\u0129\0\u014a\0\u016b\0\u018c\0\102\0\u01ad"+
    "\0\102\0\102\0\102\0\102\0\u01ce\0\u01ef\0\u0210\0\u0231"+
    "\0\u0252\0\u0273\0\u0294\0\u02b5\0\u02d6\0\u02f7\0\u0318\0\102"+
    "\0\102\0\u0339\0\306\0\u035a\0\u037b\0\306\0\u039c\0\u03bd"+
    "\0\102\0\u03de\0\u03ff\0\u0420\0\306\0\306\0\u0441\0\u0462"+
    "\0\102\0\102\0\102\0\u0483\0\u04a4\0\u02f7\0\u0318\0\102"+
    "\0\u04c5\0\u04e6\0\u0507\0\306\0\u0528\0\306\0\u0549\0\u056a"+
    "\0\306\0\u058b\0\u05ac\0\u05cd\0\u05ee\0\u060f\0\306\0\306"+
    "\0\306\0\306\0\306\0\u0630\0\u0651\0\306\0\306\0\306"+
    "\0\306";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[81];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
    "\1\13\1\10\1\14\1\15\1\16\1\17\1\20\1\21"+
    "\1\22\1\23\1\24\1\25\1\26\3\10\1\27\1\30"+
    "\1\31\1\32\1\33\1\34\2\10\1\35\2\36\1\37"+
    "\36\36\43\0\1\40\41\0\1\41\41\0\1\7\41\0"+
    "\10\10\6\0\10\10\2\0\4\10\6\0\1\11\37\0"+
    "\3\10\1\42\4\10\6\0\10\10\2\0\2\10\1\43"+
    "\1\10\5\0\6\10\1\44\1\10\6\0\3\10\1\45"+
    "\4\10\2\0\4\10\5\0\10\10\6\0\1\46\7\10"+
    "\2\0\4\10\5\0\10\10\6\0\5\10\1\47\2\10"+
    "\2\0\4\10\5\0\10\10\6\0\5\10\1\50\2\10"+
    "\2\0\4\10\17\0\1\51\26\0\10\10\6\0\5\10"+
    "\1\52\1\10\1\53\2\0\4\10\5\0\10\10\6\0"+
    "\2\10\1\54\2\10\1\55\2\10\2\0\4\10\5\0"+
    "\10\10\6\0\7\10\1\56\2\0\4\10\5\0\6\10"+
    "\1\57\1\10\6\0\10\10\2\0\4\10\5\0\3\10"+
    "\1\60\4\10\6\0\10\10\2\0\4\10\17\0\1\61"+
    "\40\0\1\62\13\0\1\63\12\0\10\10\6\0\2\10"+
    "\1\64\5\10\2\0\4\10\5\0\10\10\6\0\7\10"+
    "\1\65\2\0\4\10\2\66\1\67\41\66\1\70\35\66"+
    "\5\0\4\10\1\71\3\10\6\0\10\10\2\0\4\10"+
    "\5\0\7\10\1\72\6\0\10\10\2\0\4\10\5\0"+
    "\10\10\6\0\4\10\1\73\3\10\2\0\4\10\5\0"+
    "\10\10\6\0\1\10\1\74\6\10\2\0\4\10\5\0"+
    "\6\10\1\75\1\10\6\0\10\10\2\0\4\10\5\0"+
    "\10\10\6\0\7\10\1\76\2\0\4\10\5\0\10\10"+
    "\6\0\5\10\1\77\2\10\2\0\4\10\5\0\3\10"+
    "\1\100\4\10\6\0\10\10\2\0\4\10\5\0\7\10"+
    "\1\101\6\0\10\10\2\0\4\10\5\0\10\10\6\0"+
    "\6\10\1\102\1\10\2\0\4\10\5\0\5\10\1\103"+
    "\2\10\6\0\10\10\2\0\4\10\5\0\5\10\1\104"+
    "\2\10\6\0\10\10\2\0\4\10\5\0\5\10\1\105"+
    "\2\10\6\0\10\10\2\0\4\10\5\0\5\10\1\106"+
    "\2\10\6\0\10\10\2\0\4\10\5\0\3\10\1\107"+
    "\4\10\6\0\10\10\2\0\4\10\5\0\3\10\1\110"+
    "\4\10\6\0\10\10\2\0\4\10\5\0\10\10\6\0"+
    "\10\10\2\0\1\10\1\111\2\10\5\0\6\10\1\112"+
    "\1\10\6\0\10\10\2\0\4\10\5\0\7\10\1\113"+
    "\6\0\10\10\2\0\4\10\5\0\10\10\6\0\3\10"+
    "\1\114\4\10\2\0\4\10\5\0\6\10\1\115\1\10"+
    "\6\0\10\10\2\0\4\10\5\0\6\10\1\116\1\10"+
    "\6\0\10\10\2\0\4\10\5\0\10\10\6\0\1\117"+
    "\7\10\2\0\4\10\5\0\3\10\1\120\4\10\6\0"+
    "\10\10\2\0\4\10\5\0\10\10\6\0\1\10\1\121"+
    "\6\10\2\0\4\10";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1650];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\2\1\1\11\10\1\1\11\1\1\4\11"+
    "\13\1\2\11\7\1\1\11\7\1\3\11\2\1\2\0"+
    "\1\11\31\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[81];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
    // A list that contains all the recognized symbols
    List<Symbol> symbols = new ArrayList<Symbol>();

    // A LinkedHashMap is used to preserve the order of recognized identifiers
    HashMap<Integer, Symbol> identifiers = new LinkedHashMap<Integer, Symbol>();

    void addSymbol(LexicalUnit unit) {
        /**
         * Adds a new symbol to the list of recognized symbols, with the current
         * line number and column number. If the symbol is a VARNAME token,
         * it is considered as an identifier and added to the identifiers hashmap,
         * except if the identifier has already been encountered.
         * This makes the assumption that the line number never decreases as the scanner runs.
         *
         * @param  unit   The recognized lexical unit
         */
        Symbol symbol = new Symbol(unit, yyline, yycolumn, yytext());
        symbols.add(symbol);
        if (symbol.getType() == LexicalUnit.VARNAME) {
            // Calling the Symbol.hashCode() explicitly because the Symbol.equals(Symbol symbol) 
            // method has not been implemented by default
            if (!identifiers.containsKey(symbol.hashCode())) identifiers.put(symbol.hashCode(), symbol);
        }
    }

    public boolean isAtEOF() {
      return this.zzAtEOF;
    }

    public List<Symbol> getTokens() {
        return symbols;
    }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public LexicalAnalyzer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 130) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
        // Print matched lexical units
    for (Symbol symbol : symbols) {
        System.out.println(symbol.toString());
    }

    // Print the content of the symbol table
    System.out.println("\nIdentifiers");
    for (Integer index : identifiers.keySet()) {
        Symbol identifier = identifiers.get(index);
        int line = identifier.getLine();
        String token = identifier.getValue().toString();
        System.out.println(String.format("%-15s", token) + " " + line);
    }

    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException, BadTerminalException, BadTerminalContextException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
          {     return 0;
 }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { throw new BadTerminalException("Bad terminal: '" + yytext() + "'");
            }
          case 40: break;
          case 2: 
            { addSymbol(LexicalUnit.LPAREN);
            }
          case 41: break;
          case 3: 
            { addSymbol(LexicalUnit.TIMES);
            }
          case 42: break;
          case 4: 
            { addSymbol(LexicalUnit.RPAREN);
            }
          case 43: break;
          case 5: 
            { /* ignore */
            }
          case 44: break;
          case 6: 
            { addSymbol(LexicalUnit.VARNAME);
            }
          case 45: break;
          case 7: 
            { addSymbol(LexicalUnit.NUMBER);
            }
          case 46: break;
          case 8: 
            { addSymbol(LexicalUnit.SEMICOLON);
            }
          case 47: break;
          case 9: 
            { addSymbol(LexicalUnit.EQ);
            }
          case 48: break;
          case 10: 
            { addSymbol(LexicalUnit.MINUS);
            }
          case 49: break;
          case 11: 
            { addSymbol(LexicalUnit.PLUS);
            }
          case 50: break;
          case 12: 
            { addSymbol(LexicalUnit.DIVIDE);
            }
          case 51: break;
          case 13: 
            { addSymbol(LexicalUnit.GT);
            }
          case 52: break;
          case 14: 
            { addSymbol(LexicalUnit.LT);
            }
          case 53: break;
          case 15: 
            { yybegin(COMMENT);
            }
          case 54: break;
          case 16: 
            { throw new BadTerminalContextException("'*)' occured without '(*'");
            }
          case 55: break;
          case 17: 
            { addSymbol(LexicalUnit.BY);
            }
          case 56: break;
          case 18: 
            { addSymbol(LexicalUnit.IF);
            }
          case 57: break;
          case 19: 
            { addSymbol(LexicalUnit.DO);
            }
          case 58: break;
          case 20: 
            { addSymbol(LexicalUnit.ASSIGN);
            }
          case 59: break;
          case 21: 
            { addSymbol(LexicalUnit.TO);
            }
          case 60: break;
          case 22: 
            { addSymbol(LexicalUnit.OR);
            }
          case 61: break;
          case 23: 
            { addSymbol(LexicalUnit.GEQ);
            }
          case 62: break;
          case 24: 
            { addSymbol(LexicalUnit.LEQ);
            }
          case 63: break;
          case 25: 
            { addSymbol(LexicalUnit.NEQ);
            }
          case 64: break;
          case 26: 
            { yybegin(YYINITIAL);
            }
          case 65: break;
          case 27: 
            { addSymbol(LexicalUnit.END);
            }
          case 66: break;
          case 28: 
            { addSymbol(LexicalUnit.NOT);
            }
          case 67: break;
          case 29: 
            { addSymbol(LexicalUnit.FOR);
            }
          case 68: break;
          case 30: 
            { addSymbol(LexicalUnit.AND);
            }
          case 69: break;
          case 31: 
            { addSymbol(LexicalUnit.ELSE);
            }
          case 70: break;
          case 32: 
            { addSymbol(LexicalUnit.DONE);
            }
          case 71: break;
          case 33: 
            { addSymbol(LexicalUnit.FROM);
            }
          case 72: break;
          case 34: 
            { addSymbol(LexicalUnit.THEN);
            }
          case 73: break;
          case 35: 
            { addSymbol(LexicalUnit.READ);
            }
          case 74: break;
          case 36: 
            { addSymbol(LexicalUnit.BEGIN);
            }
          case 75: break;
          case 37: 
            { addSymbol(LexicalUnit.ENDIF);
            }
          case 76: break;
          case 38: 
            { addSymbol(LexicalUnit.WHILE);
            }
          case 77: break;
          case 39: 
            { addSymbol(LexicalUnit.PRINT);
            }
          case 78: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
