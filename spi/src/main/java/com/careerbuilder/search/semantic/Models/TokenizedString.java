package com.careerbuilder.search.semantic.Models;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;

public class TokenizedString {
    public final String [] tokens;
    public final String [] separators;
    public final int length;

    public TokenizedString(String str)
    {
        str = str.replaceAll("[\\n\\r\\s]", " ").replaceAll("\"", "");
        String characters = " &-\\/|";
        str = StringUtils.strip(str, characters);
        HashSet<Character> splitChars = extractCharacters(characters);

        LinkedList<String> tokens = new LinkedList<String>();
        LinkedList<String> separators = new LinkedList<String>();
        tokenizeString(str, splitChars, tokens, separators);
        this.tokens = tokens.toArray(new String[tokens.size()]);
        this.separators = separators.toArray(new String[separators.size()]);
        this.length = this.tokens.length;
    }

    private HashSet<Character> extractCharacters(String characters) {
        HashSet<Character> splitChars = new HashSet<Character>();
        for(int i = 0; i < characters.length(); ++i)
        {
            splitChars.add(characters.charAt(i));
        }
        return splitChars;
    }

    private void tokenizeString(String str, HashSet<Character> splitChars, LinkedList<String> tokens, LinkedList<String> separators) {
        int index = 0;
        StringBuilder builder = new StringBuilder();
        boolean word = true;
        boolean previousWord = true;
        while(index < str.length())
        {
            word = !splitChars.contains(str.charAt(index));
            if(previousWord && !word) {
                tokens.add(builder.toString());
                builder.setLength(0);
            }
            if(word && !previousWord)
            {
                separators.add(builder.toString());
                builder.setLength(0);
            }

            builder.append(str.charAt(index));
            previousWord =  word;
            ++index;
        }
        if(word)
            tokens.add(builder.toString());
        else
            separators.add(builder.toString());
    }
}
