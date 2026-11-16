
pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "/usr/lib/jvm/java-21-openjdk-amd64/bin:${env.PATH}"
        DOCKERHUB_USER = 'suresh2830'
        IMAGE_NAME = 'ecommerce-app'
        CONTAINER_NAME = 'ecommerce-container'
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    stages {

        // Stage 1: Checkout
        stage('Checkout') {
            steps {
                checkout scm
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
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
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
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                    docker images ${IMAGE_NAME}
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

                        docker tag ${IMAGE_NAME}:${BUILD_NUMBER} \
                            ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                        echo "$DOCKERHUB_TOKEN" | docker login \
                            -u "$DOCKERHUB_USER" --password-stdin

                        docker push ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                        docker logout
                    '''
                }
            }
        }

        // Stage 8: Docker Deploy
        stage('Docker Deploy') {
            steps {
                sh '''
                    docker pull ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                    docker rm -f ${CONTAINER_NAME} || true

                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        --restart unless-stopped \
                        -p 8080:8080 \
                        ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                    echo "Deployment completed"

                    docker ps -a --filter name=${CONTAINER_NAME}
                '''
            }
        }

        // Stage 9: Deployment Health Check
        stage('Deployment Health Check') {
            steps {
                sh '''
                    echo "Checking application health..."

                    for i in $(seq 1 10); do

                        if curl -fsS http://localhost:8080/; then
                            echo "Application is healthy!"
                            exit 0
                        fi

                        echo "Waiting for application..."
                        sleep 5
                    done

                    echo "Application health check failed"

                    docker logs ${CONTAINER_NAME}

                    exit 1
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
