import codecs
paperidurls=[]
source=codecs.open('pprpdflink.tsv','r','utf-8')
target=codecs.open('cleaned.csv','w','utf-8')
try:
	for line in source:
		record=line.strip().split("\t")
		record=[record[0],record[1].split("/")[-1]+"\n"]
		target.write(",".join(record))
except Exception as e:
		pass
print(len(paperidurls))