#!/bin/bash

#--- Output File
TMPFILE=tmp.txt
OUTPUTFILE=gov_nodecount.txt
OUTPUTCSV=gov_nodecount.csv

#--- Input  File
array_for_10=(
"BAH_O"
"BabyName"
"ChooseMaryland"
"CityOfSeattleWages"
"CommunitiesConnectNetwork"
"DataAnalysis_2014-8-26"
"Data_Gov_VHA_2010_Dataset2_Hospital_Facility_Services"
"Data_Gov_VHA_2010_Dataset4_Medical_Center_Staffing"
"EducationandYouth2010"
"Grand_loan"
"HPI_master"
"HealthCoalitions"
"Hospital-AcquiredInfections"
"HospitalReadMission"
"NEH_Grants2010s"
"NFSResearch"
"NSFBudgetHistory"
"NSFFundingRateHistory_co"
"NTIS_Catalog_2005_Current"
"PerformanceMetrics"
"ProfessionalMedicalConduct"
"SexOffender"
"StateAverage"
"StudentWeightStatus"
"Vaccination"
"fhlb_members"
"frus-latest"
"investigation"
"rssTopStories"
"ssacardholdersroster_co"
)

#---XML
XML=.xml

#---CSV
CSV=.csv

#---DATASETNUM
NUM=1
DATASET=dataset_

#--- Reference Directory
DIR=~/developments/lab/RelationGenerator/test/xml/src/gov/

#-------------- Generate CSV Data
# : > ${TMPFILE}
# : > ${OUTPUTCSV}

for file in ${array_for_10[@]}
do
    # : > ${OUTPUTFILE}
    # : > ${TMPFILE}
    # (java -jar nez-0.9.jar rel --infer -p src/org/peg4d/lib/xml.p4d -i ${XMARKDIR}${file} > /dev/null) 2>> ${OUTPUTFILE}
    java -jar nez-0.9.2.jar parse -X org.peg4d.data.RelInferWriter -p src/org/peg4d/lib/xml.p4d -i ${DIR}${file}${XML} > ${DIR}${DATASET}${NUM}${CSV}
    NUM=$(expr ${NUM} + 1)
    # echo ${file} >> ${TMPFILE}
    # grep "real" ${OUTPUTFILE} | awk '{print $2}' >>  ${TMPFILE}
    # awk -F\n -v ORS=',' '{print}' ${TMPFILE} >> ${OUTPUTCSV} && echo -e "" >> ${OUTPUTCSV}
done
