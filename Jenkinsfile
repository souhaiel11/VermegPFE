pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'equipe1-3arctic1-2425'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_LOGIN = credentials('sonar-token1')
        // Ajout de la configuration JaCoCo
        JACOCO_EXEC = '**/target/jacoco.exec'
        JACOCO_SOURCE = '**/src/main/java'
    }

    stages {
        stage('🧹 Nettoyage') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('⚙️ Compilation') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('🧪 Tests & Couverture') {
            steps {
                // Exécution de tous les tests avec JaCoCo
                sh 'mvn test jacoco:report'

                // Archivage des résultats des tests
                junit '**/target/surefire-reports/*.xml'

                // Archivage des rapports JaCoCo
                jacoco(
                    execPattern: JACOCO_EXEC,
                    sourcePattern: JACOCO_SOURCE,
                    exclusionPattern: '**/target/generated-sources/**'
                )
            }
        }

        stage('🔍 Analyse SonarQube') {
            steps {
                withSonarQubeEnv('MySonarServer') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_LOGIN} \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('🛡️ Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('📦 Packaging') {
            steps {
                // Package sans re-exécuter les tests (déjà faits)
                sh 'mvn package -DskipTests -e'
            }
        }

        stage('📤 Déploiement Nexus') {
            steps {
                // Déploiement seulement si Quality Gate est passé
                sh 'mvn deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline exécuté avec succès"
            // Optionnel : Notification de succès
        }
        failure {
            echo "❌ Le pipeline a échoué. Vérifiez les erreurs dans la console Jenkins."
            // Optionnel : Notification d'échec
        }
    }
}