# Citation-Recommender-System
Citation Recommender as part of the ILS-Z604 Web and Text Analytics for Web Data

This project uses the data from Microsoft Academic Graph http://research.microsoft.com/en-us/projects/mag/ for recommendation of citations.
We have used lot of interesting tools for this project.

Hadoop and Hive : Subset the data or Information Retrieval and Machine Learning
R : Regression model to predict how many citations the author would accumulate over time
Apache PDFbox & Shell Scripting: To download and process pdfs to plain text (We have not used this for the recommendation engine yet. Future possibility?)
Python: Evaluation Scripts and Load Data to Neo4j
Neo4j: Graph database to hold citing paper, cited paper and their relationships
Scala and Spark : Leverages Parallel processing of RDD and the MLlib (Machine Learning Library) of spark framework to build a colloborative filtering using Matrix Factorization.
Java and Lucene: Index paper titles and retrieve papers with similar titles

This project uses Alternating Least Squares algotithm, to minimize the MSE (Mean Squared Error). This was the exact algorithm used in the famous Netflix Competition.

Results: Top Few

Query : an ensemble of grapheme and phoneme for machine transliteration (language)

Context 1: english to korean statistical transliteration for information retrieval

Context 2: automatic identification and back transliteration of foreign words for information retrieval

Context 3: automatic transliteration and back transliteration by decision tree learning

Result 1: japanese english cross language information retrieval exploration of query translation and transliteration

Result 2: an english to korean transliteration model of extended markov window

Result 3: an ensemble of transliteration models for information retrieval

Result 4: mining the web to create a language model for mapping between english names and phrases and Japanese

Result 5: backward transliteration for thai document retrieval

Result 6: uci repository of machine learning databases

Result 7: generating phonetic cognates to handle named entities in english chinese cross language spoken document retrieval
