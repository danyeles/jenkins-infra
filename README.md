====================================================================
README #2 — jenkins-infra
====================================================================

Jenkins Infrastructure — Seed Job & App Manifest
This repository contains the seed pipeline and the manifest that defines which deployment jobs Jenkins should generate.

Purpose
This repo is the “brain” of your Jenkins automation. It contains:
- seed.groovy — the job generator
- apps.yaml — the list of apps to deploy
Running the seed job automatically creates or updates all deployment jobs.

Repository Structure
```
jenkins-infra/
  seed.groovy
  apps.yaml
```

apps.yaml Format
```
apps:
  - name: sonarr
    config: configs/sonarr.yaml

  - name: radarr
    config: configs/radarr.yaml
```

Each entry corresponds to a generated Jenkins job:
```
deploy-sonarr
deploy-radarr
```

seed.groovy (Scripted Pipeline)
```
node {
    stage('Generate Jobs') {

        def apps = readYaml file: 'apps.yaml'

        apps.apps.each { app ->

            jobDsl scriptText: """
                pipelineJob("deploy-${app.name}") {
                    definition {
                        cps {
                            script(\"\"\"
                                @Library('my-shared-lib') _
                                appPipeline(
                                    appName: '${app.name}',
                                    configFile: '${app.config}'
                                )
                            \"\"\".stripIndent())
                        }
                    }
                }
            """
        }
    }
}
```

Installing Jenkins (Ubuntu/Debian)
Add Jenkins repo
```
curl -fsSL https://pkg.jenkins.io/debian/jenkins.io.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
```

Install Jenkins
```
sudo apt update
sudo apt install openjdk-17-jdk jenkins -y
sudo systemctl enable jenkins
sudo systemctl start jenkins
```

Required Jenkins Plugins
- Pipeline
- Pipeline: Groovy
- Pipeline Utility Steps
- Job DSL
- Git plugin
- Credentials Binding
- Docker Pipeline

Jenkins Credentials Required
GitHub Token
- Kind: Username with password
- ID: github-token
- Username: your GitHub username
- Password: GitHub personal access token

Creating the Seed Job in Jenkins
- New Item → Pipeline
- Name: ArrSeed
- Pipeline script from SCM
- SCM: Git
- Repo: https://github.com/<your-user>/jenkins-infra.git
- Credentials: github-token
- Script Path: seed.groovy
- Save and run

You should see:
```
Added items:
    GeneratedJob{name='deploy-sonarr'}
```

Updating Jobs
Whenever you:
- Add a new app
- Change apps.yaml
- Update the shared library

Run the seed job again.
It will update all deploy-* jobs automatically.
