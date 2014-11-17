#!/bin/bash

OUTPUTFILE=benchtime.txt
TMPFILE=tmp.txt
OUTPUTCSV=benchtime.csv

XMARKDIR=../../RelationGenerator/test/xml/src/xmark/

# array={"xmark_1M.xml" "xmark_2M.xml" "xmark_5M.xml" "xmark_10M.xml"
#     "xmark_20M.xml" "xmark_50M.xml" "xmark_100M.xml" "xmark_200M.xml"
#     "xmark_500M.xml" "xmark_1G.xml" "xmark_2G.xml" "xmark_10G.xml" }
array=("xmark_1M.xml" "xmark_2M.xml")


echo "" > ${TMPFILE}
echo "" > ${OUTPUTCSV}


for file in ${array[@]}
do
    echo "" > ${OUTPUTFILE}
    for i in {0..10}
    do
	(time -p java -jar peg4d.jar -p src/org/peg4d/lib/xml.p4d ${XMARKDIR}${f} > /dev/null) 2>> ${OUTPUTFILE}
    done
    grep "real" ${OUTPUTFILE} | awk '{print $2}' >  ${TMPFILE}
    awk -F\n -v ORS=',' '{print}' ${TMPFILE} >> ${OUTPUTCSV} && echo -e "" >> ${OUTPUTCSV}
done


##ruby -e 'File.open("benchtime.txt") {|f| f.each.to_a.map{|e| e =~ /real (.*)/; $1}.join(",") }' > ${OUTPUTCSV}
