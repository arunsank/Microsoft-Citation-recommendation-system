# Pagerank calculation for Paper  
We have tried to calculate pagerank for every papers in database based on the numbers of papers, each paper sites and how many times each paper was cited by other papers.  
For this we subset the papers for fields of Machine Learning and Information Retrievel. Then we took PaperID of those and subset the file `PaperReferences` and created `paper_ref_ml.txt` which has same columns as PaperReferences.  
We can extend the idea of calculating pageranks for other fields as well, i.e. Authors, Conferences, Location. This will improve the result of our recommendations.  
This script is just a prototype for now. Altough the subset file size is small enough, calculating pagerank required good amount of calculation. So calculation for this file was consuming too much memory and we were not able to run the script successfully despite the best optimizations possible for pure python in AWS cloud VM. Given a time, we could have defined Map-Reduce task for calculating and could have run this system on some distributed system. Also we were not able to find particular library for pagerank as per our needs. 

### References:  
1. http://www.cs.princeton.edu/~chazelle/courses/BIB/pagerank.htm  
2. http://dpk.io/pagerank  
