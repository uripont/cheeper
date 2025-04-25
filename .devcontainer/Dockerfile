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

# Clear out any JDK-global flags that VS Code’s base image may inject
ENV JAVA_TOOL_OPTIONS=""
ENV _JAVA_OPTIONS=""

# 1) Drop a setenv.sh in Tomcat to clear any unwanted JVM flags
RUN echo 'CATALINA_OPTS=""' > $CATALINA_HOME/bin/setenv.sh \
 && chmod +x $CATALINA_HOME/bin/setenv.sh

# 2) Give the vscode user ownership of the entire Tomcat directory, to be able to copy .war files (deploying)
RUN chown -R vscode:root $CATALINA_HOME

USER vscode
WORKDIR /cheeper