pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'z9shay/eduflow'
        DOCKER_TAG = "${BUILD_NUMBER}"
        EC2_HOST = '13.201.88.77'
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${PATH}"
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
                echo '🧪 Skipping tests - no test DB available...'
                echo '✅ Tests will run after RDS setup in Phase 12'
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'

                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }

        stage('Docker Push') {
            steps {
                echo '📦 Pushing to Docker Hub...'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deploying to EC2...'

                withCredentials([
                    string(credentialsId: 'SPRING_DATASOURCE_URL', variable: 'DB_URL'),
                    string(credentialsId: 'SPRING_DATASOURCE_USERNAME', variable: 'DB_USER'),
                    string(credentialsId: 'SPRING_DATASOURCE_PASSWORD', variable: 'DB_PASS'),
                    string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                    string(credentialsId: 'AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY'),
                    string(credentialsId: 'AWS_SECRET_KEY', variable: 'AWS_SECRET_KEY'),
                    string(credentialsId: 'AWS_BUCKET_NAME', variable: 'AWS_BUCKET_NAME'),
                    string(credentialsId: 'AWS_REGION', variable: 'AWS_REGION'),
                    string(credentialsId: 'GOOGLE_CLIENT_ID', variable: 'GOOGLE_CLIENT_ID'),
                    string(credentialsId: 'GOOGLE_CLIENT_SECRET', variable: 'GOOGLE_CLIENT_SECRET')
                ]) {

                    sh """
                        cat > app.env <<EOF
SPRING_DATASOURCE_URL=${DB_URL}
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASS}
JWT_SECRET=${JWT_SECRET}
AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
AWS_SECRET_KEY=${AWS_SECRET_KEY}
AWS_BUCKET_NAME=${AWS_BUCKET_NAME}
AWS_REGION=${AWS_REGION}
GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
EOF
                    """

                    sshagent(['ec2-ssh-key']) {

                        sh """
                            scp -o StrictHostKeyChecking=no app.env ubuntu@${EC2_HOST}:/home/ubuntu/app.env
                        """

                        sh """
                            ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} '
                                docker pull ${DOCKER_IMAGE}:latest &&
                                docker stop eduflow-app || true &&
                                docker rm eduflow-app || true &&
                                docker run -d \\
                                    --name eduflow-app \\
                                    --restart unless-stopped \\
                                    -p 8080:8080 \\
                                    --env-file /home/ubuntu/app.env \\
                                    ${DOCKER_IMAGE}:latest
                            '
                        """
                    }

                    sh 'rm -f app.env'
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