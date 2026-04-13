def COLOR_MAP = [
  SUCCESS : 'good',
  UNSTABLE: 'warning',
  FAILURE : 'danger'
]

pipeline {
  agent any
  tools {
    maven 'M3'
  }
environment {
    SONAR_PROJECT_KEY = 'equipe1-3arctic1-2425'
    DOCKER_IMAGE      = 'pfevermeg'
}
  stages {
    stage('🧹 Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('⚙️ Compile') {
      steps {
        sh 'mvn compile'
      }
    }
    stage('🧪 Tests') {
      steps {
        sh 'mvn test -Dtest=BlocServiceMockTest,BlocServiceTest'
      }
    }
    stage('📦 Package + Detect JAR') {
      steps {
        sh 'mvn package -DskipTests'
        script {
          def jar = sh(script: "ls target/*.jar | grep -v 'original' | head -n 1", returnStdout: true).trim()
          env.JAR_NAME = jar.replaceAll('target/', '')
          echo "JAR détecté : ${env.JAR_NAME}"
        }
      }
    }
stage('🔍 Analyse SonarQube') {
    steps {
        withSonarQubeEnv('sq1') {
            sh '''
                /var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/M3/bin/mvn \
                org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                -Dsonar.projectKey=$SONAR_PROJECT_KEY
            '''
        }
    }
}
    stage('📤 Deploy Nexus') {
      steps {
        sh 'mvn deploy -DskipTests --settings /var/jenkins_home/.m2/settings.xml'
      }
    }
    stage('🐳 Build Docker') {
      steps {
        sh "docker build --build-arg JAR_FILE=${env.JAR_NAME} -t ${DOCKER_IMAGE}:latest ."
      }
    }
    stage('📤 Push Docker') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'dockerHub',
          usernameVariable: 'DOCKER_HUB_USER',
          passwordVariable: 'DOCKER_HUB_PASS'
        )]) {
          sh """
            echo "${DOCKER_HUB_PASS}" | docker login -u "${DOCKER_HUB_USER}" --password-stdin
            docker tag ${DOCKER_IMAGE}:latest ${DOCKER_HUB_USER}/${DOCKER_IMAGE}:latest
            docker push ${DOCKER_HUB_USER}/${DOCKER_IMAGE}:latest
          """
        }
      }
    }
    stage('🚀 Docker Compose') {
      steps {
        sh '''
          docker ps -a | grep springboot-app || true
          docker rm -f springboot-app || true
          docker compose up -d --no-deps --build springboot-app
        '''
      }
    }
  }
  post {
    always {
      script {
        def buildStatus = currentBuild.currentResult

        // ── Email ────────────────────────────────────────────────
        emailext(
          subject: "📬 Build ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
          body: """
            <p>📌 Project: ${env.JOB_NAME}</p>
            <p>🔢 Build: ${env.BUILD_NUMBER}</p>
            <p>📊 Status: ${buildStatus}</p>
            <p>🔗 <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
          """,
          to: 'amrisouhail96@gmail.com',
          from: 'amrisouhail96@gmail.com',
          replyTo: 'amrisouhail96@gmail.com',
          mimeType: 'text/html'
        )

        // ── Slack ────────────────────────────────────────────────
        try {
          slackSend(
            channel: '#souhaiel',
            color: COLOR_MAP[buildStatus] ?: 'warning',
            message: "*${buildStatus}:* Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\nURL: ${env.BUILD_URL}"
          )
        } catch (Exception e) {
          echo "Slack notification failed: ${e.message}"
        }

        // ── DevSecOps IA — n8n Notification ─────────────────────
        try {
          def severity = (buildStatus == 'FAILURE') ? 'HIGH' : (buildStatus == 'UNSTABLE') ? 'MEDIUM' : 'LOW'
          def payload = """{
            "jenkins": true,
            "build": {
              "number": ${env.BUILD_NUMBER},
              "status": "${buildStatus}",
              "phase": "FINALIZED",
              "url": "${env.BUILD_URL}"
            },
            "name": "${env.JOB_NAME}",
            "sonarProjectKey": "${env.SONAR_PROJECT_KEY}",
            "severity": "${severity}",
            "logs": [
              "Build ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
              "URL: ${env.BUILD_URL}"
            ]
          }"""

          httpRequest(
            url: 'http://n8n:5678/webhook/incident-intake',
            httpMode: 'POST',
            contentType: 'APPLICATION_JSON',
            requestBody: payload,
            customHeaders: [[name: 'X-API-Key', value: 'devsecops-secret-2024']],
            ignoreSslErrors: true,
            validResponseCodes: '100:599'
          )
          echo "✅ Notification DevSecOps IA envoyée — statut: ${buildStatus}"
        } catch (Exception e) {
          echo "⚠️ Notification DevSecOps IA failed: ${e.message}"
        }

        echo "Build terminé: ${buildStatus}"
      }
    }
  }
}
