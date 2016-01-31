#Statistical Phrase Identifier
*Parses text into most likely phrases based upon statistical occurrences in a corpus of data*

The Statistical Phrase Identifier is a request handler for Apache Solr which takes in a string of text and then leverages a language model (an Apache Lucene/Solr index) to predict how the inputted text should be divided into phrases. The intended purpose of this tool is to parse short-text queries into phrases prior to executing a keyword search (as opposed parsing out each keyword as a single term).

##Purpose
Assume you're building a job search engine, and one of your users searches for the following:  
*machine learning research and development Portland, OR software engineer AND hadoop, java*

Most search engines will natively parse this query into the following boolean representation:  
*(machine AND learning AND research AND development AND Portland) OR (software AND engineer AND hadoop AND java)*

While this query may still yield relevant results, it is clear that the intent of the user wasn't understood very well at all. By leveraging the Statistical Phrase Identifier on this string prior to query parsing, you can instead expect the following parsing:  
*{machine learning} {and} {research and development} {Portland, OR} {software engineer} {AND} {hadoop,} {java}*

It is then possile to modify all the multi-word phrases prior to executing the search:  
*"machine learning" and "research and development" "Portland, OR" "software engineer" AND hadoop, java*

Of course, you could do your own query parsing to specifically handle the boolean syntax, but the following would eventually be interpreted correctly by Apache Solr and most other search engines:  
*"machine learning" AND "research and development" AND "Portland, OR" AND "software engineer" AND hadoop AND java*

##Running the Example
To get you up and running quickly, we've included a complete end-to-end example for testing the Statistical Phrase Identifier. This includes example documents (using a sample Stack Exchange data set), the corresponding Solr configuration files (schema.xml, solrconfig.xml, etc.), and a script for starting Solr up to test the Statisical Phrase Identifier.

...

##Building Just the Solr Plugin
The Statistical Phrase Identifier is a Request Handler for Apache Solr. If you only want to build the Apache Solr plugin and tie it into your currently-configured Solr environment, simply execute the following command:

`cd /spi`  
`mvn package`  

You will then find the Statistical Phrase Identifier plugin jar at:
.......
