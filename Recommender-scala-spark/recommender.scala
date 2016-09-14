/**
Spark program for the recommendation engine.
Uses the Alternating Least Squares Algorithm for Colloborative filtering.
Authors : Ganesh Nagarajan, Neha Bisht
Data Preprocessing: Ganesh Nagarajan, Siddharth Jaysankar, Arun and Nishanth
This code is based on http://spark.apache.org/docs/latest/mllib-collaborative-filtering.html
and the getting started scala guide.
*/

/** Imports */
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD
import scala.io.Source
import java.io._


object Recommender {
  /**
  Object Recommender is a singleton class for training a ALS model for
  Colloborative filtering. This uses Spark's mllib library for parallel processing
  to train the model and predicting the recommenders.
  */
  def start(): Unit = {
    /** Equivalent to def main. Since this is run using spark-shell and not
    spark-submit, the object should be singleton and have direct call functions.
    */

    /** Configuration Parameters */
    val rank = 20
    val numIterations = 30
    val graphFile = "/home/ganesh/Desktop/graph_train.csv"
    val graphTest = "/home/ganesh/Desktop/graph_test.csv"
    val similarityFile = "/home/ganesh/spark/bin/similarity_result.txt"
    val lambda = 0.01
    val alpha = 0.01
    /** File Handlers */
    val docFile = sc.textFile(graphFile)
    val docTest = sc.textFile(graphTest)
    val docSimilarity = Source.fromFile(similarityFile).getLines()

    /**
     * Read the graph file to create input items, citing paper and the
     * cited paper for the matrix factorization. Here the Citing paper
     * is considered as user and the Cited Paper is considered as the product.
     * We are trying to recommend the products to the user
     */

    val traindata = docFile.map { line =>
      val content = line.split(",")
      (content(4), content(0))
    }

    val testpair = docTest.map { line =>
      val content = line.split(",")
      (content(4), content(0))
    }

    /**
    The Training set and the test set is joined so that by low rank matrix
    factorization and approximation, the unknown values for the training is
    imputed. We then take argmax of this row from the matrix and return top n
    entries for top n recommendations.
    */
    //Unique Numbers for testing.
    val testids = testpair.map(x=> x._1).distinct
    val kvpair = traindata.union(testpair)

    /* Since the data is parallely processed, look up tables need to be in
    all the segments/workers/nodes. Hence this data is broadcasted and persisited
    in all nodes using sc.broadcast */

    val uniqueCitingIDS = kvpair.map(x => x._1).distinct
    val uniqueCitedIDS = kvpair.map(x => x._2).distinct
    val CitingLookup = uniqueCitingIDS.zipWithIndex
    val CitedLookup = uniqueCitedIDS.zipWithIndex
    val CitingLookups = sc.broadcast(CitingLookup.map(x => x).collectAsMap)
    val CitedLookups = sc.broadcast(CitedLookup.map(x => x).collectAsMap)
    val CitingReverseLookup = sc.broadcast(CitingLookup.map(x => (x._2, x._1)).collectAsMap)
    val CitedReverseLookup = sc.broadcast(CitedLookup.map(x => (x._2, x._1)).collectAsMap)

    /**
     * Create Ratings class for the matrix factorization Model.
     * Since this is an implicit rating, a binary rating is chosen.
     */
    val ratings = kvpair.map(x => Rating(CitingLookups.value(x._1).toInt,
      CitedLookups.value(x._2).toInt, 1.toDouble))

    /** Train an implicit recommendation model
    Remove the commands to call model selection function findBestModel
    */

    //val model = findBestModel(ratings)
    val model = ALS.trainImplicit(ratings, 25, 30, 0.1, 3000)
    //println("MSE",findMSE(ratings,model))


    /** Load the Similarity File for Method 1 calculation*/

    val similarityDS = docSimilarity.map { line =>
      val item = line.replace("[", "").replace("]", "").split(",")
      (item(0), Array(item(1), item(2), item(3), item(4), item(5)))
    }

    /** Load the reverse lookup for paper ids */

    val broadcastedlookup = docFile.map { line =>
      val content = line.split(",")
      (content(9), content(10))
    }.union(docTest.map { line =>
      val content = line.split(",")
      (content(9), content(10))}).collectAsMap

    println(broadcastedlookup.take(5))

    //Below code for using a similarity File
    /**
    val example=similarityDS.flatMap(item=>
    item._2.map{ids =>
    val id=CitingLookups.value(ids);
    val recommendations = model.recommendProducts(id.toInt, 4);
    recommendations.map(x => item._1+":"+ids+":"+CitedReverseLookup.value(x.product)+":"+x.rating+"\n")})
    val write=example.map(x=> x.mkString(""))
    val fileop=new File("/home/ganesh/Desktop/output.csv")
    val bw=new BufferedWriter(new FileWriter(fileop))
    write.foreach(bw.write)
    bw.close()
    */

    //Below code block is for using the training file.

    val localtestids=testids.map(x=>x).collect()
    val results=localtestids.map{ids=>
      val id=CitingLookups.value(ids);
      val recommendations = model.recommendProducts(id.toInt, 20);
      recommendations.map(x => ids+":"+CitedReverseLookup.value(x.product)+":"+x.rating+"\n")}

      //Print the reverse lookup of the ids for inspection.
      println("-------------------")
      results.flatMap{x=> x.map(y => broadcastedlookup(y.split(":")(1))+y.split(":")(0))}.foreach(println)

      //Write the results to the database.
      val write=results.map(x=> x.mkString(""))
      val fileop=new File("/home/ganesh/Desktop/output.csv")
      val bw=new BufferedWriter(new FileWriter(fileop))
      write.foreach(bw.write)
      bw.close()



  }

  def findBestModel(data : RDD[Rating]): MatrixFactorizationModel={
    /**
    Model selection parameters. For every possible search parameter given,
    search for the model with minimum MSE and return the model with least MSE
    */
    val rank=Range(25,35,5).toList
    //val rank=Array(45)
    //val lambda=Range.Double(0.1,5,0.2).toList
    val lambda=Array(0.1)
    val alpha=Range.Double(3000,4000,50).toList
    //val alpha=Array(95)
    val BestRMSEVal=100.0
    val BestModel: MatrixFactorizationModel = ALS.trainImplicit(data, 10 , 20, 0.01, 0.01)

    //Strip the training data off rating to predict
    val strippedrating=data.map{case Rating(user,product,rating) => (user,product)}
    val bestParameters=""

    for (r <-rank){
      for (l <- lambda){
        for (a <-alpha){
          println(" "+r+" "+l+" "+a)
          val model = ALS.trainImplicit(data, r, 15, l, a)
          val predicted = model.predict(strippedrating).map(x => x.rating)
          val mse=predicted.map{x=> val error=1-x; error*error }.mean()
          println(mse)
          if (mse < BestRMSEVal){
            val BestModel=model
            val bestParameters=" "+r+" "+l+" "+a
          }
        }
      }
    }
    print(("The Best MSE"),BestRMSEVal)
    return(BestModel)
  }

  def findMSE(data : RDD[Rating], model : MatrixFactorizationModel): Double ={
    /** Given a model and data, return MSE. Only implicit feedback is assumed.
    The max is 1 and the min is zero */

    val strippedrating=data.map{case Rating(user,product,rating) => (user,product)}
    val predicted = model.predict(strippedrating).map(x => x.rating)
    val mse=predicted.map{x=> val error=1-x; error*error }.mean()
    return(mse)
  }
}
//Run the Recommender
Recommender.start()
//Once the execution is complete, exit the code.
System.exit(0)
