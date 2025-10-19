pipeline {
    agent any

    tools {
        jdk 'Java 17'
        maven 'Maven 3'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/R0h-a-a-n/Taskmanager.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Manual deploy step for now (e.g. run JAR locally or copy to EC2 later)'
            }
        }
    }
}
