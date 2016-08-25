package com.jnj.cde.rollup.util

import java.text.SimpleDateFormat
import java.util.Random
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import org.apache.spark.rdd.RDD
import java.io.File
import util.Try
import scala.reflect.io.Path;
import org.apache.log4j.Logger;

object Utilities {
  final val LOG = Logger.getLogger("Utilities");
  val tz = TimeZone.getTimeZone("IST");
  val SLIDE_INTERVAL = 15
  def getDateTime(): String = {
    val pattern = "dd-MM-yyyy-hh:mm:ss"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getTime(): String = {
    val pattern = "dd-MM-yyyy-hh-mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getDay(): String = {
    val pattern = "dd"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getDate(): String = {
    val pattern = "dd-MM-yyyy"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getDate(pattern:String): String = {
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getHour(): String = {
    val pattern = "HH"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getPrevHour(): String = {
    val pattern = "HH"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - 3600 * 1000);
    return format.format(date)
  }
  def getNextHour(): String = {
    val pattern = "HH"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() + 3600 * 1000);
    return format.format(date)
  }
  def getMinutes(): String = {
    val pattern = "mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }
  def getPrevMnts(): String = {
    val pattern = "mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - SLIDE_INTERVAL * 60000);
    return format.format(date)
  }
  def getPrev_15_Mnts(): String = {
    val pattern = "mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - 15 * 60000);
    return format.format(date)
  }
  def getPrev_30_Mnts(): String = {
    val pattern = "mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - 30 * 60000);
    return format.format(date)
  }
  def getPrev_45_Mnts(): String = {
    val pattern = "mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - 45 * 60000);
    return format.format(date)
  }
  def getPrevDate(): String = {
    val pattern = "dd-MM-yyyy"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    val date = new Date()
    date.setTime(date.getTime() - 1000 * 60 * 60 * 24);
    return format.format(date)
  }
  def getDateFolder(): String = {
    val pattern = "dd-MM-yyyy_HH_mm"
    val format = new SimpleDateFormat(pattern);
    format.setTimeZone(tz)
    return format.format(new Date())
  }

  
  
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
  
  private def checkForComma(value: Any): String = {
    if (!"".equals(value) && value.toString().indexOf(',') > -1) {
      val newvalue = "\"" + value + "\"";
      return newvalue;
    }
    return value.toString();
  }

  def mv(oldName: String, newName: String) = {
    Try(new File(oldName).renameTo(new File(newName))).getOrElse(false)
  }
  
 private def dropHeader(data: RDD[String]): RDD[String] = {
      data.mapPartitionsWithIndex((idx, lines) => {
        if (idx == 0) {
          lines.drop(1)
        }
        lines
      })
    }
  def deleteDirectory(directory: String): Boolean = {
    var isDeleted = false;
    LOG.info("Starting to delete the "+directory);
    val path = Path.apply(directory)    
    try {
      isDeleted = path.deleteRecursively();//deleteRecursively(continueOnFailure = false)
      LOG.info("Deleted the "+directory+" directory successfully");
    } catch {
      case e: Exception => 
        LOG.error("Error while deleting the directory: ",e);
        throw e;
    }
    return false;
  }
}