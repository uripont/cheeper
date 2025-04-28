# Cheeper WebApp

## Dev Container setup for Java/Tomcat/Maven

### Motivation
We spent a *non-negligible* amount of time setting up with local Java/Tomcat/Maven setups and interiorizing IDE-specific GUI flows. We wanted to let every teammate use **any IDE** (VS Code, IntelliJ,...) against a single, reproducible environment, with the minimum amount of local installs.

### Dev Container approach
We settled on using a devcontainer, which is an open standard for defining a development environment in a Docker container. We just have to set up a single Dockerfile, and then we can use it in any IDE that supports devcontainers: they can "mount" the local filesystem into the container and integrate the whole IDE experience, providing a transparent experience as if it was running locally on the host machine.

For our setup (web development using Java), we created a Dev Container that bundles:
- **OpenJDK 21**  
- **Maven CLI**  
- **Tomcat 11**  

Everything lives in a small Dockerfile and a config file under `.devcontainer/`. If using VSCode and the Dev Containers extension (recommended approach), the IDE prompts “Reopens in Container” and instantly has the exact JDK/Maven/Tomcat stack we tested.

### Getting Started

1. **Install Docker** and the **VS Code Dev Containers** extension (or the desired IDE’s equivalent).  
2. **Clone the repository and open it in VS Code**.
3. **Open the Command Palette** (Ctrl+Shift+P) and select **Dev Containers: Reopen in Container**.
4. **Wait for the container to build** (it may take a few minutes the first time).

### Running the Application

Can easily be done using the VSCode build task we created for it, that can be run using the Command Palette (Ctrl+Shift+P) and selecting **Tasks: Run Build Task**, or with the shortcut **Ctrl+Shift+B** (Windows/Linux)/**Cmd+Shift+B** (Mac). It automatically builds the project into a `.war` file and runs it on the Tomcat server. The application will be available at `http://localhost:8080/`, which is automatically opened in the browser on your local machine.