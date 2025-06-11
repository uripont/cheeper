#!/bin/bash

mkdir -p src/main/resources 
rm -rf target/webapps/ROOT 
mvn clean package 
cp target/cheeper.war tomcat-webapps/ROOT.war
echo '✅ Deployed new ROOT.war — http://localhost:8080/'
