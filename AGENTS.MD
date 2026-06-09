---
name: Web Dev Minimal Environment
description: Strict binary whitelist for Spring Boot, Python, and React execution.
---

# Allowed Terminal Tools

The agent is strictly restricted to executing only the following whitelisted system binaries and commands:

* **Java & Spring Boot**: `java`, `javac`, `mvn`, `gradle`, `./gradlew`, `jar`
* **Python**: `python3`, `python`, `pip3`, `pip`, `pytest`, `venv`, `virtualenv`
* **React & Frontend**: `node`, `npm`, `yarn`, `pnpm`, `npx`
* **Containers & Databases**: `docker`, `docker-compose`, `psql`, `mysql`, `sqlite3`, `redis-cli`
* **Testing & APIs**: `curl`, `wget`, `jq`, `playwright`, `cypress`
* **System & Code Utilities**: `git`, `grep`, `rg`, `find`, `lsof`, `kill`, `sed`, `awk`, `gh`

# Execution Rules
* Do not attempt to install or execute any terminal tools outside of this list.
* Always run the appropriate test suite (`mvn test`, `pytest`, or `npm test`) to verify changes before completing a task.

# Formatting requirements:
* Use google-java-format for all Java code.
* After modifying Java files, run spotlessApply.
* Generated code must pass formatting checks.