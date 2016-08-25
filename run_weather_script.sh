#!/bin/bash
#source /home/vsibbyal/Rollup/rollup.env
export MAVEN_HOME=/home/vsibbyal/Softwares/apache-maven-3.3.9
export PATH=$MAVEN_HOME/bin:$PATH
export JAVA_HOME=/home/vsibbyal/Softwares/jdk1.8.0_40
export PATH=$JAVA_HOME/bin:$PATH
export SCALA_HOME=/home/vsibbyal/Softwares/scala-2.10.4
export PATH=$SCALA_HOME/bin:$PATH
export SPARK_HOME=/home/vsibbyal/Softwares/spark-1.5.2-bin-hadoop2.6
export PATH=$SPARK_HOME/bin:$PATH
#source /home/vsibbyal/Rollup/rollup.env
ROLLUP_CONF_DIR=/home/vsibbyal/Razorfishrollup/src/main/resources
DEP_JAR_DIR=/home/vsibbyal/RollupDemendentJars
ROLLUP_JAR_DIR=/home/vsibbyal/Razorfishrollup/target
echo $PATH
#export CLASSPATH=$JAVA_HOME/jre/lib:/home/vsibbyal/Softwares/RollupDemendentJars/*:$CLASSPATH
spark-submit --properties-file $ROLLUP_CONF_DIR/weather.conf --class com.jnj.cde.rollup.job.WeatherDatasetProcessor --jars $DEP_JAR_DIR/scala-library-2.10.4.jar,$DEP_JAR_DIR/guava-16.0.1.jar,$DEP_JAR_DIR/jsr166e-1.1.0.jar,$DEP_JAR_DIR/metrics-core-2.2.0.jar,$DEP_JAR_DIR/hadoop-aws-2.6.0.jar,$DEP_JAR_DIR/hadoop-auth-2.6.0.jar,$DEP_JAR_DIR/aws-java-sdk-1.7.4.jar --master local[2] $ROLLUP_JAR_DIR/Razorfishrollup-0.0.1-SNAPSHOT.jar
