cd spi/
mvn clean
mvn package
cd ../
ant package-nobuild
cd deploy/
chmod -R +x bin
chmod +x solr/bin/solr
cd bin/
./restart.sh

