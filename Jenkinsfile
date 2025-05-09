pipeline {
    agent any

    environment {
        // Configuration SonarQube
        SONAR_PROJECT_KEY = 'projet-dev'                   // 🔁 À adapter
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_LOGIN = credentials('sonar-token')           // 🔁 Token stocké dans Jenkins
    }

    stages {

        stage('📦 Clonage du dépôt Git privé') {
            steps {
                echo "🔁 Clonage du projet depuis GitHub"
                git branch: 'souhaielBloc',
                    url: 'https://github.com/OussamaBENHADJAHMED999/equipe1-3arctic1-2425.git',  // 🔁 Modifier ici
                    credentialsId: 'github-token'  // 🔁 Assure-toi que c’est bien créé dans Jenkins > Credentials
            }
        }

        stage('🧹 Nettoyage avec Maven') {
            steps {
                echo "🧼 Suppression du dossier target"
                sh 'mvn clean'
            }
        }

        stage('⚙️ Compilation du code') {
            steps {
                echo "🔧 Compilation avec mvn compile"
                sh 'mvn compile'
            }
        }

        stage('📦 Packaging sans exécuter les tests') {
            steps {
                echo "🎯 Génération du livrable (tests désactivés)"
                sh 'mvn package -DskipTests -e'  // ❌ Skip tests à cause de l'absence de la base de données
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                echo "📊 Analyse qualité avec SonarQube"
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
