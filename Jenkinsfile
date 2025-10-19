pipeline {
    agent any

    tools {
        jdk 'Java 17'
        maven 'Maven 3'
    }

    environment {
        APP_NAME = 'taskmanager'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/R0h-a-a-n/Taskmanager.git'
            }
        }

        stage('Build') {
            steps {
                withCredentials([string(credentialsId: 'rds-password', variable: 'DB_PASSWORD')]) {
                    sh 'mvn clean package -DskipTests -Dspring.datasource.password=$DB_PASSWORD'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Manual deploy step for now (e.g. run JAR locally or on EC2)'
            }
        }
    }

    post {
        success {
            echo "Build successful for ${APP_NAME}"
        }
        failure {
            echo "Build failed for ${APP_NAME}"
        }
    }
}
