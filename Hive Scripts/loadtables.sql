##################################
# @Author : Ganesh Nagarajan
# Purpose:
# Load the data into corresponding hive tables.
##################################
LOAD DATA LOCAL INPATH 'Affiliations.txt' OVERWRITE INTO TABLE affiliations;
LOAD DATA LOCAL INPATH 'Authors.txt' OVERWRITE INTO TABLE authors;
LOAD DATA LOCAL INPATH 'Conferences.txt' OVERWRITE INTO TABLE conference;
LOAD DATA LOCAL INPATH 'ConferenceInstances.txt' OVERWRITE INTO TABLE conferenceinstance;
LOAD DATA LOCAL INPATH 'FieldsOfStudy.txt' OVERWRITE INTO TABLE fieldofstudy;
LOAD DATA LOCAL INPATH 'Journals.txt' OVERWRITE INTO TABLE journals;
LOAD DATA LOCAL INPATH 'PaperAuthorAffiliations.txt' OVERWRITE INTO TABLE paperaffiliations;
LOAD DATA LOCAL INPATH 'PaperKeywords.txt' OVERWRITE INTO TABLE paperkeywords;
LOAD DATA LOCAL INPATH 'PaperReferences.txt' OVERWRITE INTO TABLE paperreferences;
LOAD DATA LOCAL INPATH 'Papers.txt' OVERWRITE INTO TABLE papers;
LOAD DATA LOCAL INPATH 'PaperUrls.txt' OVERWRITE INTO TABLE paperurl;
