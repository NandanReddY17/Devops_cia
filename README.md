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
