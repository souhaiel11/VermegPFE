pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'equipe1-3arctic1-2425'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_LOGIN = credentials('sonar-token')
    }

    stages {
        stage('📦 Clonage du dépôt Git privé') {
            steps {
                git branch: 'souhaielBloc',
                    url: 'https://github.com/OussamaBENHADJAHMED999/equipe1-3arctic1-2425.git',
                    credentialsId: 'github-token'
            }
        }

        stage('🧹 Nettoyage avec Maven') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('⚙️ Compilation du code') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('🧪 Exécution des tests unitaires') {
            steps {
                sh 'mvn test -Dtest=BlocServiceMockTest'
            }
        }

        stage('🧪 Exécution des tests d’intégration') {
            steps {
                sh 'mvn test -Dtest=BlocServiceTest'
            }
        }

        stage('📦 Packaging sans tests') {
            steps {
                sh 'mvn package -DskipTests -e'
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                withSonarQubeEnv('MySonarServer') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_LOGIN}
                    """
                }
            }
        }

        stage('📤 Déploiement vers Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline exécuté avec succès"
        }
        failure {
            echo "❌ Le pipeline a échoué. Vérifie les erreurs dans la console Jenkins."
        }
    }
}
