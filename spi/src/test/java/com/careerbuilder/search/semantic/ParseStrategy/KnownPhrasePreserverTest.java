package com.careerbuilder.search.semantic.ParseStrategy;

import com.careerbuilder.search.semantic.Models.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class KnownPhrasePreserverTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");
    private TokenizedString tokensDelimiters = new TokenizedString("Software | Test engineer / quality-assurance");
    private Parsing parsing;
    private Parsing parsingDelimiters;

    @Before
    public void init(){
        parsing = new Parsing(tokens, new boolean[]{true, true, true, true});
        parsingDelimiters = new Parsing(tokensDelimiters, new boolean[]{true, true, true, true});
    }

    @Test
    public void testPreserve(){
        KnownPhrasePreserver target = new KnownPhrasePreserver(tokens, "Software Test^ quality assurance");
        boolean [] actual = target.preserveKnownPhrases(parsing.getBoundaries());
        boolean [] expected = new boolean[] {false, true, true, false};
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(actual[i] == expected[i]);
        }
    }

    @Test
    public void emptyPhrase(){
        KnownPhrasePreserver target = new KnownPhrasePreserver(tokens, "Software Test^ ");
        boolean [] actual = target.preserveKnownPhrases(parsing.getBoundaries());
        boolean [] expected = new boolean[] {false, true, true, true};
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(actual[i] == expected[i]);
        }
    }

    @Test
    public void delimiters(){
        KnownPhrasePreserver target = new KnownPhrasePreserver(tokensDelimiters, "Software-Test ^ quality / assurance");
        boolean [] actual = target.preserveKnownPhrases(parsingDelimiters.getBoundaries());
        boolean [] expected = new boolean[] {false, true, true, false};
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(actual[i] == expected[i]);
        }
    }
}
