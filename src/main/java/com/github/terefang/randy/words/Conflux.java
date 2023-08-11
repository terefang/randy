/***********************************************************************************
 * 
 *	Copyright (c) 2006-2015, Alfred Reibenschuh
 *	All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without 
 *	modification, are permitted provided that the following conditions 
 *	are met:
 *
 *	1. Redistributions of source code must retain the above copyright 
 *	notice, this list of conditions and the following disclaimer.
 *
 *	2. Redistributions in binary form must reproduce the above copyright 
 *	notice, this list of conditions and the following disclaimer in the 
 *	documentation and/or other materials provided with the distribution.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 *	"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 *	LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 *	A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 *	HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 *	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 *	TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *	LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 ***********************************************************************************/

package com.github.terefang.randy.words;

import com.github.terefang.randy.rng.impl.ArcRand;

import java.io.*;
import java.util.*;

public class Conflux extends HashMap<String,List<Character>>
{
	int tableSize = 3;

	boolean restrictedMode = false;
	boolean debug = false;
	int loopBreaker = 1000;
	int fudge = 3;
	boolean breakHarder = false;
	boolean allowSpecialChars = false;
	boolean allowExtendedChars = false;
	private boolean useDashSpace;

	public int getTableSize() {
		return tableSize;
	}

	public boolean isUseDashSpace() {
		return useDashSpace;
	}

	public void setUseDashSpace(boolean useDashSpace) {
		this.useDashSpace = useDashSpace;
	}

	public boolean isBreakHarder() {
		return breakHarder;
	}

	public void setBreakHarder(boolean breakHarder) {
		this.breakHarder = breakHarder;
	}

	public boolean isAllowSpecialChars()
	{
		return allowSpecialChars;
	}

	public void setAllowSpecialChars(boolean allowSpecialChars)
	{
		this.allowSpecialChars = allowSpecialChars;
	}

	public boolean isAllowExtendedChars()
	{
		return allowExtendedChars;
	}

	public void setAllowExtendedChars(boolean allowExtendedChars)
	{
		this.allowExtendedChars = allowExtendedChars;
	}

	public int getFudge()
	{
		return fudge;
	}

	public void setFudge(int fudge)
	{
		this.fudge = fudge;
	}

	public int getLoopBreaker()
	{
		return loopBreaker;
	}

	public void setLoopBreaker(int loopBreaker)
	{
		this.loopBreaker = loopBreaker;
	}

	public boolean isRestrictedMode()
	{
		return restrictedMode;
	}

