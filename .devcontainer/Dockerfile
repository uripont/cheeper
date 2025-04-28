FROM eclipse-temurin:21-jdk-jammy

# install Maven (Java project management) + git/curl/unzip
USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends git maven curl unzip \
 && rm -rf /var/lib/apt/lists/*

# download & unpack Tomcat 11
ENV CATALINA_VERSION=11.0.0 \
    CATALINA_HOME=/opt/tomcat \
    PATH=/opt/tomcat/bin:$PATH
RUN curl -fsSL \
    https://archive.apache.org/dist/tomcat/tomcat-11/v${CATALINA_VERSION}/bin/apache-tomcat-${CATALINA_VERSION}.zip \
    -o /tmp/tomcat.zip \
 && unzip /tmp/tomcat.zip -d /opt \
 && mv /opt/apache-tomcat-${CATALINA_VERSION} $CATALINA_HOME \
 && rm /tmp/tomcat.zip \
 && chmod +x $CATALINA_HOME/bin/*.sh \
 && chown -R root:root $CATALINA_HOME

# (Re)create the 'vscode' user with UID 1000 and GID 1000 (default for VS Code)
RUN groupadd -g 1000 vscode \
 && useradd -m -u 1000 -g 1000 -s /bin/bash vscode \
 # Give that user access to Tomcat and the workspace
 && chown -R vscode:vscode $CATALINA_HOME \
 && mkdir -p /cheeper \
 && chown -R vscode:vscode /cheeper

# Switch into that user for all subsequent operations
USER vscode
WORKDIR /cheeper