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
The easiest way to build the Statistical Phrase Identifier is to run the `build.sh` script in the root directory of the project, which will pull and compile Apache Solr with the Statistical Phrase Identifier intalled and ready to use. The final application will be found in the `deploy/` directory, which you can simply copy to your production environment to run the Statistical Phrase Identifier. 

You can use the scripts in the `deploy/bin/` directory to operate the Statistical Phrase Identifier. The available scripts include:
* `start.sh`: used to start Apache Solr running the Statistical Phrase Identifier if no current instance is running (and otherwise leave any running instances alone)
* `restart.sh`: will stop any currently running instances of Solr and start the Statistical Phrase Identifier cleanly
* `stop.sh`: will stop the Statistical Phrase Identifier
* `debug.sh`: will restart the Statistical Phrase Identifier with remote JVM debugging enabled
* `load-data.sh`: will send all `.csv` files (or `.zip` files containing `.csv` files) in the `deploy/datasets/` directory to the Statistical Phrase Identifier to use as a language model for its statistical calculations. The Statistical Phrase Identifier expects CSVs of documents with the columns `id`, `title`, `description` (and optionally other fields, which you can configure in Solr's `schema.xml` found in `deploy/solr/server/solr/spi/conf/`).

You can simply copy the `deploy/` folder to your production environment and run the `bin/start.sh` script (if no current instances are running) or the `bin/restart.sh` script (recommended) to launch the service. By default, you can the access the API for the Statistical Phrase Identifier at `http://localhost:8983/solr/spi/parse?q=PHRASE_TO_PARSE`.

After your initial build has completed, you can also subsequently run `redeploy.sh` from the root directory, which will only rebuild and package the Statistical Phrase Identifier and skip the expensive step of building Solr again, and then start (or restart) the newly deployed version. This comes in handy during rapid development.

## Using the System
Once the Statistical Phrase Identifier project has been built (`./build.sh`) and you have loaded a corpus of data into it (`deploy/bin/load-data.sh`), you can then send strings of text to the API to see how they are parsed based upon the language model built from the indexed data.

The statistical phrase identifier ships with a sample dataset derived from Stack Exchange's Scifi dataset (located in `deploy/datasets/` once the project is built). Here is a sample query for that domain:
### Request
```
http://localhost:8983/solr/spi/parse?q=darth vader obi wan kenobi anakin skywalker toad x men magneto professor xavier
```

Prior to running `deploy/bin/load-data.sh` all terms would be seen as independent since there is no data from which to calculate any statistically-likely phrases:
### Response (before loading data):
```
{
  "responseHeader":{
    "status":0,
    "QTime":32},
  "top_parsed_query":"{darth} {vader} {obi} {wan} {kenobi} {anakin} {skywalker} {toad} {x} {men} {magneto} {professor} {xavier}",
  "top_parsed_phrases":["darth",
    "vader",
    "obi",
    "wan",
    "kenobi",
    "anakin",
    "skywalker",
    "toad",
    "x",
    "men",
    "magneto",
    "professor",
    "xavier"],
  "potential_parsings":[{
      "parsed_phrases":["darth",
        "vader",
        "obi",
        "wan",
        "kenobi",
        "anakin",
        "skywalker",
        "toad",
        "x",
        "men",
        "magneto",
        "professor",
        "xavier"],
      "parsed_query":"{darth} {vader} {obi} {wan} {kenobi} {anakin} {skywalker} {toad} {x}-{men} {magneto} {professor} {xavier}",
      "score":0.0}]}
```

After loading the Scifi dataset into the Statistical Phrase Idenfifier, however, you will then see more meaningful output with the phrases in the incoming query parsed out:
### Response (after loading data):
```
{
  "responseHeader":{
    "status":0,
    "QTime":25},
  "top_parsed_query":"{darth vader} {obi wan kenobi} {anakin skywalker} {toad} {x men} {magneto} {professor xavier}",
  "top_parsed_phrases":["darth vader",
    "obi wan kenobi",
    "anakin skywalker",
    "toad",
    "x-men",
    "magneto",
    "professor xavier"],
  "potential_parsings":[{
      "parsed_phrases":["darth vader",
        "obi wan kenobi",
        "anakin skywalker",
        "toad",
        "x-men",
        "magneto",
        "professor xavier"],
      "parsed_query":"{darth vader} {obi wan kenobi} {anakin skywalker} {toad} {x-men} {magneto} {professor xavier}",
      "score":0.0}]}
```

## TODO
Document various configuration and language model options

## TL;DR
1. Run `./build.sh`
2. `cd deploy/bin/ && ./restart.sh && ./load-data.sh`
3. Go to `http://localhost:8983/solr/spi/parse?q=darth vader obi wan kenobi anakin skywalker toad x-men magneto professor xavier` in your favorite web browser
#### Curl Command:
```
curl http://localhost:8983/solr/spi/parse?q=darth%20vader%20obi%20wan%20kenobi%20anakin%20skywalker%20toad%20x-men%20magneto%20professor%20xavier
```

4. Substitute in your own phrases in the `q=` parameter and integrate into your application as you see fit. Enjoy!