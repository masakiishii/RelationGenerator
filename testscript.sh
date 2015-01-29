#!/bin/bash

ant clean && ant

echo `java -jar peg4d.jar -p lib/xml.p4d --relation  mydata/xml/src/xmark.xml > test.csv`

ANSWER=./mydata/xml/csv/validate-xmark/xmark-validate.csv
OUTPUT=./test.csv

result=`diff ${ANSWER} ${OUTPUT}`
if [ "$result" = "" ]; then
    echo "xmark OK"
else
    echo "xmark FAILD"
    #diff ${ANSWER} ${OUTPUT}
fi

echo `java -jar peg4d.jar -p lib/xml.p4d --relation  mydata/xml/src/build_dummy.xml > test.csv`

ANSWER=./mydata/xml/csv/buildxml/buildxml.csv
OUTPUT=./test.csv

result=`diff ${ANSWER} ${OUTPUT}`
if [ "$result" = "" ]; then
    echo "buildxml OK"
else
    echo "buildxml FAILD"
    #diff ${ANSWER} ${OUTPUT}
fi

echo `java -jar peg4d.jar -p lib/json.p4d --relation  mydata/json/src/geojson/earth2.geo > test.csv`

ANSWER=./mydata/json/csv/geojson/val-geojson.csv
OUTPUT=./test.csv

result=`diff ${ANSWER} ${OUTPUT}`
if [ "$result" = "" ]; then
    echo "geojson OK"
else
    echo "geojson FAILD"
    #diff ${ANSWER} ${OUTPUT}
fi

echo `java -jar peg4d.jar -p lib/json.p4d --relation  mydata/json/src/aspen/result.json > test.csv`

ANSWER=./mydata/json/csv/aspen/result.csv
OUTPUT=./test.csv

result=`diff ${ANSWER} ${OUTPUT}`
if [ "$result" = "" ]; then
    echo "new aspen OK"
else
    echo "new aspen FAILD"
    #diff ${ANSWER} ${OUTPUT}
fi
