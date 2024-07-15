def call(Map config) {
    def result=null
    container("envsubst") {
        //TODO: Iterate over all config.build.X.images and expose them as ebv vars
        env.MAVEN_IMAGE = config.build.maven.image
        //env.MAVEN_IMAGE="maven:3-amazoncorretto-17"
        def agentYaml=libraryResource("podtemplates/podTemplate-envsubst-images.yaml")
        sh '''
            ls -la 
            cat agent.yaml
            echo ${agentYaml} |envsubst > ${config.dynamicPodTemplateFile}
            sed -i '1d' ${config.dynamicPodTemplateFile} #workartund to remove "---"
            cat ${config.dynamicPodTemplateFile}
        '''
        //writeYaml file: config.dynamicPodTemplateFile , data: libraryResource("podtemplates/podTemplate-envsubst-images.yaml")
        result = readYaml file: "tmp-podagent.yaml"
        sh "echo $agentYaml"
    }
    return result
}
