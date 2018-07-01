# Statistical Phrase Identifier
*Parses text into most likely phrases based upon statistical occurrences in a corpus of data*

The Statistical Phrase Identifier is a request handler for Apache Solr which takes in a string of text and then leverages a language model (an Apache Lucene/Solr index) to predict how the inputted text should be divided into phrases. The intended purpose of this tool is to parse short-text queries into phrases prior to executing a keyword search (as opposed parsing out each keyword as a single term).

## Purpose
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

## Building and Running
The easiest way to build the Statistical Phrase Identifier is to run the `build.sh` script in the root directory of the project (or `rebuild.sh`, which will build and launch an Apache Solr instance with the Statistical Phrase Identifier configured). The final application will be found in the `deploy` directory, and you can launch it using the `restart-solr.sh` script found in that directory. You can simply copy this `deploy` folder to your production environment and run the `restart-solr.sh` script to launch the service. By default, you can hit it at `http://localhost:8983/solr/spi/parse`.

## Using the System
Once the Statistical Phrase Identifier project has been built, you need to indexing a corpus of data through it by running the `feed.sh` script. The fields you include in your corpus should correspond to the fields defined in your Solr `schema.xml` found in the `deploy/solr/spi/conf` directory.
