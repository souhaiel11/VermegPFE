def COLOR_MAP = [
  SUCCESS : 'good',
  UNSTABLE: 'warning',
  FAILURE : 'danger'
]

pipeline {
  agent any

  environment {
    SONAR_PROJECT_KEY = 'equipe1-3arctic1-2425'
    SONAR_HOST_URL    = 'http://localhost:9000'
    SONAR_LOGIN       = credentials('sonar-token1')
    DOCKER_IMAGE      = 'souhaielbloc'
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
        sh 'sleep 20'
        script {
          def jar = sh(script: "ls target/*.jar | grep -v 'original' | head -n 1", returnStdout: true).trim()
          env.JAR_NAME = jar.replaceAll('target/', '')
          echo "JAR détecté : ${env.JAR_NAME}"
        }
      }
    }

    stage('🔍 Analyse SonarQube') {
      steps {
        withSonarQubeEnv('MySonarServer') {
          sh """
            mvn sonar:sonar \\
              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \\
              -Dsonar.host.url=${SONAR_HOST_URL} \\
              -Dsonar.login=${SONAR_LOGIN}
          """
        }
      }
    }

    stage('📤 Deploy Nexus') {
      steps {
        sh 'mvn deploy -DskipTests'
      }
    }

    stage('🐳 Build Docker') {
      steps {
        sh "docker build --build-arg JAR_FILE=${env.JAR_NAME} -t ${DOCKER_IMAGE}:latest ."
      }
    }

    stage('📤 Push Docker') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerHub', usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
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
        sh 'docker-compose stop springboot-app'
        sh 'docker-compose up -d --build springboot-app'
        sh 'sleep 30'
      }
    }
  }

  post {
    always {
      script {
        def buildStatus = currentBuild.currentResult
        def buildUser = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0]?.userId ?: 'GitHub User'

        emailext(
          subject: "📬 Build ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
          body: """
            <p>📌 Project: ${env.JOB_NA"ME}</p>
            <p>🔢 Build: ${env.BUILD_NUMBER}</p>
            <p>📊 Status: ${buildStatus}</p>
            <p>👤 User: ${buildUser}</p>
            <p>🔗 <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
          """,
          to: 'amrisouhail96@gmail.com',
          from: 'amrisouhail96@gmail.com',
          replyTo: 'amrisouhail96@gmail.com',
          mimeType: 'text/html'
        )

        slackSend(
          channel: '#souhaiel',
          color: COLOR_MAP[buildStatus] ?: 'warning',
          message: "*${buildStatus}:* Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\nURL: ${env.BUILD_URL}"
        )
      }
    }
  }
}
