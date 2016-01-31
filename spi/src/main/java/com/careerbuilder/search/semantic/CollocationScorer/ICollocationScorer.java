package com.careerbuilder.search.semantic.CollocationScorer;

import com.careerbuilder.search.semantic.Models.NGram;
import java.util.List;
import java.util.TreeMap;

public interface ICollocationScorer {
    double evaluateCollocation(NGram nGram, List<TreeMap<String,Object>> scoringOutput);
}
