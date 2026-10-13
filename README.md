# Student Management CI/CD Lab Template

A small Java 17 + Spring Boot REST API with Maven tests, Docker packaging, and a Jenkins pipeline.

## Endpoints

- `GET /students` — list students
- `POST /students` — add a student
- `GET /students/{id}` — view one student

Example POST JSON:
```json
{"name":"Asha","course":"CSE"}
```

## 1. Run locally (Java 17 and Maven required)

```bash
mvn clean test
mvn package
java -jar target/student-management.jar
```

Test in another terminal:
```bash
curl http://localhost:8080/students
curl -X POST http://localhost:8080/students -H "Content-Type: application/json" -d "{\"name\":\"Asha\",\"course\":\"CSE\"}"
```

PowerShell users can use:
```powershell
Invoke-RestMethod http://localhost:8080/students
$body = @{name="Asha"; course="CSE"} | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/students -Method Post -ContentType "application/json" -Body $body
```

## 2. Docker

Docker Desktop / Docker Engine must be running:
```bash
docker build -t student-management:latest .
docker run --rm -p 8080:8080 --name student-management-app student-management:latest
```
Then open `http://localhost:8080/students`.

## 3. Push to GitHub

Create an empty GitHub repository, then from this project folder:
```bash
git init
git add .
git commit -m "Add Student Management application and CI/CD pipeline"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```

For a later change:
```bash
git add .
git commit -m "Add or update application feature"
git push
```

## 4. Jenkins setup

Prerequisites: Jenkins must be running with a compatible JDK, Maven, Git, Docker CLI/daemon, and `curl` available to the Jenkins agent. The agent must be allowed to run Docker commands. The Jenkins controller/agent and Docker daemon must share the expected port/network context for the smoke test.

1. Push this repository to GitHub.
2. In Jenkins, choose **New Item** → **Pipeline**.
3. Select **Pipeline script from SCM** → **Git**, and enter the repository URL and branch `*/main`.
4. Save and click **Build Now**.
5. Open the build to see Checkout, Maven Build and Test, Build Docker Image, Deploy Container, and Smoke Test stages.

If Jenkins is configured with a Maven tool instead of Maven on PATH, adjust the pipeline to use the configured tool. On Windows agents, replace `sh` steps with equivalent `bat` steps. A Docker daemon may not be available on hosted Jenkins; confirm the lab machine's setup.

## Lab checklist

- [ ] Git repository exists and has meaningful commits
- [ ] `mvn clean test` passes
- [ ] `mvn package` creates `target/student-management.jar`
- [ ] Docker image builds
- [ ] Container runs and `GET /students` responds
- [ ] Jenkinsfile is committed and pipeline stages are green
- [ ] Capture screenshots of Git commits, Maven tests, Docker image/container, and Jenkins stages

## Common failures

- `mvn` not recognized: Maven is not installed or not on PATH; use the IDE/terminal configured by the lab.
- Docker daemon error: start Docker Desktop/Engine and verify `docker info`.
- Port 8080 already in use: stop the old app/container or change the host port, e.g. `-p 8081:8080`.
- Jenkins `docker: not found` / permission denied: the Jenkins agent needs Docker CLI and daemon access; this is an environment issue, not a Java compilation error.
- Maven dependency download fails: check internet/proxy and retry.
- Jenkins checkout fails: verify repository URL, credentials for private repositories, and branch name.

## Important note

The application stores records in memory, so data resets when the process/container restarts. This is intentionally lightweight for a lab exercise.




# Student Management App — End-to-End CI/CD Lab Runbook (Windows PowerShell)

Use this file as your step-by-step checklist during the lab. Run commands from **PowerShell** unless a step explicitly says otherwise. Replace placeholders such as `YOUR_GITHUB_USERNAME` with your own values.

## What this project does

A small Java 17 + Spring Boot REST API with Maven build/tests, a Docker image/container, and a Jenkins pipeline.

- `GET /students` — list students
- `POST /students` — add a student
- `GET /students/{id}` — view one student
- Sample request body: `{"name":"Asha","course":"CSE"}`

---

## Part 1 — Open the project in the lab system

