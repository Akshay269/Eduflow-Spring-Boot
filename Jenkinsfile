pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'z9shay/eduflow'
        DOCKER_TAG = "${BUILD_NUMBER}"
        EC2_HOST = '13.201.88.77',
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo '🔄 Checking out code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building application...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                echo '📦 Pushing to Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker push ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deploying to EC2...'
                sshagent(['ec2-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} '
                            docker pull ${DOCKER_IMAGE}:latest &&
                            docker stop eduflow-app || true &&
                            docker rm eduflow-app || true &&
                            docker run -d \
                                --name eduflow-app \
                                --restart unless-stopped \
                                -p 8080:8080 \
                                -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} \
                                -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
                                -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                                -e JWT_SECRET=${JWT_SECRET} \
                                -e AWS_ACCESS_KEY=${AWS_ACCESS_KEY} \
                                -e AWS_SECRET_KEY=${AWS_SECRET_KEY} \
                                -e AWS_BUCKET_NAME=${AWS_BUCKET_NAME} \
                                -e AWS_REGION=${AWS_REGION} \
                                -e GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
                                -e GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
                                ${DOCKER_IMAGE}:latest
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        always {
            sh 'docker image prune -f'
        }
    }
}
