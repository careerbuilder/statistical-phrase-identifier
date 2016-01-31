package com.careerbuilder.search.semantic.TermFilter;

import com.careerbuilder.search.semantic.Models.Parsing;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public interface ITermFilter {
    void filterParsings(TreeSet<Parsing> parsings, List<TreeMap<String,Object>> output);
}
