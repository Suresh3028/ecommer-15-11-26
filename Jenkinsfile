
pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        // Stage 1: Checkout
        stage('Checkout') {
            steps {
                echo 'Source code checked out from GitHub'
            }
        }

        // Stage 2: Environment Check
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
                    docker --version
                '''
            }
        }

        // Stage 3: Compile
        stage('Compile') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        // Stage 4: Unit Testing
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

        // Stage 5: Package
        stage('Package') {
            steps {
                sh 'mvn -B package'
                archiveArtifacts artifacts: 'target/*.jar',
                    fingerprint: true
            }
        }

        // Stage 6: Docker Build
        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ecommerce-app:${BUILD_NUMBER} .
                    docker images ecommerce-app
                '''
            }
        }

        // Stage 7: Docker Hub Push
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    sh '''
                        set +x

                        docker tag ecommerce-app:${BUILD_NUMBER} \
                            ${DOCKERHUB_USER}/ecommerce-app:${BUILD_NUMBER}

                        echo "$DOCKERHUB_TOKEN" | docker login \
                            -u "$DOCKERHUB_USER" --password-stdin

                        docker push ${DOCKERHUB_USER}/ecommerce-app:${BUILD_NUMBER}

                        docker logout
                    '''
                }
            }
        }

        // Stage 8: Docker Deploy
        stage('Docker Deploy') {
            steps {
                sh '''
                    docker rm -f ecommerce-container || true

                    docker run --name ecommerce-container \
                        ${DOCKERHUB_USER:-suresh2830}/ecommerce-app:${BUILD_NUMBER}

                    echo "Container deployment completed"

                    docker ps -a --filter name=ecommerce-container

                    docker logs ecommerce-container
                '''
            }
        }
    }

    post {
        success {
            echo 'CI/CD Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check Console Output.'
        }

        always {
            echo 'Pipeline execution finished.'
        }
    }
}
