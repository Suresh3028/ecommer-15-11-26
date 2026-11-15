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
