ant pull
cd lucene-solr/
ant ivy-bootstrap
cd ../spi/
mvn clean
mvn package
cd ../
ant package
cd deploy/
chmod -R +x bin
chmod +x solr/bin/solr
