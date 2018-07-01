#!/bin/bash
INPUTFOLDER=$1
INPUTCORENAME=$2

CSVFOLDER="${INPUTFOLDER:-../datasets}"
CORENAME="${INPUTCORENAME:-spi}"


for file in $CSVFOLDER/*.zip
do
  unzip -n $file -d $CSVFOLDER
done

for file in $CSVFOLDER/*.csv
do
  echo "Sending $file to Solr"
  curl http://localhost:8983/solr/$CORENAME/update?commit=true --data-binary @$file -H 'Content-type:text/csv'
done

