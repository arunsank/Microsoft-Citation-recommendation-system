##################################
# @Author : Ganesh Nagarajan
# Purpose:
# Query Hive and create shell script to download unique paper URLs.
##################################

hive -e "select distinct(concat('wget --timeout=60 --tries=2\"',concat(distinct(url),'\"'))) from fil_url where url like '%pdf%';" > pdf.sh
hive -e "select distinct(ppr_id),url from fil_url where url like '%pdf%';" > pprpdflink.tsv
