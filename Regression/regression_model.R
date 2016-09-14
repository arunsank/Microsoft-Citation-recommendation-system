##################################
# @Author : Ganesh Nagarajan, Nishant Shah
# Purpose:
# R Script for regression.
# The regression model total citations as a function of total papers published
#+ papers published as author 1 (A1) though A4, Academic age, Page rank of the paper
##################################
infodataset<-read.csv("d:\\final.txt",header = FALSE,sep = "\t",na.strings =c("NULL",""))
infodataset[is.na(infodataset)] <- 0
colnames(infodataset)<-c("Author.Name","Total.Papers","Total.A1","Total.A2","Total.A3","Total.gt3","Avg.Pgrk","acad.age","Cit.A1","Cit.A2","Cit.A3","Cit.gt3","Total.Citation")
infodataset1<-infodataset[-1]
m_base<-lm(Total.Citation ~ 1, data = infodataset1)
summary(m_base)
m_base.forward <- step(m_base, scope=~Total.Papers+Total.A1+Total.A2+Total.A3+Total.gt3+Avg.Pgrk+acad.age+Cit.A1+Cit.A2+Cit.A3+Cit.gt3, direction="forward")
summary(m_base.forward)
m_base.forward <- step(m_base, scope=~Total.Papers+Total.A1+Total.A2+Total.A3+Total.gt3+Avg.Pgrk+acad.age, direction="forward")
summary(m_base.forward)
summary(infodataset)
