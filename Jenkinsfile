pipeline {
    agent any

    tools {
        jdk 'Java 17'
        maven 'Maven 3'
    }

    environment {
        APP_NAME = 'taskmanager'
        DOCKER_IMAGE = 'r0haan/taskmanager'
        BLUE_DEPLOYMENT = 'deployment-blue.yaml'
        GREEN_DEPLOYMENT = 'deployment-green.yaml'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/R0h-a-a-n/Taskmanager.git'
            }
        }

        stage('Build JAR') {
            steps {
                withCredentials([string(credentialsId: 'rds-password', variable: 'DB_PASSWORD')]) {
                    sh '''
                        echo "Building Spring Boot JAR..."
                        mvn clean package -DskipTests -Dspring.datasource.password=$DB_PASSWORD
                    '''
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def imageTag = "blue"
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        sh """
                            echo "Copying JAR into Docker build context..."
                            cp target/taskmanager-0.0.1-SNAPSHOT.jar .

                            echo "Building Docker image ${DOCKER_IMAGE}:${imageTag}"
                            docker build -t ${DOCKER_IMAGE}:${imageTag} .

                            echo "Pushing image to Docker Hub..."
                            docker push ${DOCKER_IMAGE}:${imageTag}
                        """
                    }
                }
            }
        }

        stage('Verify K8s Connection') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG')]) {
                    sh '''
                        echo "Verifying Kubernetes connection..."
                        export KUBECONFIG=$KUBECONFIG
                        kubectl cluster-info
                        kubectl get nodes
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes - Blue') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG')]) {
                    sh '''
                        echo "Deploying Blue version..."
                        export KUBECONFIG=$KUBECONFIG
                        kubectl apply -f k8s/service.yaml
                        kubectl apply -f k8s/${BLUE_DEPLOYMENT}

                        echo "Waiting for Blue pods to be ready..."
                        kubectl rollout status deployment/taskmanager-blue --timeout=120s
                    '''
                }
            }
        }

        stage('Blue-Green Switch') {
            steps {
                script {
                    input message: "Blue deployment successful. Proceed to deploy Green version?"

                    withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG')]) {
                        sh '''
                            export KUBECONFIG=$KUBECONFIG
                            echo "Deploying Green version..."
                            kubectl apply -f k8s/${GREEN_DEPLOYMENT}

                            echo "Waiting for Green pods to be ready..."
                            kubectl rollout status deployment/taskmanager-green --timeout=120s

                            echo "Switching service traffic from Blue → Green..."
                            kubectl patch service taskmanager-service -p '{"spec":{"selector":{"app":"taskmanager","version":"green"}}}'
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Blue-Green deployment completed successfully for ${APP_NAME}"
        }
        failure {
            echo "Pipeline failed for ${APP_NAME}"
        }
    }
}
