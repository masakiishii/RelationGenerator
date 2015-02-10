#!/bin/bash

#--- Output File
TMPFILE=tmp.txt
OUTPUTFILE=benchtime.txt
#OUTPUTCSV=benchtime_old.csv
#OUTPUTCSV=benchtime_peg4d.csv
OUTPUTCSV=benchtime_new.csv

#--- Input  File
array_for_10=(
"BAH_O.xml"
"BabyName.xml"
"ChooseMaryland.xml"
"CityOfSeattleWages.xml"
"CommunitiesConnectNetwork.xml"
"DataAnalysis_2014-8-26.xml"
"Data_Gov_VHA_2010_Dataset2_Hospital_Facility_Services.xml"
"Data_Gov_VHA_2010_Dataset4_Medical_Center_Staffing.xml"
"EducationandYouth2010.xml"
"FundingRatePI.xml"
"Grand_loan.xml"
"HPI_master.xml"
"HealthCoalitions.xml"
"Hospital-AcquiredInfections.xml"
"HospitalReadMission.xml"
"NEH_Grants2010s.xml"
"NFSResearch.xml"
"NSFBudgetHistory.xml"
"NSFFundingRateHistory_co.xml"
"NTIS_Catalog_2005_Current.xml"
"PerformanceMetrics.xml"
"ProfessionalMedicalConduct.xml"
"SexOffender.xml"
"StateAverage.xml"
"StudentWeightStatus.xml"
"Vaccination.xml"
"fhlb_members.xml"
"frus-latest.xml"
"investigation.xml"
"ssacardholdersroster_co.xml"
)

#--- Reference Directory
#XMARKDIR=xmark-bench/


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
