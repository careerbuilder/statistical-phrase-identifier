package com.careerbuilder.search.semantic.Models;

import java.util.HashMap;

public class FrequencyMap {
    public HashMap<String, Long> descriptionTermFrequencyMap;
    public HashMap<String, Long> descriptionDocFrequencyMap;
    public HashMap<String, Long> titleTermFrequencyMap;
    public HashMap<String, Long> titleDocFrequencyMap;
    public HashMap<NGramField, Long> totalDescriptionTermFrequencyMap;
}
