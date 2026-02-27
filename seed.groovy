node {
    stage('Generate Jobs') {

        def apps = readYaml file: 'apps.yaml'
        echo "APPS FOUND: ${apps.apps}"
        echo "RAW YAML:"
        sh "sed -n '1,200p' apps.yaml | sed -e 's/\t/[TAB]/g'"

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
