pipeline {
    agent any 
    
    tools {
        jdk 'jdk17'
        gradle 'Gradle 7.5'
    }
    environment {
        IMAGE_TAG = "${env.CIRCLE_TAG ?: '0.0.1'}"
        SCANNER_HOME = tool 'sonar-scanner'
        IMAGE_REPO_NAME = "msp-repo"
        REPOSITORY_URI = "416668258315.dkr.ecr.us-west-2.amazonaws.com/msp-repo"
        AWS_DEFAULT_REGION = "us-west-2"
        AWS_ACCOUNT_ID = "416668258315"
        SONARQUBE_URL = 'http://52.33.43.169:9000'
        SONARQUBE_CREDENTIALS = credentials('sonar-token')
        NEXUS_URL = 'http://54.188.185.39:8081'
        NEXUS_CREDENTIALS = credentials('nexus-cred-1')
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk' // Adjust the path if necessary
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/balarimpy/merchant-service.git'
            }
        }

        stage('Install Gradle Wrapper') {
            steps {
                sh 'gradle wrapper'
            }
        }

        stage('Restore Cache') {
            steps {
                // Placeholder for cache restoration
                echo 'Restoring cache...'
            }
        }

        stage('Prepare Environment') {
            steps {
                // Create the directory if it doesn't exist
                sh 'mkdir -p src/main/resources/dev'
                // Copy the .env file
                sh 'cp example.env src/main/resources/dev/.env'
            }
        }
        // stage('SonarQube Analysis') {
        //      steps {
        //              withSonarQubeEnv('sonar-server') {
        //              // Use double quotes to allow variable interpolation
        //             sh "./gradlew sonarqube -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.login=${SONARQUBE_CREDENTIALS}"
        //             }
        //     }
        // }
        // stage('OWASP Dependency Check') {
        //     steps {
        //         sh './gradlew dependencyCheckAnalyze'
        //     }
        //     post {
        //         always {
        //             dependencyCheckPublisher pattern: '**/build/reports/dependency-check-report.xml'
        //         }
        //     }
        // }

        stage('Run Project Build') {
            steps {
                sh './gradlew build -x test'
            }
        }

        // stage('Store Artifacts') {
        //     steps {
        //         archiveArtifacts artifacts: 'build/libs/merchant-service-0.0.1-SNAPSHOT.jar', allowEmptyArchive: true
        //     }
        // }

        // stage('Upload to Nexus') {
        //     steps {
        //         nexusArtifactUploader(
        //             nexusVersion: 'nexus3',
        //             protocol: 'http',
        //             nexusUrl: "${NEXUS_URL}",
        //             groupId: 'com.example',
        //             version: "${IMAGE_TAG}",
        //             repository: 'maven-releases',
        //             credentialsId: 'nexus-cred-1',
        //             artifacts: [
        //                 [artifactId: 'merchant-service', classifier: '', file: 'build/libs/merchant-service-0.0.1-SNAPSHOT.jar', type: 'jar']
        //             ]
        //         )
        //     }
        // }

        stage('Logging into AWS ECR') {
            steps {
                script {
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                }
            }
        }

        stage('Building image') {
            steps {
                script {
                    sh 'export DOCKER_BUILDKIT=0'
                    sh 'docker --version'
                    dockerImage = docker.build "merchant-service:${IMAGE_TAG}"
                    sh "docker tag merchant-service:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }

        stage('Trivy Scan') {
            steps {
                sh "trivy image ${REPOSITORY_URI}:${IMAGE_TAG} > trivy-report.txt"
            }
        }

        stage('Pushing to ECR') {
            steps {
                script {
                    sh "docker tag merchant-service:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"
                    sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/merchant-service:${IMAGE_TAG}"
                }
            }
        }
    }
}
