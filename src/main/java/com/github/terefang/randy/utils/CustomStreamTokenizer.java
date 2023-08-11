package com.github.terefang.randy.utils;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * Original Code from github.com/apache/harmony/.../StreamTokenizer.java
 *
 * changes Copyright (c) 2021-2022. terefang@gmail.com
 *
 */

import lombok.SneakyThrows;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;

/**
 * Parses a stream into a set of defined tokens, one at a time. The different
 * types of tokens that can be found are numbers, identifiers, quoted strings,
 * and different comment styles. The class can be used for limited processing
 * of source code of programming languages like Java, although it is nowhere
 * near a full parser.
 */
public class CustomStreamTokenizer {
    /**
     * Contains a number if the current token is a number ({@code ttype} ==
     * {@code TOKEN_TYPE_NUMBER}).
     */
    public double numValue;

    /**
     * Contains a string if the current token is a word ({@code ttype} ==
     * {@code TOKEN_TYPE_WORD}).
     */
    public String stringValue;

    /**
     * Contains a cadinal if the current token is a number ({@code ttype} ==
     * {@code TOKEN_TYPE_CADINAL}).
     */
    public long cardinalValue;

    private byte[] byteValue;
    /**
     * The constant representing the end of the stream.
     */
    public static final int TOKEN_TYPE_EOF = -1;

    /**
     * The constant representing the end of the line.
     */
    public static final int TOKEN_TYPE_EOL = '\n';

    /**
     * The constant representing a number token.
     */
    public static final int TOKEN_TYPE_NUMBER = -2;

    /**
     * The constant representing a word token.
     */
    public static final int TOKEN_TYPE_WORD = -3;

    /**
     * The constant representing a cardinal/integer token.
     */
    public static final int TOKEN_TYPE_CARDINAL = -4;

    /**
     * The constant representing a cardinal/integer token.
     */
    public static final int TOKEN_TYPE_DATETIME = -5;

    /**
     * The constant representing a byte[] token.
     */
    public static final int TOKEN_TYPE_BYTES = -6;

    /**
     * Internal representation of unknown state.
     */
    private static final int TOKEN_TYPE_UNKNOWN = -7;

    /**
     * After calling {@code nextToken()}, {@code ttype} contains the type of
     * token that has been read. When a single character is read, its value
     * converted to an integer is stored in {@code ttype}. For a quoted string,
     * the value is the quoted character. Otherwise, its value is one of the
     * following:
     * <ul>
     * <li> {@code TOKEN_TYPE_WORD} - the token is a word.</li>
     * <li> {@code TOKEN_TYPE_NUMBER} - the token is a number.</li>
     * <li> {@code TOKEN_TYPE_CARDINAL} - the token is a cardinal.</li>
     * <li> {@code TOKEN_TYPE_EOL} - the end of line has been reached. Depends on
     * whether {@code eolIsSignificant} is {@code true}.</li>
     * <li> {@code TOKEN_TYPE_EOF} - the end of the stream has been reached.</li>
     * </ul>
     */
    public int ttype = TOKEN_TYPE_UNKNOWN;

    /**
     * Internal character meanings, 0 implies TOKEN_ORDINARY
     */
    private byte tokenTypes[] = new byte[256];

    private static final byte TOKEN_COMMENT = 1;

    private static final byte TOKEN_QUOTE = 2;

    private static final byte TOKEN_WHITE = 4;

    private static final byte TOKEN_WORD = 8;

    private static final byte TOKEN_DIGIT = 16;

    private static final byte TOKEN_BYTES = 32;

    private int lineNumber = 1;

    private boolean forceLowercase;

    private boolean isEOLSignificant;

    private boolean slashStarComments;

    private boolean slashSlashComments;

    private boolean tripleQuotes;

    private boolean hexLiterals;

    private boolean byteLiterals;

    private boolean dateTimeLiterals;

    private boolean autoUnicodeMode;

    private boolean pushBackToken;

    private boolean lastCr;

    private Reader inReader;

    private int peekChar = -2;