### Option A: The project is already on your USB / lab desktop

Open PowerShell and `cd` into the folder containing `pom.xml`, `Dockerfile`, and `Jenkinsfile`. Example:

```powershell
cd "$HOME\Desktop\devops-ci-cd-student-template"
Get-ChildItem
```

Make sure `pom.xml`, `Dockerfile`, `Jenkinsfile`, and `src` are visible.

### Option B: Clone your GitHub repository (recommended if you pushed it already)

Replace the URL with your actual GitHub repository URL:

```powershell
cd "$HOME\Desktop"
git clone https://github.com/YOUR_GITHUB_USERNAME/devops-ci-cd-lab.git
cd .\devops-ci-cd-lab
Get-ChildItem
```

If your repository has a different name, use that name in the `cd` command. If the repository is private, sign in when Git asks.

### Verify installed tools

Run these commands one by one:

```powershell
java -version
mvn -version
git --version
docker --version
docker info
```

Expected: Java 17, Maven, Git, and Docker versions appear. `docker info` should show server information. If `java`, `mvn`, or `docker` is “not recognized,” that tool is missing or not on PATH; tell the lab instructor before spending time debugging the application. Docker Desktop/Engine must be running for `docker info` to work.

---

## Part 2 — Build the Java application and run unit tests

Run these from the project root (the folder containing `pom.xml`):

```powershell
mvn clean test
```

Wait for `BUILD SUCCESS`. The test report is under `target\surefire-reports`.

Then package the application:

```powershell
mvn package
Get-ChildItem .\target
```

Confirm that this file exists:

```powershell
Test-Path .\target\student-management.jar
```

Expected output: `True`.

**Screenshot to capture:** PowerShell showing `mvn clean test` / `mvn package` ending with `BUILD SUCCESS` and the JAR file in `target`.

---

## Part 3 — Run the app locally and test its API

### Terminal 1: start the app

```powershell
java -jar .\target\student-management.jar
```

Leave this terminal open. Wait until Spring Boot says it has started and is listening on port 8080.

### Terminal 2: test the endpoints

