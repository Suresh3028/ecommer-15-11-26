
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
                echo 'Source code checked out from GitHub'
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
                sh 'mvn -B clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
    stage('Docker Build') {
    steps {
        sh '''
            docker build -t ecommerce-app:v1 .
            docker images ecommerce-app
        '''
    }
}

    post {
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed. Check the console output.'
        }
    }
}
