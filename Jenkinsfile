
pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        DOCKERHUB_USER = 'suresh2830'
        IMAGE_NAME = 'ecommerce-app'
        CONTAINER_NAME = 'ecommerce-container'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    stages {

        // Stage 1: Checkout
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Source code checked out successfully'
            }
        }

        // Stage 2: Environment Check
        stage('Environment Check') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    javac -version
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
                    docker build \
                        -t ${IMAGE_NAME}:${BUILD_NUMBER} .

                    docker images ${IMAGE_NAME}
                '''
            }
        }

        // Stage 7: Docker Hub Push
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'REGISTRY_USER',
                    passwordVariable: 'REGISTRY_TOKEN'
                )]) {
                    sh '''
                        set +x

                        docker tag \
                            ${IMAGE_NAME}:${BUILD_NUMBER} \
                            ${REGISTRY_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                        echo "$REGISTRY_TOKEN" | docker login \
                            -u "$REGISTRY_USER" \
                            --password-stdin

                        docker push \
                            ${REGISTRY_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                        docker logout
                    '''
                }
            }
        }

        // Stage 8: Docker Deploy
        stage('Docker Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'REGISTRY_USER',
                    passwordVariable: 'REGISTRY_TOKEN'
                )]) {
                    sh '''
                        set +x

                        echo "$REGISTRY_TOKEN" | docker login \
                            -u "$REGISTRY_USER" \
                            --password-stdin

                        docker pull \
                            ${REGISTRY_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                        docker logout
                    '''
                }

                sh '''
                    docker rm -f ${CONTAINER_NAME} || true

                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        --restart unless-stopped \
                        -p 8081:8080 \
                        ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                    sleep 5

                    if [ "$(docker inspect -f '{{.State.Running}}' ${CONTAINER_NAME})" != "true" ]; then
                        echo "Container failed to start"
                        docker logs ${CONTAINER_NAME}
                        exit 1
                    fi

                    echo "Container is running successfully"
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
                            echo "Application is healthy"
                            exit 0
                        fi

                        echo "Waiting for application..."
                        sleep 5
                    done

                    echo "Health check failed"
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
