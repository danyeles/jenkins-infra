====================================================================
README #3 — deploy-jobs (for all generated jobs)
====================================================================

Deployment Jobs — Usage Guide
This document explains how to use the auto‑generated deployment jobs such as:
- deploy-sonarr
- deploy-radarr
- deploy-bazarr
- and any others defined in apps.yaml
These jobs are created automatically by the seed pipeline in the jenkins-infra repository.

What These Jobs Do
Each deploy-* job:
- Loads the Jenkins Shared Library (my-shared-lib)
- Reads the YAML config for the app
- Executes a standardized Docker deployment workflow
The job supports three actions:

Deploy
Runs the container if it does not exist.

Update
- Pulls the latest image
- Stops and removes the existing container
- Recreates it with the same configuration

Stop and Run
- Stops the container
- Starts it again without updating the image

Job Parameters
ACTION
A dropdown with:
- Deploy
- Update
- Stop and Run

How to Run a Deployment Job
1. Open the job (e.g., deploy-sonarr)
2. Click Build with Parameters
3. Select the desired ACTION
4. Click Build

Where Configuration Comes From
Each job loads its configuration from the shared library:
```
jenkins-shared-library/resources/configs/<app>.yaml
```

This YAML file defines:
- Docker image
- Container name
- Ports
- Volumes
- Environment variables
- Restart policy
To change how an app is deployed, edit its YAML file and commit to the shared library repo.

Updating a Job After Config Changes
If you modify:
- apps.yaml (in jenkins-infra)
- Any YAML config (in jenkins-shared-library)
- The shared library pipeline logic
You must run the seed job again:
```
ArrSeed
```
This updates all generated jobs.

Troubleshooting
FileNotFoundException for YAML
The job is trying to load YAML from the workspace instead of the shared library.
Fix:
Use:
```
readYaml text: libraryResource(args.configFile)
```

Permission denied when running Docker
Ensure Jenkins user is in the Docker group:
```
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

Job not updating after changes
Run the seed job again.

Adding a New App
1. Add a YAML file under jenkins-shared-library/resources/configs/
2. Add an entry in apps.yaml in jenkins-infra
3. Run the seed job
4. A new deploy-<app> job will appear

Removing an App
1. Remove the entry from apps.yaml
2. Run the seed job
3. Delete the corresponding job manually in Jenkins
