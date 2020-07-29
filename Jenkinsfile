pipeline {
    environment {
        registry = 'archguard/document_app'
        registryCredential = 'DockerHubCredentials'
        dockerImage = ''
        dev_env = 'ag-dev'
        qa_env = 'ag-qa'
        uat_env = 'ag-uat'
        prod_env = 'ag-prod'
    }
    agent any
    stages {
        stage('Build') {
            steps {
                sh '''
                    ./gradlew clean build
                '''
            }
        }
        stage('Build Image') {
            steps{
                script {
                    dockerImage = docker.build(registry)
                    docker.withRegistry('https://registry.hub.docker.com', registryCredential ) {
                        dockerImage.push()
                    }
               }
            }
        }
       stage('Deploy') {
           steps {
                sh 'curl -sSL http://ec2-68-79-38-105.cn-northwest-1.compute.amazonaws.com.cn:8080/job/DeployFiles/lastSuccessfulBuild/artifact/docker-compose.yml --output docker-compose.yml'
                sh 'docker-compose -p archguard -f docker-compose.yml pull'
                sh 'docker-compose -p archguard -f docker-compose.yml up -d'
           }
       }
    }
}
