# Bigdata
This code create a toy simulation of the environment (taking into account things like atmosphere, topography, geography,
oceanography, or similar) that evolves over time. Then take measurements at various locations and times, and
have your program emit that data, as in the following:

Location Position Local Time Conditions Temperature Pressure Humidity
Sydney|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97
Melbourne|-37.83,144.98,7|2015-12-24T15:30:55Z|Snow|-5.3|998.4|55
Adelaide|-34.92,138.62,48|2016-01-03T12:35:37Z|Sunny|+39.4|1114.1|12

This is Maven project and it uses the spark, scala, java, mailing API, logging and hadoop

Steps for required softwares installations in Unix/Ubuntu.

Go to thedirectory where you want to install the softwares. Example: cd /home/vsibbyal/Softwares/ and use the following commands
Java 8:
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u40-b25/jdk-8u40-linux-x64.tar.gz"
tar xzf jdk-8u40-linux-x64.tar.gz
 
Scala 2.10.4:
wget http://www.scala-lang.org/files/archive/scala-2.10.4.tgz
tar xzf scala-2.10.4.tgz
 
Spark 1.5.2:
wget http://apache.claz.org/spark/spark-1.5.2/spark-1.5.2-bin-hadoop2.6.tgz
sudo tar xzf spark-1.5.2-bin-hadoop2.6.tgz

wget http://mirror.stjschools.org/public/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar xzf apache-maven-3.3.9-bin.tar.gz
tar xzf .tar.gz

Get the maven and install.


Set all the installed s/w in path as follows.

export MAVEN_HOME=/home/vsibbyal/Softwares/apache-maven-3.3.9
export PATH=$MAVEN_HOME/bin:$PATH

export MAVEN_HOME=/home/vsibbyal/Softwares/apache-maven-3.3.9
export JAVA_HOME=/home/vsibbyal/Softwares/jdk1.8.0_40
export SCALA_HOME=/home/vsibbyal/Softwares/scala-2.10.4
export SPARK_HOME=/home/vsibbyal/Softwares/spark-1.5.2-bin-hadoop2.6
export PATH=$JAVA_HOME/bin:$SCALA_HOME/bin:$SPARK_HOME/bin:$MAVEN_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/jre/lib:$CLASSPATH

Process Source files:
1.	WeatherDatasetProcessor.scala:  Main class to process the Weather requirement functionality.
2.	Utilities.scala: Scala utility functions for date format, file utils.
3.	RollUpEmailNotify.java: Email notification functionality.

Process Configuration Files:
1.	 weather.conf:  Describes the configuration parameters which will requires for weather process.   
Location: Razorfishrollup/src/main/resources/weather.conf

Key	Description		Discription and Example
spark.fromMailId	From mail id to send the QC Check notification.	cde@its.jnj.com
spark.toMailIds		to mail id to whom to send the QC Check notification.	DL-JANUS-CDEITGroup@its.jnj.com
spark.environment 	Environment	DEV
spark.inputFilePath source I/P directory from where the weather files to pick up the process 			/home/vsibbyal/Rollup/Weather/input/WeatherDataSet.csv
spark.outputPath 	destination directory where the rollup process places the processed files. /home/vsibbyal/Rollup/Weather/output/
spark.outputFileName Output file name	WatherOutcomeData.txt
spark.isEmailEnabled Mail functionality configuration flag(Y/N)	N
spark.dateFormat 	Date format of the incoming source directory.	yyyy-MM-dd

Process Script file:
1.	run_weather_script.sh:  Educates about setting up the required software in path/classpath, environment variables and sperk-submit job command.
Location: /run_rollup_script.sh

Variable															Description
export SCALA_HOME=/cdemnt/CDE/softwares/scala-2.10.4				Setting scala home
export PATH=$SCALA_HOME/bin:$PATH									Setting scala home in path
export SPARK_HOME=/cdemnt/CDE/softwares/spark-1.5.2-bin-hadoop2.6	Setting spark home
export PATH=$SPARK_HOME/bin:$PATH									Setting spark home in path
ROLLUP_CONF_DIR=/cdemnt/CDE/utility/config							Configuring  the rollup.conf  directory location
DEP_JAR_DIR=/cdemnt/CDE/softwares/RollupDemendentJars				Configuring  the job dependent jars  directory location
ROLLUP_JAR_DIR=/cdemnt/CDE/utility									Configuring  the rollup jar  directory location
	
If email functionality is required then set spark.isEmailEnabled to Y and change the mail.smtp.host and mail.smtp.port in RollUpEmailNotify.java
To run the job execute the following command where the script run_rollup_script.sh file is available.
sh run_rollup_script.sh
