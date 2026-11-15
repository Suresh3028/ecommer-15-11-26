
pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Environment Check') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }
stage('Environment Check') {
    steps {
        sh '''
            echo "JAVA_HOME=$JAVA_HOME"
            which java
            java -version
            which javac
            javac -version
            which mvn
            mvn -version
        '''
    }
}
        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'E-commerce CI pipeline successful!'
        }
        failure {
            echo 'Build failed. Check console output.'
        }
    }
}