    /**
     * Private constructor to initialize the default values according to the
     * specification.
     */
    private CustomStreamTokenizer() {
        /*
         * Initialize the default state per specification. All byte values 'A'
         * through 'Z', 'a' through 'z', and '\u00A0' through '\u00FF' are
         * considered to be alphabetic.
         */
        wordChars('A', 'Z');
        wordChars('a', 'z');
        wordChars(160, 255);
        /**
         * All byte values '\u0000' through '\u0020' are considered to be white
         * space.
         */
        whitespaceChars(0, 32);
        /**
         * '/' is a comment character. Single quote '\'' and double quote '"'
         * are string quote characters.
         */
        commentChar('/');
        quoteChar('"');
        quoteChar('\'');
        /**
         * Numbers are parsed.
         */
        parseNumbers();
        /**
         * Ends of lines are treated as white space, not as separate tokens.
         * C-style and C++-style comments are not recognized. These are the
         * defaults and are not needed in constructor.
         */
    }

    /**
     * Constructs a new {@code CustomStreamTokenizer} with {@code r} as source reader.
     * The tokenizer's initial state is as follows:
     * <ul>
     * <li>All byte values 'A' through 'Z', 'a' through 'z', and '&#92;u00A0'
     * through '&#92;u00FF' are considered to be alphabetic.</li>
     * <li>All byte values '&#92;u0000' through '&#92;u0020' are considered to
     * be white space. '/' is a comment character.</li>
     * <li>Single quote '\'' and double quote '"' are string quote characters.
     * </li>
     * <li>Numbers are parsed.</li>
     * <li>End of lines are considered to be white space rather than separate
     * tokens.</li>
     * <li>C-style and C++-style comments are not recognized.</LI>
     * </ul>
     *
     * @param r
     *            the source reader from which to parse tokens.
     */
    public CustomStreamTokenizer(Reader r) {
        this();
        if (r == null) {
            throw new NullPointerException();
        }
        inReader = r;
    }

    /**
     * Specifies that the character {@code ch} shall be treated as a comment
     * character.
     *
     * @param ch
     *            the character to be considered a comment character.
     */
    public void commentChar(int ch) {
        if (0 <= ch && ch < tokenTypes.length) {
            tokenTypes[ch] = TOKEN_COMMENT;
        }
    }

    /**
     * Specifies whether the end of a line is significant and should be returned
     * as {@code TOKEN_TYPE_EOF} in {@code ttype} by this tokenizer.
     *
     * @param flag
     *            {@code true} if EOL is significant, {@code false} otherwise.
     */
    public void eolIsSignificant(boolean flag) {
        isEOLSignificant = flag;
    }

    /**
     * Returns the current line number.
     *
     * @return this tokenizer's current line number.
     */
    public int lineno() {
        return lineNumber;
    }

    /**
     * Specifies whether word tokens should be converted to lower case when they
     * are stored in {@code sval}.
     *
     * @param flag
     *            {@code true} if {@code sval} should be converted to lower
     *            case, {@code false} otherwise.
     */
    public void lowerCaseMode(boolean flag) {
        forceLowercase = flag;
    }

    public void autoUnicodeMode(boolean flag) {
        autoUnicodeMode = flag;
    }

    /* get tokens as specific value */

    public byte[] tokenAsBytes() { return this.byteValue; }
    public String tokenAsString() { return this.stringValue; }
    public long tokenAsCardinal() { return this.cardinalValue; }
    public double tokenAsNumber() { return this.numValue; }

