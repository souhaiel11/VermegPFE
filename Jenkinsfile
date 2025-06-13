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

    stage('🧹 Nettoyage') {
      steps {
        echo "🧽 Maven clean..."
        sh 'mvn clean'
      }
    }

    stage('⚙️ Compilation') {
      steps {
        echo "🔧 Compilation du projet..."
        sh 'mvn compile'
      }
    }

    stage('🧪 Tests') {
      steps {
        echo "▶️ Tests unitaires et d’intégration"
        sh 'mvn test -Dtest=BlocServiceMockTest,BlocServiceTest'
      }
    }

    stage('📦 Packaging & Détection JAR') {
      steps {
        echo "📦 Packaging avec skipTests"
        sh 'mvn package -DskipTests'
        script {
          def jar = sh(script: 'ls target/*.jar | grep -v original | head -n 1', returnStdout: true).trim()
          env.JAR_NAME = jar.replaceAll('target/', '')
          echo "🗂️ JAR détecté : ${env.JAR_NAME}"
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

    stage('📤 Déploiement Nexus') {
      steps {
        echo "🚀 Déploiement vers Nexus..."
        sh 'mvn deploy -DskipTests'
      }
    }

    stage('🐳 Build Docker Image') {
      steps {
        echo "📦 Construction de l'image Docker avec ${env.JAR_NAME}..."
        sh "docker build --build-arg JAR_FILE=${env.JAR_NAME} -f src/main/docker/Dockerfile -t ${DOCKER_IMAGE}:latest ."
      }
    }

    stage('📤 Docker Push') {
      steps {
        echo '📤 Pushing Docker image to Docker Hub...'
        withCredentials([usernamePassword(credentialsId: 'dockerHub', usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
          sh """
            echo "${DOCKER_HUB_PASS}" | docker login -u "${DOCKER_HUB_USER}" --password-stdin
            docker tag ${DOCKER_IMAGE}:latest ${DOCKER_HUB_USER}/${DOCKER_IMAGE}:latest
            docker push ${DOCKER_HUB_USER}/${DOCKER_IMAGE}:latest
          """
        }
      }
    }

    stage('🚀 Docker Compose Deploy') {
      steps {
        echo '🚀 Déploiement avec Docker Compose...'
        dir('src/main/docker') {
          sh 'docker-compose down || true'
          sh 'docker-compose up -d --build'
        }
      }
    }
  }

  post {
    always {
      script {
        def buildStatus = currentBuild.currentResult
        def buildUser = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0]?.userId ?: 'GitHub User'

        emailext(
          subject: "📬 Pipeline ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
          body: """
            <p><b>Jenkins Pipeline Notification</b></p>
            <p>📌 <b>Project</b>: ${env.JOB_NAME}</p>
            <p>🔢 <b>Build Number</b>: ${env.BUILD_NUMBER}</p>
            <p>📊 <b>Status</b>: ${buildStatus}</p>
            <p>👤 <b>Started by</b>: ${buildUser}</p>
            <p>🔗 <b>URL</b>: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
            <p>💬 YA SOU YA M3ALLLLLEM</p>
          """,
          to: 'amrisouhail96@gmail.com',
          from: 'amrisouhail96@gmail.com',
          replyTo: 'amrisouhail96@gmail.com',
          mimeType: 'text/html'
        )

        slackSend(
          channel: '#souhaiel',
          color: COLOR_MAP[buildStatus] ?: 'warning',
          message: "*${buildStatus}:* Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\nMore info: ${env.BUILD_URL}"
        )
      }
    }
  }
}