	public void setRestrictedMode(boolean restrictedMode)
	{
		this.restrictedMode = restrictedMode;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public Conflux()
	{
		super();
	}

	public Conflux loadFromString(String _words) throws IOException
	{
		return this.load(new StreamTokenizer(new StringReader(_words)), 3);
	}

	public Conflux loadFromString(int _ts, String _words) throws IOException
	{
		return this.load(new StreamTokenizer(new StringReader(_words)), _ts);
	}

	public Conflux load(File fn) throws IOException
	{
		return this.load(fn, this.tableSize);
	}

	public Conflux load(File fn, int ts) throws IOException
	{
		FileReader fin = new FileReader(fn);
		StreamTokenizer st = new StreamTokenizer(fin);
		this.load(st, ts);
		fin.close();
		return this;
	}

	public Conflux load(StreamTokenizer st) throws IOException
	{
		return this.load(st, this.tableSize);
	}

	public Conflux load(StreamTokenizer st, int ts) throws IOException
	{
		this.tableSize = (ts>this.tableSize) ? ts : this.tableSize;

		int tokenType;

		st.resetSyntax();
		st.whitespaceChars(0, 32);
		st.slashSlashComments(true);
		st.slashStarComments(true);
		st.commentChar('#');
		st.commentChar(';');
		st.commentChar(':');
		st.commentChar('.');
		st.commentChar('%');
		st.lowerCaseMode(true);
		st.wordChars('A', 'Z');
		st.wordChars('a', 'z');

		if(allowSpecialChars)
		{
			st.wordChars('\'', '\'');
			st.wordChars('`', '`');
			st.wordChars('-', '-');
		}

		if(allowExtendedChars)
		{
			st.wordChars(0x7e, 0xff);
		}

		while((tokenType = st.nextToken()) !=  StreamTokenizer.TT_EOF)
		{
			if(tokenType == StreamTokenizer.TT_WORD)
			{
				if(debug) System.out.println("S = "+st.sval);
				this.putWord(ts, st.sval);;
			}
		}

		for(Entry<String, List<Character>> _entry : this.entrySet())
		{
			Collections.sort(_entry.getValue(), (_a, _b) -> {
				return Character.compare((char)_a, (char)_b);
			});
		}

		return this;
	}

	public void putWord(String buf)
	{
		putWord(this.tableSize, buf);
	}

	public void putWord(int ts, String buf)
	{
		buf = buf.toLowerCase();
		this.putTable(" ", buf.charAt(0));

		for(int j=1; j<=ts && j<buf.length(); j++)
		{
			for(int i=j; i<buf.length(); i++)
			{
				String key = buf.substring(i-j, i);
				char v = buf.charAt(i);
				this.putTable(key, v);
			}
		}
		for(int i= ((ts>=buf.length()) ? buf.length() : ts); i>0; i--)
		{
			String key = buf.substring(buf.length()-i, buf.length());
			this.putTable(key, ' ');
		}

		for(int _i=0; _i<ts && _i<buf.length(); _i++)
		{
			this.putTable(" "+buf.substring(0,_i), buf.charAt(_i));
		}
	}

	public void putTable(String key, char v)
	{
		if(!this.containsKey(key))
		{
			if(this.isRestrictedMode())
			{
				this.put(key, new UniqueList<>());
			}
			else
			{
				this.put(key, new Vector<>());
			}
		}
		this.get(key).add(v);
	}

	public Collection<Character> getTable(String key)
	{
		String lookup = key;

		if(lookup.length()>this.tableSize)
		{
			lookup = lookup.substring(lookup.length()-tableSize);
		}

		while(!this.containsKey(lookup) && lookup.length()>0)
		{
			lookup = lookup.substring(1);
		}

		if(!this.containsKey(lookup))
		{
			return null;
		}
		else
		{
			return this.get(lookup);
		}
	}

	public char getChar(ArcRand _rng, String key)
	{
		Collection<Character> v = getTable(key);
		if(v==null)
		{
			//return ' ';
			System.err.println("Error: lookup '"+key+"' no probs.");
			return (char)('a'+_rng.nextInt(26));
		}
		else
		{
			int i = _rng.nextInt(v.size());
			return (Character) v.toArray()[i];
		}
	}

	public char getStartChar(ArcRand _rng)
	{
		return getChar(_rng, " ");
	}

	public List<String> generateWords(long _seed, List<String> w, int size, int num)
	{
		return generateWords(ArcRand.from(_seed), w, size, num);
	}

	public List<String> generateWords(ArcRand _rng, List<String> w, int size, int num)
	{
		int itr = 0;
		while(w.size() < num)
		{
			if(itr>loopBreaker*num*size)
			{
				throw new RuntimeException("loop detected");
			}

			String _word = this.generateWord(_rng, size, false);
			if(!w.contains(_word))
			{
				w.add(_word);
				itr=0;
			}
			itr++;

			if(debug) System.out.println(_word+"\t"+w);
		}
		return w;
	}

	public String generateWord(long _seed, int size, boolean keepSpace)
	{
		return generateWord(ArcRand.from(_seed), size, keepSpace);
	}

	public boolean canBreak(String _word)
	{
		for(int _i=this.tableSize ; _i>1; _i--)
		{
			if(_word.length() >= _i)
			{
				String _key = _word.substring(_word.length()-_i);
				if(this.containsKey(_key) && this.get(_key).get(0)==' ')
				{
					return true;
				}
			}
		}
		return false;
	}

	public String generateWord(ArcRand _rng, int size, boolean keepSpace)
	{
		StringBuilder word = new StringBuilder();
		word.append(" ");
		//word.append(this.getStartChar());
		int itr = 0;

		while(true)
		{
			if((breakHarder && (itr>loopBreaker))
					|| (itr>loopBreaker*2)
					|| (word.toString().trim().length() > size*2)
					|| ((word.toString().trim().length() >= size) && this.canBreak(word.toString())))
			{
				return word.toString().trim();
			}

			char c = this.getChar(_rng, word.toString());

			// break loop if size gets too big
			if((breakHarder || (itr>loopBreaker)) && (word.length()>(size+fudge)))
			{
				return word.toString().trim();
			}

			if(c==' ')
			{
				if(word.toString().trim().length()>size)
				{
					return word.toString().trim();
				}

				if(keepSpace)
				{
					if(useDashSpace)
					{
						word.append("-");
					}
					else
					{
						word.append(c);
					}
				}


				word.append(this.getStartChar(_rng));
			}
			else
			if(c=='\'' || c=='-' || c=='`')
			{
				if(word.toString().trim().length()>size)
				{
					return word.toString().trim();
				}
				else
				{
					word.append(c);
				}
			}
			else
			{
				word.append(c);
			}

			itr++;
		}
	}

	public void printTable()
	{
		Vector<String> k = new Vector(this.keySet());
		Collections.sort(k, new Comparator<String>() { public int compare(String s1, String s2){ return s1.compareTo(s2); } });

		for(String key : k)
		{
			System.out.println("\""+key+"\" = "+this.get(key));
		}
	}



	public static void main(String [] args) throws IOException
	{
		Conflux cf = new Conflux();
		cf.setRestrictedMode(true);
		//cf.setDebug(true);
		cf.setLoopBreaker(1000);
		cf.setFudge(0);
		cf.setAllowExtendedChars(true);
		cf.setAllowSpecialChars(true);
		cf.load(new File("./src/test/resources/test.txt"), 2);

		Vector<String> w = new Vector();
		for(int i=5; i<9; i++)
		{
			try
			{
				cf.generateWords(0x1E27L, w, i, w.size()+10);
			}
			catch (Exception xe)
			{
				System.err.println("I="+i+" W="+w.size()+": "+xe);
			}
		}

		Collections.sort(w, new Comparator<String>()
		{
			public int compare(String s1, String s2)
			{
				return s1.compareTo(s2);
				//return Integer.compare(s1.length(), s2.length())==0 ? s1.compareTo(s2) : Integer.compare(s1.length(), s2.length());
			}
		});

		for(int i=0; i<w.size(); i++)
		{
			System.out.println(w.elementAt(i));
		}
		cf.printTable();
	}

	public final String toId(ArcRand _rng, String _n, int _size, boolean keepSpaces)
	{
		return this.toId(_rng, _n, _size, keepSpaces, 0xfffL,13, true);
	}

	public final String toId(ArcRand _rng, String _n, int _size, boolean keepSpaces, long _mask)
	{
		return this.toId(_rng, _n, _size, keepSpaces,_mask,13, true);
	}

	public static final String[] GREEK_ALPHA = { "alpha","beta","gamma","delta","epsilon","zeta","eta","theta","iota",
			"kappa","lambda","mu","nu","xi","omicron","pi","rho","tau","upsilon","phi","chi","psi","omega" };
	public final String toId(ArcRand _rng, String _n, int _size, boolean keepSpaces, long _mask, int _radix, boolean numberSuffix)
	{
		UUID _uuid = UUID.nameUUIDFromBytes(_n.getBytes());
		if(numberSuffix)
		{
			if(((_uuid.getLeastSignificantBits()>>>32) & 1) == 1)
			{
				return String.format("%s %s %s", GREEK_ALPHA[_rng.nextInt(GREEK_ALPHA.length)], this.generateWord(_rng, _size, false), Long.toString(_uuid.getLeastSignificantBits() & _mask, _radix));
			}
			else
			{
				return String.format("%s %s", this.generateWord(_rng, _size, keepSpaces), Long.toString(_uuid.getLeastSignificantBits() & _mask, _radix));
			}
		}
		else
		{
			return String.format("%s %s", Long.toString(_rng.nextInt((int) (_mask+1)), _radix), this.generateWord(_rng, _size, keepSpaces));
		}
	}

	public final String toWid(ArcRand _rng, int _size1, int _size2, boolean keepSpaces)
	{
		return String.format("%s %s", this.generateWord(_rng, _size1, false), this.generateWord(_rng, _size2, keepSpaces));
	}

	public void setTableSize(int i) {
		this.tableSize = i;
	}

	static class UniqueList<T> extends ArrayList<T> implements List<T>
	{
		@Override
		public boolean add(T t)
		{
			if(this.contains(t)) return true;
			return super.add(t);
		}

		@Override
		public boolean addAll(Collection<? extends T> c)
		{
			for(T _e : c)
			{
				this.add(_e);
			}
			return true;
		}

	}
}