    static final SimpleDateFormat[] _sdf = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ"),
            new SimpleDateFormat("yyyy-MM-ddXXX"),
            new SimpleDateFormat("yyyy-MM-ddZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyyMMddHHmmssXXX"),
            new SimpleDateFormat("yyyyMMddHHmmssZ"),
            new SimpleDateFormat("yyyyMMddHHmmXXX"),
            new SimpleDateFormat("yyyyMMddHHmmZ"),
            new SimpleDateFormat("yyyyMMddXXX"),
            new SimpleDateFormat("yyyyMMddZ"),
            new SimpleDateFormat("yyyyMMddHHmmss"),
            new SimpleDateFormat("yyyyMMddHHmm"),
            new SimpleDateFormat("yyyyMMdd")
    };

    /**
     * Parses the next token from this tokenizer's source stream or reader. The
     * type of the token is stored in the {@code ttype} field, additional
     * information may be stored in the {@code nval} or {@code sval} fields.
     *
     * @return the value of {@code ttype}.
     * @throws IOException
     *             if an I/O error occurs while parsing the next token.
     */
    public static final String HEX_SET = "0123456789abcdefABCDEF";

    public byte checkCharType(int _c, boolean _tokenStart)
    {
        if(_tokenStart && this.byteLiterals && (_c == '<'))
        {
            return TOKEN_BYTES;
        }

        if(_c < 256) return tokenTypes[_c];

        if(autoUnicodeMode)
        {
            if(Character.isAlphabetic(_c)) return  TOKEN_WORD;
            if(Character.isLetter(_c)) return  TOKEN_WORD;
            if(Character.isDigit(_c)) return  TOKEN_DIGIT;
            if(Character.isWhitespace(_c)) return  TOKEN_WHITE;
        }

        return TOKEN_TYPE_UNKNOWN;
    }
    public int nextToken() throws IOException {
        if (pushBackToken) {
            pushBackToken = false;
            if (ttype != TOKEN_TYPE_UNKNOWN) {
                return ttype;
            }
        }
        stringValue = null; // Always reset sval to null
        int currentChar = peekChar == -2 ? read() : peekChar;

        if (lastCr && currentChar == '\n') {
            lastCr = false;
            currentChar = read();
        }
        if (currentChar == -1) {
            return (ttype = TOKEN_TYPE_EOF);
        }

        byte currentType = checkCharType(currentChar, true);

        while ((currentType & TOKEN_WHITE) != 0) {
            /**
             * Skip over white space until we hit a new line or a real token
             */
            if (currentChar == '\r') {
                lineNumber++;
                if (isEOLSignificant) {
                    lastCr = true;
                    peekChar = -2;
                    return (ttype = TOKEN_TYPE_EOL);
                }
                if ((currentChar = read()) == '\n') {
                    currentChar = read();
                }
            } else if (currentChar == '\n') {
                lineNumber++;
                if (isEOLSignificant) {
                    peekChar = -2;
                    return (ttype = TOKEN_TYPE_EOL);
                }
                currentChar = read();
            } else {
                // Advance over this white space character and try again.
                currentChar = read();
            }
            if (currentChar == -1) {
                return (ttype = TOKEN_TYPE_EOF);
            }
            currentType = checkCharType(currentChar, true);
        }

        /**
         * Check for digits before checking for words since digits can be
         * contained within words.
         */
        if ((currentType & TOKEN_DIGIT) != 0) {
            StringBuilder digits = new StringBuilder(20);
            boolean haveDecimal = false, checkJustNegative = currentChar == '-', isHex = false, isBinary = false;
            while (true) {
                if(hexLiterals && (digits.length() == 0) && (currentChar == '0'))
                {
                    // should we detect hex-literals starting with '0x' and switch parsing ?
                    // actually we can discard the zero here
                    currentChar = read();
                    if(currentChar == 'x')
                    {
                        isHex = true;
                        currentChar = read();
                        if (!(HEX_SET.indexOf(currentChar)>=0)) {
                            break;
                        }
                    }
                    else
                    if(currentChar == 'b')
                    {
                        isBinary = true;
                        currentChar = read();
                        if ((currentChar < '0') || (currentChar > '1')) {
                            break;
                        }
                    }
                    else
                        // alternative would be to have a float-literal starting with '0.'.
                        if (currentChar == '.')
                        {
                            haveDecimal = true;
                        }
                        else
                        if ((currentChar < '0') || (currentChar > '9'))
                        {
                            digits.append("0");
                            break;
                        }
                }

                if(isBinary)
                {
                    if(currentChar != '_') digits.append((char) currentChar);
                    currentChar = read();
                    if (((currentChar < '0') || (currentChar > '1'))
                            && (currentChar != '_')) {
                        break;
                    }
                }
                else
                if(isHex)
                {
                    if(currentChar != '_') digits.append((char) currentChar);
                    currentChar = read();
                    if ((!(HEX_SET.indexOf(currentChar)>=0))
                            && (currentChar != '_')) {
                        break;
                    }
                }
                else
                {
                    if (currentChar == '.') {
                        haveDecimal = true;
                    }
                    if(currentChar != '_') digits.append((char) currentChar);
                    currentChar = read();
                    if ((currentChar < '0' || currentChar > '9')
                            && (haveDecimal || currentChar != '.') && (currentChar != '_')) {
                        break;
                    }
                }
            }
            peekChar = currentChar;
            if (checkJustNegative && digits.length() == 1) {
                // Didn't get any other digits other than '-'
                return (ttype = '-');
            }
            try {
                if(isHex)
                {
                    cardinalValue = Long.valueOf(digits.toString(), 16).longValue();
                }
                else
                if(isBinary)
                {
                    cardinalValue = Long.valueOf(digits.toString(), 2).longValue();
                }
                else
                if(haveDecimal)
                {
                    numValue = Double.valueOf(digits.toString()).doubleValue();
                }
                else
                {
                    cardinalValue = Long.valueOf(digits.toString()).longValue();
                }
            } catch (NumberFormatException e) {
                // Unsure what to do, will write test.
                numValue = 0;
            }
            return (ttype = (haveDecimal ? TOKEN_TYPE_NUMBER : TOKEN_TYPE_CARDINAL));
        }
        // Check for words
        if ((currentType & TOKEN_WORD) != 0) {
            StringBuilder word = new StringBuilder(20);
            boolean isDateTime = false;
            while (true) {
                word.append((char) currentChar);
                currentChar = read();
                if(dateTimeLiterals && (word.length() == 1) && (currentChar == ':') && "D".equalsIgnoreCase(word.toString()))
                {
                    // "D:2017-06-08T14:25:36Z"
                    // "D:2017-06-08T14:25:36.005Z"
                    // "D:2017-06-08T14:25:36-03:00"
                    // "D:2017-06-08T14:25:36.005+03:00"
                    isDateTime = true;
                }
                else
                if(dateTimeLiterals && isDateTime && ((tokenTypes[currentChar] & TOKEN_DIGIT) == 0)
                        && (currentChar != 'T') && (currentChar != 'Z')
                        && (currentChar != '-') && (currentChar != '+') && (currentChar != '.') && (currentChar != ':'))
                {
                    break;
                }
                else
                if(!isDateTime && (currentChar == -1
                        || (currentChar < 256 && (tokenTypes[currentChar] & (TOKEN_WORD | TOKEN_DIGIT)) == 0))) {
                    break;
                }
            }
            peekChar = currentChar;
            if(dateTimeLiterals && isDateTime)
            {
                Exception _xet = null;
                for(SimpleDateFormat _sd : _sdf)
                {
                    try{
                        this.cardinalValue = _sd.parse(word.toString().substring(2)).getTime();
                        return (ttype = TOKEN_TYPE_DATETIME);
                    }
                    catch(Exception _xe)
                    {
                        _xet = _xe;
                    }
                }
                throw new IOException(_xet.getMessage(), _xet);
            }
            this.stringValue = this.forceLowercase ? word.toString().toLowerCase() : word.toString();
            return (ttype = TOKEN_TYPE_WORD);
        }
        // Check for byte literals
        if((this.byteLiterals) && (currentType == TOKEN_BYTES))
        {
            StringBuilder _sb = new StringBuilder();
            int _c = readSimpleQuoted('>', _sb);
            this.byteValue = fromHex(_sb.toString().toCharArray());
            return (ttype = TOKEN_TYPE_BYTES);
        }
        // Check for quoted character
        if (currentType == TOKEN_QUOTE) {
            int matchQuote = currentChar;
            StringBuilder quoteString = new StringBuilder();
            int peekOne = read();
            while (peekOne >= 0 && peekOne != matchQuote && peekOne != '\r'
                    && peekOne != '\n') {
                boolean readPeek = true;
                if (peekOne == '\\') {
                    int c1 = read();
                    // Check for quoted octal IE: \377
                    if (c1 <= '7' && c1 >= '0') {
                        int digitValue = c1 - '0';
                        c1 = read();
                        if (c1 > '7' || c1 < '0') {
                            readPeek = false;
                        } else {
                            digitValue = digitValue * 8 + (c1 - '0');
                            c1 = read();
                            // limit the digit value to a byte
                            if (digitValue > 037 || c1 > '7' || c1 < '0') {
                                readPeek = false;
                            } else {
                                digitValue = digitValue * 8 + (c1 - '0');
                            }
                        }
                        if (!readPeek) {
                            // We've consumed one to many
                            quoteString.append((char) digitValue);
                            peekOne = c1;
                        } else {
                            peekOne = digitValue;
                        }
                    } else {
                        switch (c1) {
                            case 'a':
                                peekOne = 0x7;
                                break;
                            case 'b':
                                peekOne = 0x8;
                                break;
                            case 'f':
                                peekOne = 0xc;
                                break;
                            case 'n':
                                peekOne = 0xA;
                                break;
                            case 'r':
                                peekOne = 0xD;
                                break;
                            case 't':
                                peekOne = 0x9;
                                break;
                            case 'v':
                                peekOne = 0xB;
                                break;
                            default:
                                peekOne = c1;
                        }
                    }
                }
                if (readPeek) {
                    quoteString.append((char) peekOne);
                    peekOne = read();
                }
            }

            if (peekOne == matchQuote) {
                peekOne = read();
                if(tripleQuotes && (quoteString.length() == 0) && (peekOne == matchQuote))
                {
                    // found start of QQQ
                    // should we parse it or return error?
                    peekOne = readTripleQuoted(matchQuote,quoteString);
                }
            }
            peekChar = peekOne;
            ttype = matchQuote;
            stringValue = quoteString.toString();
            return ttype;
        }

        // Do comments, both "//" and "/*stuff*/"
        if (currentChar == '/' && (slashSlashComments || slashStarComments)) {
            if ((currentChar = read()) == '*' && slashStarComments) {
                int peekOne = read();
                while (true) {
                    currentChar = peekOne;
                    peekOne = read();
                    if (currentChar == -1) {
                        peekChar = -1;
                        return (ttype = TOKEN_TYPE_EOF);
                    }
                    if (currentChar == '\r') {
                        if (peekOne == '\n') {
                            peekOne = read();
                        }
                        lineNumber++;
                    } else if (currentChar == '\n') {
                        lineNumber++;
                    } else if (currentChar == '*' && peekOne == '/') {
                        peekChar = read();
                        return nextToken();
                    }
                }
            } else if (currentChar == '/' && slashSlashComments) {
                // Skip to EOF or new line then return the next token
                while ((currentChar = read()) >= 0 && currentChar != '\r'
                        && currentChar != '\n') {
                    // Intentionally empty
                }
                peekChar = currentChar;
                return nextToken();
            } else if (currentType != TOKEN_COMMENT) {
                // Was just a slash by itself
                peekChar = currentChar;
                return (ttype = '/');
            }
        }
        // Check for comment character
        if (currentType == TOKEN_COMMENT) {
            // Skip to EOF or new line then return the next token
            while ((currentChar = read()) >= 0 && currentChar != '\r'
                    && currentChar != '\n') {
                // Intentionally empty
            }
            peekChar = currentChar;
            return nextToken();
        }

        peekChar = read();
        return (ttype = currentChar);
    }

    public int readHereDocument(String endHereQuote, StringBuilder quoteString) throws IOException
    {
        while(!quoteString.toString().endsWith(endHereQuote)) {
            int _c = read();
            if(_c == '\n') lineNumber++;
            if(_c<0) throw new EOFException();
            quoteString.append((char) _c);
        }
        quoteString.setLength(quoteString.length()-endHereQuote.length());
        return (peekChar = -2);
    }


    private int readSimpleQuoted(int matchQuote, StringBuilder quoteString) throws IOException
    {
        String _q = Character.toString((char)matchQuote);
        while(!quoteString.toString().endsWith(_q))
        {
            int _c = read();
            if(_c == '\n') lineNumber++;
            if(_c<0) throw new EOFException();
            if(_c>32 && _c<127)
            {
                quoteString.append((char) _c);
            }
        }
        quoteString.setLength(quoteString.length()-1);
        return -2;
    }
    private int readTripleQuoted(int matchQuote, StringBuilder quoteString) throws IOException
    {
        int _c1,_c2,_c3,_c4;
        String _qqq = new String(new char[] {(char) matchQuote, (char) matchQuote, (char) matchQuote});
        while(!quoteString.toString().endsWith(_qqq))
        {
            int _c = read();
            if(_c == '\n') lineNumber++;
            if(_c<0) throw new EOFException();
            if(_c == '\\')
            {
                _c = read();
                switch (_c) {
                    case 'a':
                        _c = 0x7;
                        break;
                    case 'b':
                        _c = 0x8;
                        break;
                    case 'f':
                        _c = 0xc;
                        break;
                    case 'n':
                        _c = 0xA;
                        break;
                    case 'r':
                        _c = 0xD;
                        break;
                    case 't':
                        _c = 0x9;
                        break;
                    case 'v':
                        _c = 0xB;
                        break;
                    case 'x':
                        _c1 = read();
                        _c2 = read();
                        _c = Integer.parseInt(new String(new char[]{(char) _c1, (char) _c2}), 16);
                        break;
                    case 'u':
                        _c1 = read();
                        _c2 = read();
                        _c3 = read();
                        _c4 = read();
                        _c = Integer.parseInt(new String(new char[]{(char) _c1, (char) _c2, (char) _c3, (char) _c4}), 16);
                        break;
                    default:
                        // IGNORE
                }
            }
            quoteString.append((char) _c);
        }
        quoteString.setLength(quoteString.length()-3);
        // text blocks remove all the incidental indentations and keep only essential indentations.
        int _i=0;
        String [] _parts = quoteString.toString().split("\\n");
        // eat whitespaced lines
        while((_i<_parts.length) && _parts[_i].trim().length()==0) _i++;
        int _j=0;
        // find incidential whitespace
        while((_j<_parts[_i].length()) && Character.isWhitespace(_parts[_i].charAt(_j))) _j++;
        quoteString.setLength(0);
        for(int _k=_i; _k<_parts.length; _k++)
        {
            int _l = 0;
            // eat incidential whitespace
            while((_l<_j) && (_l<_parts[_k].length()) && Character.isWhitespace(_parts[_k].charAt(_l))) _l++;
            quoteString.append(_parts[_k].substring(_l));
            if(_k!=_parts.length-1) quoteString.append("\n");
        }
        return -2;
    }

    /**
     * Specifies that the character {@code ch} shall be treated as an ordinary
     * character by this tokenizer. That is, it has no special meaning as a
     * comment character, word component, white space, string delimiter or
     * number.
     *
     * @param ch
     *            the character to be considered an ordinary character.
     */
    public void ordinaryChar(int ch) {
        if (0 <= ch && ch < tokenTypes.length) {
            tokenTypes[ch] = 0;
        }
    }

    /**
     * Specifies that the characters in the range from {@code low} to {@code hi}
     * shall be treated as an ordinary character by this tokenizer. That is,
     * they have no special meaning as a comment character, word component,
     * white space, string delimiter or number.
     *
     * @param low
     *            the first character in the range of ordinary characters.
     * @param hi
     *            the last character in the range of ordinary characters.
     */
    public void ordinaryChars(int low, int hi) {
        if (low < 0) {
            low = 0;
        }
        if (hi > tokenTypes.length) {
            hi = tokenTypes.length - 1;
        }
        for (int i = low; i <= hi; i++) {
            tokenTypes[i] = 0;
        }
    }

    /**
     * Specifies that this tokenizer shall parse numbers.
     */
    public void parseNumbers() {
        for (int i = '0'; i <= '9'; i++) {
            tokenTypes[i] |= TOKEN_DIGIT;
        }
        tokenTypes['.'] |= TOKEN_DIGIT;
        tokenTypes['-'] |= TOKEN_DIGIT;
    }

    /**
     * Indicates that the current token should be pushed back and returned again
     * the next time {@code nextToken()} is called.
     */
    public void pushBack() {
        pushBackToken = true;
    }

    /**
     * Specifies that the character {@code ch} shall be treated as a quote
     * character.
     *
     * @param ch
     *            the character to be considered a quote character.
     */
    public void quoteChar(int ch) {
        if (0 <= ch && ch < tokenTypes.length) {
            tokenTypes[ch] = TOKEN_QUOTE;
        }
    }

    private int read() throws IOException {
        return inReader.read();
    }

    /**
     * Specifies that all characters shall be treated as ordinary characters.
     */
    public void resetSyntax() {
        for (int i = 0; i < 256; i++) {
            tokenTypes[i] = 0;
        }
    }

    /**
     * Specifies whether "slash-slash" (C++-style) comments shall be recognized.
     * This kind of comment ends at the end of the line.
     *
     * @param flag
     *            {@code true} if {@code //} should be recognized as the start
     *            of a comment, {@code false} otherwise.
     */
    public void slashSlashComments(boolean flag) {
        slashSlashComments = flag;
    }

    /**
     * Specifies whether "slash-star" (C-style) comments shall be recognized.
     * Slash-star comments cannot be nested and end when a star-slash
     * combination is found.
     *
     * @param flag
     *            {@code true} if {@code /*} should be recognized as the start
     *            of a comment, {@code false} otherwise.
     */
    public void slashStarComments(boolean flag) {
        slashStarComments = flag;
    }

    public void tripleQuotes(boolean flag) {
        tripleQuotes = flag;
    }

    public void hexLiterals(boolean flag) { hexLiterals = flag; }

    public void byteLiterals(boolean flag) { byteLiterals = flag; }

    public void dateTimeLiterals(boolean flag) { dateTimeLiterals = flag; }

    /**
     * Returns the state of this tokenizer in a readable format.
     *
     * @return the current state of this tokenizer.
     */
    @Override
    public String toString() {
        // Values determined through experimentation
        StringBuilder result = new StringBuilder();
        result.append("Token["); //$NON-NLS-1$
        switch (ttype) {
            case TOKEN_TYPE_EOF:
                result.append("EOF"); //$NON-NLS-1$
                break;
            case TOKEN_TYPE_EOL:
                result.append("EOL"); //$NON-NLS-1$
                break;
            case TOKEN_TYPE_NUMBER:
                result.append("n="); //$NON-NLS-1$
                result.append(numValue);
                break;
            case TOKEN_TYPE_WORD:
                result.append(stringValue);
                break;
            default:
                if (ttype == TOKEN_TYPE_UNKNOWN || tokenTypes[ttype] == TOKEN_QUOTE) {
                    result.append(stringValue);
                } else {
                    result.append('\'');
                    result.append((char) ttype);
                    result.append('\'');
                }
        }
        result.append("], line "); //$NON-NLS-1$
        result.append(lineNumber);
        return result.toString();
    }

    /**
     * Specifies that the characters in the range from {@code low} to {@code hi}
     * shall be treated as whitespace characters by this tokenizer.
     *
     * @param low
     *            the first character in the range of whitespace characters.
     * @param hi
     *            the last character in the range of whitespace characters.
     */
    public void whitespaceChars(int low, int hi) {
        if (low < 0) {
            low = 0;
        }
        if (hi > tokenTypes.length) {
            hi = tokenTypes.length - 1;
        }
        for (int i = low; i <= hi; i++) {
            tokenTypes[i] = TOKEN_WHITE;
        }
    }

    public void whitespaceChar(int low) {
        if (low < 0) {
            low = 0;
        }
        if (low > tokenTypes.length) {
            low = tokenTypes.length - 1;
        }
        tokenTypes[low] |= TOKEN_WHITE;
    }

    public void whitespaceChar(int... _chars) {
        for(int _c : _chars)
        {
            this.whitespaceChar(_c);
        }
    }

    /**
     * Specifies that the characters in the range from {@code low} to {@code hi}
     * shall be treated as word characters by this tokenizer. A word consists of
     * a word character followed by zero or more word or number characters.
     *
     * @param low
     *            the first character in the range of word characters.
     * @param hi
     *            the last character in the range of word characters.
     */
    public void wordChars(int low, int hi) {
        if (low < 0) {
            low = 0;
        }
        if (hi > tokenTypes.length) {
            hi = tokenTypes.length - 1;
        }
        for (int i = low; i <= hi; i++) {
            tokenTypes[i] |= TOKEN_WORD;
        }
    }

    public void wordChar(int low) {
        if (low < 0) {
            low = 0;
        }
        if (low > tokenTypes.length) {
            low = tokenTypes.length - 1;
        }
        tokenTypes[low] |= TOKEN_WORD;
    }

    public void wordChar(int... _chars) {
        for(int _c : _chars)
        {
            this.wordChar(_c);
        }
    }

    /*---------------------------------------------------*/
    static byte[] fromHex(char[] _hex)
    {
        byte[] _ret = new byte[_hex.length/2];
        for(int _i=0; _i<_ret.length; _i++)
        {
            _ret[_i] = (byte) ((toDigit(_hex[_i*2])<<4) | toDigit(_hex[(_i*2)+1]));
        }
        return _ret;
    }

    @SneakyThrows
    static int toDigit(final char ch)
    {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Illegal hexadecimal character " + ch);
        }
        return digit;
    }
}