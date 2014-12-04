#!/bin/bash

#--- Command OPTION
#RELOPTION=--relation:infer

#--- Output File
TMPFILE=tmp.txt
OUTPUTFILE=benchtime.txt
OUTPUTCSV=benchtime.csv

#--- Input  File
#array_for_10=("xmark_1M.xml")

array_for_10=("xmark_1M.xml" "xmark_2M.xml" "xmark_5M.xml" "xmark_10M.xml" "xmark_20M.xml")

#array_only_1=("xmark_50M.xml")

array_only_1=("xmark_50M.xml")

#--- Reference Directory
XMARKDIR=../../RelationGenerator/test/xml/src/xmark/


#-------------- Generate CSV Data
: > ${TMPFILE}
: > ${OUTPUTCSV}

for file in ${array_for_10[@]}
do
    : > ${OUTPUTFILE}
    : > ${TMPFILE}
    for i in {0..10}
    do
	(time -p java -jar nez-0.9.jar rel --infer -p src/org/peg4d/lib/xml.p4d -i ${XMARKDIR}${file} > /dev/null) 2>> ${OUTPUTFILE}
    done
    echo ${file} >> ${TMPFILE}
    grep "real" ${OUTPUTFILE} | awk '{print $2}' >>  ${TMPFILE}
    awk -F\n -v ORS=',' '{print}' ${TMPFILE} >> ${OUTPUTCSV} && echo -e "" >> ${OUTPUTCSV}
done

for file in ${array_only_1}
do
    : > ${OUTPUTFILE}
    : > ${TMPFILE}
    (time -p java -jar nez-0.9.jar rel --infer -p src/org/peg4d/lib/xml.p4d -i ${XMARKDIR}${file} > /dev/null) 2>> ${OUTPUTFILE}
    echo ${file} >> ${TMPFILE}
    grep "real" ${OUTPUTFILE} | awk '{print $2}' >>  ${TMPFILE}
    awk -F\n -v ORS=',' '{print}' ${TMPFILE} >> ${OUTPUTCSV} && echo -e "" >> ${OUTPUTCSV}
done
