pipeline {
    agent any

    environment {
        IMAGE_NAME = 'student-management:latest'
        CONTAINER_NAME = 'student-management-app'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build and Test') {
            steps {
                sh 'mvn -B clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${IMAGE_NAME} .'
            }
        }

        stage('Deploy Container') {
            steps {
                sh '''
                    docker rm -f ${CONTAINER_NAME} || true
                    docker run -d --name ${CONTAINER_NAME} -p 8080:8080 ${IMAGE_NAME}
                '''
            }
        }

        stage('Smoke Test') {
            steps {
                sh 'sleep 5'
                sh 'curl --fail http://localhost:8080/students'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished. Check the stage results above.'
        }
    }
}
