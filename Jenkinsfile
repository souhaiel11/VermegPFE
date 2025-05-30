def COLOR_MAP = [
  SUCCESS: 'good',
  UNSTABLE: 'warning',
  FAILURE: 'danger'
]

pipeline {
  agent any

  environment {
    SONAR_PROJECT_KEY = 'equipe1-3arctic1-2425'
    SONAR_HOST_URL    = 'http://localhost:9000'
    SONAR_LOGIN       = credentials('sonar-token1')

  }

  stages {
    stage('🧹 Nettoyage') {
      steps { sh 'mvn clean' }
    }
    stage('⚙️ Compilation') {
      steps { sh 'mvn compile' }
    }
    stage('🧪 Tests Unitaires') {
      steps {
        echo "▶️ BlocServiceMockTest"
        sh 'mvn test -Dtest=BlocServiceMockTest'
      }
    }
    stage('🧪 Tests d’Intégration') {
      steps {
        echo "▶️ BlocServiceTest"
        sh 'mvn test -Dtest=BlocServiceTest'
      }
    }
    stage('📦 Packaging') {
      steps { sh 'mvn package -DskipTests -e' }
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
   /*  stage('🛡️ Quality Gate') {
      steps {
        timeout(time: 1, unit: 'HOURS') {
          waitForQualityGate abortPipeline: true
        }
      }
    } */
    stage('📤 Déploiement Nexus') {
      steps { sh 'mvn deploy -DskipTests' }
    }
  }

  post {
    success {
      echo "✅ Pipeline exécuté avec succès"
    }
    failure {
      echo "❌ Le pipeline a échoué. Vérifiez la console Jenkins."
    }
    always {
      slackSend channel: '#souhaiel',
                color: COLOR_MAP[currentBuild.currentResult] ?: 'warning',
                message: "*${currentBuild.currentResult}:* Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\nMore info: ${env.BUILD_URL}"
    }
  }
}
