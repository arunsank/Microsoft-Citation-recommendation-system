##################################
# @Author : Ganesh Nagarajan
# Purpose:
# Once the Microsoft Graph is loaded into the Hive, the following script creates temp tables and the final table for the regression analysis.
##################################
CREATE TABLE fil_keywords ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select fos.f_name,kwrds.ppr_id,kwrds.KEY_NAME from fieldofstudy fos join paperkeywords kwrds on(fos.fld_id=kwrds.fos_id and fos.f_name in ('Information retrieval','Machine learning'));
CREATE TABLE fil_affil ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select k.f_name,k.KEY_NAME,b.* from fil_keywords k join paperaffiliations b on (k.ppr_id=b.ppr_id);
CREATE TABLE fil_auth ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as selecta.f_name,a.KEY_NAME,a.AFF_ID,a.OR_AFF_NAME,a.NORM_AFF_NAME,a.SEQ_NM,b.AUTH_NAME from fil_affil a join authors b on a.auth_id=b.auth_id;
CREATE TABLE fil_paper ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select a.f_name,a.KEY_NAME,a.OR_AFF_NAME,a.NORM_AFF_NAME,a.SEQ_NM,a.AUTH_NAME,b.* from fil_auth a left join papers b on (a.ppr_id=b.ppr_id);
CREATE TABLE fil_conf ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select a.*,b.SH_NAME STRING,b.FULL_NAME from fil_paper a left join conference b on a.conf_id=b.conf_id;
CREATE TABLE fil_jour ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select a.*,b.J_NAME from fil_conf a left join journals b on (a.jrnl_id=b.jrnl_id);
CREATE TABLE reg_papercount ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select auth_name, count(ppr_id) from fil_conf group by auth_name;
create table authaff ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select f.*,a.auth_name,b.aff_name from filteredaffiliations f join authors a on (f.auth_id=a.auth_id) join affiliations b on (f.aff_id=b.aff_id);
create table paperfil ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select a.auth_name,a.NORM_AFF_NAME,b.* from authaff a join papers b on (a.ppr_id=b.ppr_id);
CREATE TABLE final ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE as select a.*,b.sh_name,b.full_name,c.j_name from paperfil a join conference b on (a.conf_id=b.conf_id) join journals c on(a.jrnl_id=c.jrnl_id);


