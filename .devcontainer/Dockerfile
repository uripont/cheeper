# Base off the VS Code–maintained Java 11 image
FROM mcr.microsoft.com/vscode/devcontainers/java:1-11

USER root

# Install Maven + curl/unzip for Tomcat
RUN apt-get update \
 && apt-get install -y --no-install-recommends maven curl unzip \
 && rm -rf /var/lib/apt/lists/*

# Download & unpack Tomcat 11
ENV CATALINA_VERSION=11.0.0
ENV CATALINA_HOME=/opt/tomcat
ENV PATH="$CATALINA_HOME/bin:$PATH"
RUN curl -fsSL \
    https://archive.apache.org/dist/tomcat/tomcat-11/v${CATALINA_VERSION}/bin/apache-tomcat-${CATALINA_VERSION}.zip \
    -o /tmp/tomcat.zip \
 && unzip /tmp/tomcat.zip -d /opt \
 && mv /opt/apache-tomcat-${CATALINA_VERSION} $CATALINA_HOME \
 && rm /tmp/tomcat.zip \
 && chmod +x $CATALINA_HOME/bin/*.sh

USER vscode
WORKDIR /workspace