package com.isotix.nufuel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for a JSON implementation conforming to
 * <a href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>.
 * 
 * Contains several nested classes for reading, writing, and representing JSON entities.
 *
 * <h3>Usage</h3>
 * 
 * <h4>Reading and Writing</h4>
 * Instantiate an {@link JSON.Parser} with an <code>InputStream</code> for reading.
 * 
 */
public class JSON
{
	public static class Object extends JSON
	{
		private HashMap<java.lang.String, JSON> jsonMap;
	
		public Object() 
		{
			jsonMap = new HashMap<java.lang.String, JSON>();
		}

		public void add(java.lang.String key, JSON value)
		{
			jsonMap.put(key, value);
		}

		public JSON get(java.lang.String key)
		{
			return jsonMap.get(key);
		}
		
		public Set<Map.Entry<java.lang.String, JSON>> entries()
		{
			return jsonMap.entrySet();
		}

		@Override
		public java.lang.String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			for(Map.Entry<java.lang.String, JSON> entry : jsonMap.entrySet())
				builder.append(entry.getKey()).append(':')
					   .append(entry.getValue());
			builder.append("}");
			return builder.toString();
		}
	}

	public static class Array extends JSON
	{
		private ArrayList<JSON> jsonList;
	
		public Array()
		{
			jsonList = new ArrayList<JSON>();
		}

		public void add(JSON value)
		{
			jsonList.add(value);
		}

		public JSON get(int index)
		{
			return jsonList.get(index);
		}

		public ArrayList<JSON> list()
		{
			return jsonList;
		}
		
		@Override
		public java.lang.String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			if(!jsonList.isEmpty()) {
				for(JSON json : jsonList)
					builder.append(json).append(",");
				builder.deleteCharAt(builder.length() - 1);
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class Literal extends JSON
	{
		private java.lang.String literal;

		public Literal(java.lang.String literal) { this.literal = literal; }

		public java.lang.String value() { return literal; }

		@Override
		public java.lang.String toString() { return literal; }
	}

	public static class Number extends JSON
	{
		private double number;

		public Number(double number) { this.number = number; }

		public double value() { return number; }

		@Override
		public java.lang.String toString() { return java.lang.String.valueOf(number); }
	}

	public static class String extends JSON
	{
		private java.lang.String string;

		public String(java.lang.String string) { this.string = string; }

		public java.lang.String value() { return string; }

		@Override
		public java.lang.String toString() { return "\"" + string + "\""; }
	}

	public static class SyntaxException extends RuntimeException
	{
		public SyntaxException(int line, int column, java.lang.String message)
		{ super("Syntax Error at " + line + ','+ column + ": " + message); }
	}

	public static class Parser
	{
		private int line, col;
	
		private int index, length;
		private char[] buffer = new char[4096];
		private InputStreamReader reader;

		public Parser(InputStream inputStream)
		{
			reader = new InputStreamReader(inputStream);
		}

		public JSON parse() throws IOException
		{
			switch(look()) 
			{
				case '{': return parseObject();
				case '['	: return parseArray();
				default: throw new JSON.SyntaxException(line, col, "Not start of valid JSON");
			}
		}
	
		public JSON.Object parseObject() throws IOException
		{
			if(next() != '{') // eat '{'
				throw new JSON.SyntaxException(line, col, "'{' not start of JSON Object");

			JSON.Object jsonObject = new JSON.Object();

			if(look() == '}') {
				read();
				return jsonObject;
			}

			while(true)
			{
				java.lang.String key = parseString().value();

				if(next() != ':')
					throw new JSON.SyntaxException(line, col, "':' not key-value delimiter");

				switch(look())
				{
					case 'n': jsonObject.add(key, parseLiteral()); break;
					case 't': jsonObject.add(key, parseLiteral()); break;
					case 'f': jsonObject.add(key, parseLiteral()); break;
					case '"': jsonObject.add(key, parseString()); break;
					case '{': jsonObject.add(key, parseObject()); break;
					case '[': jsonObject.add(key, parseArray()); break;
					default: jsonObject.add(key, parseNumber()); break;
				}
				if(look() != ',') break;
				read();
			}

			if(peek() != '}')
				throw new JSON.SyntaxException(line, col, "'}' not end of JSON Object");
			read(); // eat '}'
			return jsonObject;
		}

		public JSON.Array parseArray() throws IOException
		{
			if(next() != '[') // eat '['
				throw new JSON.SyntaxException(line, col, "'[' not start of JSON Array");

			JSON.Array jsonArray = new JSON.Array();

			if(look() == ']') {
				read();
				return jsonArray;
			}
			
			while(true)
			{
				switch(look())
				{
					case 'n': jsonArray.add(parseLiteral()); break;
					case 't': jsonArray.add(parseLiteral()); break;
					case 'f': jsonArray.add(parseLiteral()); break;
					case '"': jsonArray.add(parseString()); break;
					case '{': jsonArray.add(parseObject()); break;
					case '[': jsonArray.add(parseArray()); break;
					default: jsonArray.add(parseNumber()); break;
				}
				if(look() != ',') break;
				read();
			}

			if(look() != ']')
				throw new JSON.SyntaxException(line, col, "']' not end of JSON Array");
			
			read(); // eat ']'
			return jsonArray;
		}

		public JSON.Literal parseLiteral() throws IOException
		{
			switch(next())
			{
				case 'n': 
					if(read() != 'u') break;
					if(read() != 'l') break;
					if(read() != 'l') break;
				return new JSON.Literal("null");
				case 't':
					if(read() != 'r') break;
					if(read() != 'u') break;
					if(read() != 'e') break;
				return new JSON.Literal("true");
				case 'f':
					if(read() != 'a') break;
					if(read() != 'l') break;
					if(read() != 's') break;
					if(read() != 'e') break;
				return new JSON.Literal("false");
			}
			throw new JSON.SyntaxException(line, col, "Invalid JSON Literal");
		}

		public JSON.Number parseNumber() throws IOException
		{
			char number = next();
			if(number != '-' && number != '.' &&
			   number < '0' || number > '9')
				throw new JSON.SyntaxException(line, col, "Invalid JSON Number");

			StringBuilder builder = new StringBuilder();
			builder.append(number);
			
			outer: while((number = peek()) != 0)
			{
				if(number < '0' || number > '9') {			
					switch(number) {
						case 'E':
						case '+':
						case '-': 
						case '.': break;
						default: break outer;
					}
				}
				builder.append(next());
			}

			try {
				return new JSON.Number(Double.parseDouble(builder.toString()));
			} catch(NumberFormatException nbe) {
				throw new JSON.SyntaxException(line, col, nbe.getMessage());
			}
		}

		/*
		 *
		 */
		public JSON.String parseString() throws IOException
		{
			if(next() != '"') // eat '"'
				throw new JSON.SyntaxException(line, col, "'\"' not start of JSON String");
		
			StringBuilder builder = new StringBuilder();
			char character = 0;
			while((character = peek()) != 0)
				switch(character)
				{
					case '"': read(); // eat '"'
						return new JSON.String(builder.toString());
					case '\\': builder.append(parseEscape()); break;
					default: builder.append(read()); break;
				}

			throw new JSON.SyntaxException(line, col, "'\"' not end of JSON String");
		}

		/*
		 * Helper Parse Methods
		 */
		private char parseEscape() throws IOException
		{
			read(); // eat '\'
			char escChar = read();
			switch(escChar)
			{
				case 'u': // need to finish
					char c1 = read(); if(c1 == 0) break;
					char c2 = read(); if(c2 == 0) break;
					char c3 = read(); if(c3 == 0) break;
					char c4 = read(); if(c4 == 0) break;
					try {
						return (char)Integer.parseInt(new StringBuilder().append(c1)
																		 .append(c2)
																		 .append(c3)
																		 .append(c4)
																		 .toString(), 16);
					} catch(NumberFormatException nbe) { nbe.printStackTrace(); break; }
				case 't': return '\t';
				case 'n': return '\n';
				case 'r': return '\r';
				case 'f': return '\f';
				case 'b': return '\b';
				default: return escChar;
			}
			throw new JSON.SyntaxException(line, col, "Invalid escape sequence");
		}

		/*
		 * Convenience IO Methods
		 */
		private char look() throws IOException
		{
			eat();
			return peek();
		}
		private char next() throws IOException
		{
			eat();
			return read();
		}
		private void eat() throws IOException
		{
			while(true)
				switch(peek()) {
					case ' ':
					case '\t':
					case '\n':
					case '\r':
					case '\f':
						read();
					break;
					default: return;
				}
		}

		/*
		 * Base IO Methods
		 */
		private char peek() throws IOException
		{
			if(index == length) {
				index = 0;
				length = reader.read(buffer);
				if(length == -1) return 0;
			}
			return buffer[index];
		}
		private char read() throws IOException
		{
			if(index == length) {
				index = 0;
				length = reader.read(buffer);
				if(length == -1) return 0;
			}
			if(buffer[index] == '\n') ++line;
			else ++col;
			
			return buffer[index++];
		}
	}
}

	
