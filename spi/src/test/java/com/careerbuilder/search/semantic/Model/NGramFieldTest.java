package com.careerbuilder.search.semantic.Model;

import com.careerbuilder.search.semantic.Models.NGramField;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class NGramFieldTest {

    @Test
    public void getIndex(){
        Assert.assertEquals(1, NGramField.content.getIndex());
        Assert.assertEquals(2, NGramField.bigrams.getIndex());
        Assert.assertEquals(3, NGramField.trigrams.getIndex());
    }
}
