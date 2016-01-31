package com.careerbuilder.search.semantic.Models;

import java.util.HashMap;
import java.util.Map;

public enum NGramField {

    content(1), bigrams(2), trigrams(3),
    content_title(1), bigrams_title(2), trigrams_title(3);

    private int index;

    NGramField(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
