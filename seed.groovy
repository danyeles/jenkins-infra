pipeline {
    agent any

    stages {
        stage('Generate Jobs') {
            steps {
                script {
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
        }
    }
}
