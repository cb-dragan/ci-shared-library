def call(Map configDefaults) {
    def config=null
    def agentYaml=null
    pipeline {
        agent none
        stages {
            stage('Init') {
                agent {
                    kubernetes {
                        yaml libraryResource("podtemplates/podTemplate-init.yaml")
                    }
                }
                steps {
                    script {
                        config = init  configDefaults
                        env.MAVEN_IMAGE=config.build.maven.image
                        //env.MAVEN_IMAGE="maven:3-amazoncorretto-17"
                        writeYaml file: 'agent.yaml', data:  libraryResource("podtemplates/podTemplate-envsubt-images.yaml")
                        sh "ls -la && envsubst < agent.yaml > tmp-podagent.yaml"
                        agentYaml=readYaml file: "tmp-podagent.yaml"
                    }
                }
            }
            stage('CI') {
                //           parallel {
                //                stage ("runci"){

                agent {
                    kubernetes {
                        yaml libraryResource("podtemplates/${config.build.maven.podyaml}")
                    }
                }
                stages {
                    stage("build") {
                        steps {
                            sh "echo build "
                        }
                    }
                    stage("deploy") {
                        steps {
                            sh "echo deploy "
                        }
                    }
                    //                  }
//                }
                }
            }
            stage('CI-image-envsubt') {
                //           parallel {
                //                stage ("runci"){

                agent {
                    kubernetes {
                        //yaml libraryResource("podtemplates/podTemplate-envsubt-images.yaml")
                        yaml agentYaml
                    }
                }
                stages {
                    stage("build") {
                        steps {
                            sh "echo build "
                        }
                    }
                    stage("deploy") {
                        steps {
                            sh "echo deploy "
                        }
                    }
                    //                  }
//                }
                }
            }
        }
    }
}