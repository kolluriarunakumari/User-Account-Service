// ============================================================
// CloudBees CI/CD Pipeline - User Account Microservice
// ============================================================
pipeline {
    agent {
        label 'maven-jdk17'  // CloudBees managed agent with Maven + JDK 17
    }

    environment {
        APP_NAME        = 'user-account-service'
        APP_VERSION     = '1.0.0'
        DOCKER_REGISTRY = 'registry.cloudbees.io'                 // Replace with your registry
        IMAGE_NAME      = "${DOCKER_REGISTRY}/${APP_NAME}"
        IMAGE_TAG       = "${APP_VERSION}-${BUILD_NUMBER}"
        DOCKER_CREDS    = credentials('docker-registry-creds')    // CloudBees credential ID
        KUBE_CONFIG     = credentials('kubeconfig-creds')         // K8s config credential ID
        SONAR_TOKEN     = credentials('sonar-token')              // SonarQube token
        DEPLOY_ENV      = "${params.DEPLOY_ENV ?: 'staging'}"
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Target deployment environment')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip unit tests')
        booleanParam(name: 'FORCE_DEPLOY', defaultValue: false, description: 'Force deploy even on test failures')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        // ── Stage 1: Checkout ─────────────────────────────────────
        stage('Checkout') {
            steps {
                checkout scm
                echo "Branch: ${env.BRANCH_NAME} | Build: ${env.BUILD_NUMBER}"
                sh 'git log -1 --format="%H %s"'
            }
        }

        // ── Stage 2: Build ────────────────────────────────────────
        stage('Build') {
            steps {
                sh '''
                    mvn clean compile -B \
                        -Dmaven.test.skip=true \
                        --no-transfer-progress
                '''
            }
            post {
                success { echo "Build SUCCESS" }
                failure { echo "Build FAILED" }
            }
        }

        // ── Stage 3: Unit Tests ───────────────────────────────────
        stage('Unit Tests') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                sh '''
                    mvn test -B \
                        --no-transfer-progress \
                        -Dsurefire.failIfNoSpecifiedTests=false
                '''
            }
            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                    publishCoverage(
                        adapters: [jacocoAdapter('**/target/site/jacoco/jacoco.xml')],
                        sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                    )
                }
            }
        }

        // ── Stage 4: Code Quality (SonarQube) ─────────────────────
        stage('Code Quality') {
            when {
                branch 'main'
            }
            steps {
                withSonarQubeEnv('SonarQube-Server') {
                    sh '''
                        mvn sonar:sonar -B \
                            -Dsonar.projectKey=${APP_NAME} \
                            -Dsonar.projectName="${APP_NAME}" \
                            -Dsonar.projectVersion=${APP_VERSION} \
                            -Dsonar.token=${SONAR_TOKEN} \
                            --no-transfer-progress
                    '''
                }
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ── Stage 5: Package (JAR) ────────────────────────────────
        stage('Package') {
            steps {
                sh '''
                    mvn package -B \
                        -DskipTests=true \
                        --no-transfer-progress
                '''
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        // ── Stage 6: Docker Build & Push ──────────────────────────
        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-creds') {
                        def image = docker.build("${IMAGE_NAME}:${IMAGE_TAG}", "--no-cache .")
                        image.push()
                        image.push('latest')
                        echo "Pushed: ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }

        // ── Stage 7: Deploy to Staging ────────────────────────────
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    expression { params.DEPLOY_ENV == 'staging' }
                }
            }
            steps {
                withKubeConfig([credentialsId: 'kubeconfig-creds', namespace: 'staging']) {
                    sh """
                        sed -i 's|IMAGE_PLACEHOLDER|${IMAGE_NAME}:${IMAGE_TAG}|g' cicd/k8s/deployment.yaml
                        kubectl apply -f cicd/k8s/deployment.yaml -n staging
                        kubectl apply -f cicd/k8s/service.yaml -n staging
                        kubectl rollout status deployment/${APP_NAME} -n staging --timeout=120s
                    """
                }
            }
        }

        // ── Stage 8: Integration / Smoke Test ─────────────────────
        stage('Smoke Test') {
            when {
                anyOf { branch 'main'; branch 'develop' }
            }
            steps {
                sh '''
                    echo "Waiting for service to be ready..."
                    sleep 15
                    STAGING_URL="http://user-account-service.staging.svc.cluster.local:8080"
                    curl -f "${STAGING_URL}/actuator/health" || exit 1
                    curl -f "${STAGING_URL}/api/users"       || exit 1
                    curl -f "${STAGING_URL}/api/accounts"    || exit 1
                    echo "Smoke tests passed!"
                '''
            }
        }

        // ── Stage 9: Deploy to Production ─────────────────────────
        stage('Deploy to Production') {
            when {
                allOf {
                    branch 'main'
                    expression { params.DEPLOY_ENV == 'production' }
                }
            }
            steps {
                input(message: "Deploy ${IMAGE_TAG} to PRODUCTION?", ok: "Deploy Now")
                withKubeConfig([credentialsId: 'kubeconfig-creds', namespace: 'production']) {
                    sh """
                        sed -i 's|IMAGE_PLACEHOLDER|${IMAGE_NAME}:${IMAGE_TAG}|g' cicd/k8s/deployment.yaml
                        kubectl apply -f cicd/k8s/deployment.yaml -n production
                        kubectl apply -f cicd/k8s/service.yaml -n production
                        kubectl rollout status deployment/${APP_NAME} -n production --timeout=180s
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline SUCCESS: ${APP_NAME}:${IMAGE_TAG} deployed to ${DEPLOY_ENV}"
            // Uncomment to enable Slack notification:
            // slackSend channel: '#deployments', color: 'good',
            //     message: "✅ ${APP_NAME} ${IMAGE_TAG} deployed to ${DEPLOY_ENV}"
        }
        failure {
            echo "Pipeline FAILED for ${APP_NAME}:${IMAGE_TAG}"
            // slackSend channel: '#deployments', color: 'danger',
            //     message: "❌ ${APP_NAME} ${IMAGE_TAG} pipeline failed"
        }
        always {
            cleanWs()
        }
    }
}
