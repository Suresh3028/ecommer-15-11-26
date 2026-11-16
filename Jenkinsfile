
pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
          JAVA17_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
          PATH = "${JAVA17_HOME}/bin:${env.PATH}"
          IMAGE_NAME = 'ecommerce-app'
          DOCKERHUB_USER = 'suresh2830'
    }

    stages {

        // Stage 1: GitHub Checkout
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: 'github-ssh-key',
                    url: 'git@github.com:Suresh3028/ecommer-15-11-26.git'
            }
        }

        // Stage 2: Environment Check
        stage('Environment Check') {
            steps {
                sh '''
                    echo "Jenkins Java:"
                    java -version

                    echo "Maven Java:"
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
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        // Stage 6: Docker Build
        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                    docker tag ${IMAGE_NAME}:${BUILD_NUMBER} \
                        ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}
                '''
            }
        }

        // Stage 7: Docker Hub Push
        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        echo "$DH_TOKEN" | docker login \
                            -u "$DH_USER" --password-stdin

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

                    docker rm -f ecommerce-container || true

                    docker run -d \
                        --name ecommerce-container \
                        -p 8080:8080 \
                        ${DOCKERHUB_USER}/${IMAGE_NAME}:${BUILD_NUMBER}

                    docker ps --filter name=ecommerce-container
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
