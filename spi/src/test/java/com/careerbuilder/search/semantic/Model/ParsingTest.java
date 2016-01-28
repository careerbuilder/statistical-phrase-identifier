package com.careerbuilder.search.semantic.Model;

import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class ParsingTest {

    private TokenizedString tokens = new TokenizedString("Software Test engineer quality assurance");
    private TokenizedString separatorTest = new TokenizedString("Software/Hardware engineer");
    private boolean [] boundaries = {false, true, true, false};
    private int index = 6;

    @Test
    public void buildPhrases_fromIndex() {
        Parsing target = new Parsing(tokens, index);

        String [] expected = {"Software Test", "engineer", "quality assurance"};

        int phraseNo = 0;
        String [] actual = target.toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

    @Test
    public void buildPhrases_fromBoundaries() {
        Parsing target = new Parsing(tokens, boundaries);

        String [] expected = {"Software Test", "engineer", "quality assurance"};

        String [] actual = target.toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

    @Test
    public void includePhraseInParsing_toStringArray() {
        Parsing target = new Parsing(tokens, boundaries);

        String [] expected = {"Software Test", "quality assurance"};

        target.includePhraseInParsing(2, false);

        String [] actual = target.toStringArray();
        for(int i = 0; i < actual.length; ++i)
        {
            Assert.assertTrue(expected[i].compareTo(actual[i])== 0);
        }
    }

    @Test
    public void includePhraseInParsing_toString() {
        Parsing target = new Parsing(tokens, boundaries);

        String expected = "{Software Test} {engineer} {quality assurance}";

        target.includePhraseInParsing(2, false);

        String actual = target.toString();

        Assert.assertTrue(expected.compareTo(actual)== 0);
    }

    @Test
    public void findNextPhraseStart() {
        Parsing target = new Parsing(tokens, boundaries);

        Assert.assertEquals(target.findNextPhraseStart(0), 2);
        Assert.assertEquals(target.findNextPhraseStart(1), 2);
        Assert.assertEquals(target.findNextPhraseStart(2), 3);
        Assert.assertEquals(target.findNextPhraseStart(3), 5);
        Assert.assertEquals(target.findNextPhraseStart(4), 5);
    }

    @Test
    public void compareTo(){
        Parsing target1 = new Parsing(tokens, boundaries);
        Parsing target2 = new Parsing(tokens, new boolean[] { true, false, false, false});
        Parsing target3 = new Parsing(tokens, new boolean[] { false, true, false, true});

        target1.incrementScore(1);
        target2.incrementScore(2);
        target3.incrementScore(3);

        Assert.assertEquals(target1.compareTo(target2), -1);
        Assert.assertEquals(target2.compareTo(target3), -1);
        Assert.assertEquals(target3.compareTo(target2), 1);
        Assert.assertEquals(target2.compareTo(target1), 1);
        Assert.assertEquals(target2.compareTo(target2), 0);
        Assert.assertEquals(target1.compareTo(target1), 0);
    }
}
