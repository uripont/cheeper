FROM eclipse-temurin:21-jdk-jammy

# install Maven (Java project management) + git/curl/unzip
USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends git maven curl unzip \
 && rm -rf /var/lib/apt/lists/*

# (Re)create the 'vscode' user with UID 1000 and GID 1000 (default for VS Code)
RUN groupadd -g 1000 vscode \
 && useradd -m -u 1000 -g 1000 -s /bin/bash vscode

# Switch into that user for all subsequent operations
RUN mkdir -p /cheeper \
&& chown -R vscode:vscode /cheeper
USER vscode
WORKDIR /cheeper