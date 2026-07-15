pipeline {

    agent any

    stages {

        stage('Clone') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }

            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d --build'
            }
        }
    }
}