Open a **second** PowerShell window. If needed, `cd` to the project folder again, then run:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/students' -Method Get
```

Add a student:

```powershell
$body = @{ name = 'Asha'; course = 'CSE' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/students' -Method Post -ContentType 'application/json' -Body $body
```

List students again:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/students' -Method Get
```

View student with ID 1:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/students/1' -Method Get
```

Stop the app using **Ctrl+C** in Terminal 1 before proceeding, so port 8080 is free.

**Screenshot to capture:** successful GET and POST requests in PowerShell.

---

## Part 4 — Build and run the Docker container

First confirm Docker is running:

```powershell
docker info
```

Build the image from the project root:

```powershell
docker build -t student-management:latest .
```

Confirm the image exists:

```powershell
docker images
```

Start the container:

```powershell
docker run -d --name student-management-app -p 8080:8080 student-management:latest
```

Check that it is running and inspect its logs:

```powershell
docker ps
docker logs student-management-app
```

Test the app from the host PowerShell:

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/students' -Method Get
```

Optionally add a record to verify POST works inside the container:

```powershell
$body = @{ name = 'Ravi'; course = 'IoT' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/students' -Method Post -ContentType 'application/json' -Body $body
```

Stop/remove the container before a future rerun (or before Jenkins deploys its own container):

```powershell
docker rm -f student-management-app
```

**Screenshot to capture:** `docker images`, `docker ps`, and a successful API request to `localhost:8080`.

---

## Part 5 — Push changes to GitHub (if needed)

Check the current state:

```powershell
git status
git remote -v
git log -5 --oneline
```

If this folder is already a Git repository with the correct remote, commit and push any changes:

```powershell
git add .
git commit -m "Prepare student management CI/CD lab"
git push
```

If Git says there is nothing to commit, that is okay. If `git remote -v` shows no remote and this folder is not connected to your GitHub repo, run the following (replace the URL):

```powershell
git init
git add .
git commit -m "Add Student Management application and CI/CD pipeline"
git branch -M main
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/devops-ci-cd-lab.git
git push -u origin main
```

If `origin` already exists but points to the wrong repo, do **not** add it again. Correct it with:

```powershell
git remote set-url origin https://github.com/YOUR_GITHUB_USERNAME/devops-ci-cd-lab.git
git push -u origin main
```

Open your GitHub repository in a browser and capture a screenshot showing the files and commit history.

---

## Part 6 — Configure and run Jenkins

### Before starting

Jenkins must be available in the lab environment. The Jenkins agent needs Git, Java 17, Maven, Docker CLI, and permission to use the Docker daemon. The agent must also be able to reach the running app on its own `localhost` for the smoke test.

### Create the pipeline job

1. Open the Jenkins URL provided by your instructor and sign in.
2. Click **New Item**.
3. Enter a job name such as `student-management-ci-cd`.
4. Select **Pipeline**, then click **OK**.
5. Under **Pipeline**, set **Definition** to **Pipeline script from SCM**.
6. Set **SCM** to **Git**.
7. Enter your repository URL, for example `https://github.com/YOUR_GITHUB_USERNAME/devops-ci-cd-lab.git`.
8. Set **Branch Specifier** to `*/main`.
9. For a **Linux Jenkins agent**, set **Script Path** to `Jenkinsfile`.
10. For a **Windows Jenkins agent**, set **Script Path** to `Jenkinsfile.windows` (included in this project).
11. Click **Save**, then **Build Now**.
12. Open the build number, then **Console Output**. Wait for the pipeline to finish.

The pipeline stages are Checkout, Maven Build and Test, Build Docker Image, Deploy Container, and Smoke Test. Capture a screenshot of the stage view showing successful stages and the console output showing `Finished: SUCCESS`.

### If the Jenkins job fails

- **Checkout fails:** check the repository URL, branch `main`, and credentials if the repository is private.
- **`mvn` not found:** Maven must be installed/configured on the Jenkins agent or selected in Jenkins tool configuration.
- **`docker` not found / access denied:** the Jenkins agent needs Docker CLI and access to a Docker daemon. This is often a lab configuration issue; ask the instructor/admin.
- **Port 8080 already allocated:** stop the locally running app/container before the Jenkins build. From PowerShell, run `docker rm -f student-management-app` if that container exists. If a Java app is still running, switch to its terminal and press Ctrl+C.
- **Windows says `sh` is not recognized:** use Script Path `Jenkinsfile.windows` for a Windows agent. Use `Jenkinsfile` for Linux agents.
- **Smoke test fails immediately:** check the container logs and whether the Jenkins agent can reach `http://localhost:8080/students`.

---

## Part 7 — Submission screenshot checklist

Capture these screenshots and save them where your instructor asks:

- [ ] GitHub repository files and commit history
- [ ] `mvn clean test` and `mvn package` ending in `BUILD SUCCESS`
- [ ] `target\student-management.jar` exists
- [ ] Docker image visible in `docker images`
- [ ] Running container visible in `docker ps`
- [ ] API responds from `http://localhost:8080/students`
- [ ] Jenkins pipeline stages / Console Output ending in `Finished: SUCCESS`

Suggested screenshot filenames: `01-github.png`, `02-maven-tests.png`, `03-jar.png`, `04-docker-image.png`, `05-container-api.png`, `06-jenkins-success.png`.

---

## Quick troubleshooting commands

```powershell
# Check versions
java -version
mvn -version
git --version
docker --version

# Check Docker daemon and containers
docker info
docker ps -a
docker images

# Inspect app container
docker logs student-management-app

# Free the app's container name/port
 docker rm -f student-management-app

# Check Git state and remote
git status
git remote -v
git log -5 --oneline
```

If Maven cannot download dependencies, check the lab's internet/proxy access and retry `mvn clean test`. If port 8080 is occupied by another required service, ask the instructor before changing it.

## Important notes

- The app stores students in memory only; data resets when the application/container restarts.
- Do not commit passwords, access tokens, or other credentials to GitHub.
- The provided `Jenkinsfile` uses Linux shell steps. `Jenkinsfile.windows` is the alternative for a Windows Jenkins agent.

