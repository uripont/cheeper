# Cheeper WebApp

## Dev Container setup for Java/Tomcat/Maven

### Motivation
We spent a *non-negligible* amount of time setting up with local Java/Tomcat/Maven setups and interiorizing IDE-specific GUI flows. We wanted to let every teammate use **any IDE** (VS Code, IntelliJ,...) against a single, reproducible environment, with the minimum amount of local installs.

### Architecture Overview
Our project uses Docker Compose to orchestrate three separate services. The first service, `dev`, is our development environment container. We settled on using a devcontainer, which is an open standard for defining a development environment in a Docker container. We just have to set up a single Dockerfile, and then we can use it in any IDE that supports devcontainers: they can "mount" the local filesystem into the container and integrate the whole IDE experience, providing a transparent experience as if it was running locally on the host machine. It includes OpenJDK 21, the Maven CLI, Git, and other essential devtools for our Java EE2 stack.

The second service, `tomcat`, runs the official Tomcat 11 server. We extend the base image with a small customization so that Tomcat rescans the deployment directory every second, allowing faster "hot-deploy" when a new WAR file is built. Deployments happen automatically: when a fresh `ROOT.war` is placed into a shared `webapps` folder, Tomcat detects the change and reloads the application. Note that there is a bit of edge-case handling using a container-bind mount directory (`tomcat-webapps`) to ensure that the `webapps` folder on the server is always empty before a new deployment, to trigger the reload.

The third service, `db`, runs MySQL 8 to host the application’s database. All credentials and configuration values are stored in a single `.env` file at the repository root for simplicity and consistency.

### Getting Started

1. **Install Docker** and the **VS Code Dev Containers** extension (or the desired IDE’s equivalent).  
2. **Clone the repository and open it in VS Code**.
3. **Open the Command Palette** (Ctrl+Shift+P) and select **Dev Containers: Reopen in Container**.
4. **Wait for the container to build** (it may take a few minutes the first time).

### Running the Application

Can easily be done using the VSCode build task we created for it, that can be run using the Command Palette (Ctrl+Shift+P) and selecting **Tasks: Run Build Task**, or with the shortcut **Ctrl+Shift+B** (Windows/Linux)/**Cmd+Shift+B** (Mac). It automatically builds the project into a `.war` file under `/target`, and runs it on the Tomcat server. The application will be available at `http://localhost:8080/` after a few seconds (enough for Tomcat to detect the new `.war` file, explode it into a folder, and start serving from it).