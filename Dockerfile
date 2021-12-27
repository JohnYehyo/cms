FROM openjdk:8-jdk-alpine

MAINTAINER JohnYehyo <johnyehyo@hotmail.com>

LABEL description="内容管理系统"

ADD rjsoft-web.jar /opt/rjsoft/cms-server.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/opt/rjsoft/cms-server.jar"]
