pipeline {
  agent any

  environment {
    SONAR_PROJECT_KEY  = 'equipe1-3arctic1-2425'
    SONAR_HOST_URL     = 'http://localhost:9000'
    SONAR_LOGIN        = credentials('sonar-token')
    JACOCO_EXEC        = '**/target/jacoco.exec'
    JACOCO_SOURCE      = '**/src/main/java'
  }

  stages {

    stage('🧹 Clean') {
      steps { sh 'mvn clean' }
    }

    stage('⚙️ Compile') {
      steps { sh 'mvn compile' }
    }

    stage('🧪 Test & Coverage') {
      steps {
        // Lance les tests et génère le rapport JaCoCo
        sh 'mvn test jacoco:report'

        // Archive les résultats JUnit
        junit '**/target/surefire-reports/*.xml'

        // Affiche le rapport JaCoCo dans Jenkins
        jacoco(
          execPattern: JACOCO_EXEC,
          sourcePattern: JACOCO_SOURCE,
          exclusionPattern: '**/target/generated-sources/**'
        )
      }
    }

    stage('🔍 SonarQube Analysis') {
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

    /* stage('🛡️ Quality Gate') {
      steps {
        timeout(time: 1, unit: 'HOURS') {
          waitForQualityGate abortPipeline: true
        }
      }
    } */

    stage('📦 Package') {
      steps {
        sh 'mvn package -DskipTests -e'
      }
    }

    stage('📤 Deploy to Nexus') {
      steps {
        sh 'mvn deploy -DskipTests'
      }
    }
  }

  post {
    success { echo '✅ Pipeline exécuté avec succès' }
    failure { echo '❌ Échec du pipeline, voir la console Jenkins.' }
  }
}
