pipeline {
    agent any
    environment {
//        JAVA_HOME = "/home/java/jdk-11"
        JAVA_HOME = "/home/java/jdk-17.0.5"
        MAVEN_HOME = "/home/apache-maven-3.8.6"
    }
    stages {
        stage("编译Maven工程") {
            steps {
                script {
                    // https://www.jenkins.io/doc/pipeline/examples/
                    withEnv(["JAVA_HOME=${JAVA_HOME}", "PATH+MAVEN=${MAVEN_HOME}/bin:${JAVA_HOME}/bin"]) {
                        echo "=================================================="
                        sh "mvn -version"
                        echo "=================================================="
                        sh "clean compile deploy -T 8C -DskipTests=true -B -e -U -s /root/.m2/settings.xml"
                    }
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}