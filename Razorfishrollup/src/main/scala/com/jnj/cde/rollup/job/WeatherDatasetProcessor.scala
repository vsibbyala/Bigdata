package com.jnj.cde.rollup.job

import scala.reflect.runtime.universe
import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import au.com.bytecode.opencsv.CSVParser
import org.apache.spark.rdd.RDD
import java.io.File
import util.Try
import com.jnj.cde.rollup.mail.RollUpEmailNotify
import org.apache.log4j.Logger
import com.jnj.cde.rollup.aws.AWSS3FileUtilHelper;
import com.jnj.cde.rollup.util.Utilities

object WeatherDatasetProcessor {
  final val LOG = Logger.getLogger("Weather Dataset Processor");
  case class weatherBean(station: String,station_name: String,elevation: Double,latitude: Double,longitude: Double,time: String,temperature: Double,pressure: Double,humidity: Int,tpcp: Int,emxt: Int,emnt: Int);
  case class weatherBeanOut(location: String,position: String,localTime: String,conditions: String,temperature: Double, pressure: Double, humidity: Double);
  var parser = new CSVParser(',');
  var inputFileDir : String= "";
  var outputPath: String = "";
  
  var environment: String = "DEV";
  var fromMailId: String = "venkat.sibbyala@gmail.com";
  var toMailIds: String = "venkat.sibbyala@gmail.com";
  def scalaFormatter(x: Double) = "%1.4f".format(x);
  var inputFiles: String = "";
  val conf = new SparkConf().setAppName("WeatherDatasetProcessor").setMaster("local[2]")
  val sc = new SparkContext(conf)
  def main(args: Array[String]) {
    val isEmailEnabled = sc.getConf.get("spark.isEmailEnabled");
    try{
        LOG.info("Weather Dataset Processor is started")
        val startTime = System.currentTimeMillis();
        val timeString : String = Utilities.getDateTime()
        val sqlContext = new org.apache.spark.sql.SQLContext(sc)
        import sqlContext.implicits._
         val datePattern = sc.getConf.get("spark.dateFormat");
        val date = Utilities.getDate(datePattern);
        environment = sc.getConf.get("spark.environment");
        fromMailId = sc.getConf.get("spark.fromMailId");
        toMailIds = sc.getConf.get("spark.toMailIds");
        val inputFilepath = sc.getConf.get("spark.inputFilePath");
        outputPath = sc.getConf.get("spark.outputPath");
        val outputFileName = sc.getConf.get("spark.outputFileName");
       var finalMailBody = "Hello Team,<br><br>The Weather dataset process is completed successfully<br><br>";
       Utilities.deleteDirectory(outputPath+date)
       // Get the first line i.e. header line
       val objDataBean = sc.textFile("file:"+inputFilepath)
        val firstRow = objDataBean.first()+"\r";
        // Create a RDD for Header row
        val headerRDD: RDD[String] =  sc.parallelize(Array("Location|Position|Local|Time|Conditions|Temperature|Pressure|Humidity"))
    
        
        // Drop the Header which is not required for processing
        val dropHeaderRDD = dropHeader(objDataBean)
    
        //converting dataBean object to Data Frame.
        val filterEmptyRowsRDD = dropHeaderRDD.filter( line =>  isNonEmptyLine(line));
        val objDataBeanDF = filterEmptyRowsRDD.map(getTokens(_))
          .map(x => weatherBean(x(0), x(1), checkNullForDouble(x(2)), checkNullForDouble(x(3)), checkNullForDouble(x(4)), x(5), checkNullForDouble(x(6)), checkNullForDouble(x(7)), checkNullForInt(x(8)), checkNullForInt(x(9)), checkNullForInt(x(10)), checkNullForInt(x(11)))).toDF()
        objDataBeanDF.registerTempTable("dataBean");
        val finalDF = objDataBeanDF.map(x => weatherBeanOut(x.getString(1), x.getDouble(3)+","+x.getDouble(4)+","+x.getDouble(2), x.getString(5), getWeatherCondition(x.getDouble(6),x.getDouble(7), x.getInt(8)),x.getDouble(6),x.getDouble(7), x.getInt(8))).toDF()
        val finalRDD = finalDF.rdd.map(row => row(0) + "|" + row(1) + "|" + row(2) + "|" + row(3) + "|" + row(4) + "|" + row(5) + "|" + row(6))
        headerRDD.union(finalRDD).coalesce(1, true).saveAsTextFile("file:" + outputPath+date+"/");
        Utilities.mv(outputPath +date+"/part-00000", outputPath+outputFileName);
        Utilities.deleteDirectory(outputPath+date)
        if(null != isEmailEnabled && "Y".equals(isEmailEnabled))
        {
          RollUpEmailNotify.createMessageAndSendMail(finalMailBody+"Regards,<br>Venkat Team.","Weather dataset process is Success in "+environment+" environment",fromMailId,toMailIds);
        }
        LOG.info("======================================= Job Success ========================================");
    }
    catch {
         case ex: Exception => {
           if(null != isEmailEnabled && "Y".equals(isEmailEnabled))
            {
             RollUpEmailNotify.createMessageAndSendMail("Hello Team,<br><br>Exception While processing the Weather dataset process, please check the /home/logs/Rollup.log for more details.<br><br>Regards,<br>Venkat Team.","Weather dataset process is FAILED in "+environment+" environment",fromMailId,toMailIds);
            }
             LOG.error("Exception in processing the Rollup job:",ex);
         }
    }finally{

    }
  } // end of main
  private def checkNullForInt(value: String): Integer = {
    if (!"".equals(value)) {
      return value.toInt;
    }
    return 0;
  }
  private def checkNullForDouble(value: String): Double = {
    if (!"".equals(value)) {
      val value1 : String = value.replaceAll(",", "")
      return value1.toDouble;
    }
    return 0.0;
  }

  private def getTokens(value: String): Array[String] = {
    if (!"".equals(value)) {
      var tokens: Array[String] = parser.parseLine(value);
      if (tokens.length != 16) {
        //beforeCountVal.add(1)
        LOG.debug("Line = " + value + ",Token Length= " + tokens.length)
      }
      return tokens;
    }
    return null;
  }

  private def isNonEmptyLine(value: String): Boolean = {
    if ("".equals(value)) {
      return false;
    } else {
      if (value.length() > 20) {
        return true;
      } else return false;
    }
    return true;
  }

 private def dropHeader(data: RDD[String]): RDD[String] = {
      data.mapPartitionsWithIndex((idx, lines) => {
        if (idx == 0) {
          lines.drop(1)
        }
        lines
      })
    }

private def getWeatherCondition(temp: Double,pre: Double,hum:Int): String = {
    var conditio="";
    if(temp < 0.0 && pre>=500.0 && pre<=1000.0 && hum>=15 && hum <=60){
      conditio="Snow";
    }
    else if(temp >= 0.0 && temp <= 20.0 && pre>1000.0 && pre<=1200.0 && hum>60 && hum <=100){
      conditio="Rain";
    }else if(temp >20.0 && temp <=50.0 && pre>1200.0 && pre<=1500.0 && hum<15){
      conditio="Sunny";
    }
    else{
      conditio="Hot";
    }
    return conditio;
  }
}