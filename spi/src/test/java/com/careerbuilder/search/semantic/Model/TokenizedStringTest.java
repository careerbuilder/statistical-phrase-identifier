package com.careerbuilder.search.semantic.Model;

import com.careerbuilder.search.semantic.Models.NGram;
import com.careerbuilder.search.semantic.Models.Parsing;
import com.careerbuilder.search.semantic.Models.TokenizedString;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class TokenizedStringTest {

    private TokenizedString separatorTest = new TokenizedString("Software/Hardware engineer");

    @Test
    public void tokenizedStringOnePhrase() {
        String expected = "Software/Hardware engineer";
        int phraseNo = 0;
        String actual = new NGram(separatorTest, 0, separatorTest.length).toString();
        Assert.assertTrue(expected.compareTo(actual)== 0);
    }

    @Test
    public void tokenizedStringMultiPhrase() {
        String expected = "{Software/Hardware} {engineer}";
        int phraseNo = 0;
        String actual = new Parsing(separatorTest, new boolean[]{false, true}).toString();
        Assert.assertTrue(expected.compareTo(actual)== 0);
    }
}
