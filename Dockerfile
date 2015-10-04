FROM tomcat:8.0.20-jre7
MAINTAINER Olivier Berthonneau <olivier.berthonneau@nanocloud.com>

RUN apt-get update
RUN apt-get -y install maven openjdk-7-jdk

WORKDIR "/opt/build"
