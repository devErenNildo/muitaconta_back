// properties([
//     pipelineTriggers([
//         [
//             $class: 'GitHubPushTrigger',
//             spec: '',
//             secret: 'github-webhook-secret'
//         ]
//     ])
// ])
//
//
// pipeline {
//     agent any
//
//     environment {
//         DOCKER_REGISTRY = 'registry.muitaconta.com.br'
//         IMAGE_NAME = 'muitaconta-api'
//         DOCKER_CREDENTIALS_ID = 'docker-registry-credentials'
//         DEVOPS_REPO_URL = 'https://github.com/devErenNildo/muita-conta_devops.git'
//         DEVOPS_REPO_CREDENTIALS_ID = 'k3s-repo-credentials'
//     }
//
//     stages {
//         stage('Build da aplicação') {
//             steps {
//                 echo "Compilando a aplicação Spring"
//                 sh "mvn clean package -DskipTests"
//             }
//         }
//
//         stage('Build e Push da imagem docker') {
//             script {
//                 def imageTag = "${env.BUILD_NUMBER}"
//                 def fullImageName = "${DOCKER_REGISTRY}/${IMAGE_NAME}:${imageTag}"
//                 def latestImageName = "${DOCKER_REGISTRY}/${IMAGE_NAME}:latest"
//
//                 echo "Construindo a imagem Docker: ${fullImageName}"
//                 def customImage = docker.build(fullImageName, ".")
//
//                 docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
//                     echo "Enviando a imagem para o registry..."
//                     customImage.push()
//                     customImage.push('latest')
//                 }
//             }
//         }
//     }
//
//     stages {
//         stage('Cleanup Workspace') {
//             steps {
//                 echo "Limpando o workspace..."
//                 cleanWs()
//             }
//         }
//
//         stage('Checkout Repositório do Backend') {
//             steps {
//                 echo "Clonando o repositório da aplicação (backend)..."
//                 checkout scm
//             }
//         }
//
//         stage('Clone Repositório K3s (DevOps)') {
//             steps {
//                 echo "Clonando o repositório de DevOps..."
//                 checkout([
//                     $class: 'GitSCM',
//                     branches: [[name: '*/main']],
//                     userRemoteConfigs: [[
//                         url: 'https://github.com/devErenNildo/muita-conta_devops.git',
//                         credentialsId: 'k3s-repo-credentials'
//                     ]],
//                     extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'k3s-repo']]
//                 ])
//             }
//         }
//
//         stage('Sincronizar e Atualizar Repositório K3s') {
//             steps {
//                 dir('k3s-repo') {
//                     script {
//                         echo "Limpando o diretório de destino (exceto a pasta .git)..."
//                         sh 'find . -mindepth 1 -path ./.git -prune -o -exec rm -rf {} +'
//
//                         echo "Copiando o CONTEÚDO da pasta 'k3s' para a raiz..."
//                         sh 'cp -a ../k3s/. .'
//
//                         echo "Verificando se há alterações para commitar..."
//                         def changes = sh(script: 'git status --porcelain', returnStdout: true).trim()
//
//                         if (changes) {
//                             withCredentials([usernamePassword(credentialsId: 'k3s-repo-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
//                                 echo "Alterações detectadas. Configurando o Git..."
//                                 sh "git config user.email 'jenkins@muitaconta.com.br'"
//                                 sh "git config user.name 'Jenkins CI'"
//
//                                 echo "Adicionando e commitando as alterações..."
//                                 sh "git add ."
//                                 sh "git commit -m 'Sync: Atualizando manifestos do Kubernetes a partir do backend [ci skip]'"
//
//                                 echo "Enviando as alterações para o repositório de DevOps..."
//                                 sh "git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/devErenNildo/muita-conta_devops.git"
//                                 sh "git push origin HEAD:main"
//
//                                 echo "Repositório de DevOps atualizado com sucesso!"
//                             }
//                         } else {
//                             echo "Nenhuma alteração detectada. Nenhum commit necessário."
//                         }
//                     }
//                 }
//             }
//         }
//     }
// }







properties([
    pipelineTriggers([
        [
            $class: 'GitHubPushTrigger',
            spec: '',
            secret: 'github-webhook-secret'
        ]
    ])
])

pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'registry.muitaconta.com.br'
        IMAGE_NAME = 'muitaconta'
        DOCKER_CREDENTIALS_ID = 'docker-registry-credentials'
        DEVOPS_REPO_URL = 'https://github.com/devErenNildo/muita-conta_devops.git'
        DEVOPS_REPO_CREDENTIALS_ID = 'k3s-repo-credentials'
    }

    stages {
        stage('Cleanup e Checkout do Backend') {
            steps {
                echo "Limpando o workspace..."
                cleanWs()
                echo "Clonando o repositório da aplicação (backend)..."
                checkout scm
            }
        }

        stage('Build da aplicação') {
            steps {
                echo "Compilando a aplicação Spring"
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Build e Push da imagem Docker') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    def fullImageName = "${DOCKER_REGISTRY}/${IMAGE_NAME}:${imageTag}"
                    def latestImageName = "${DOCKER_REGISTRY}/${IMAGE_NAME}:latest"

                    echo "Construindo a imagem Docker: ${fullImageName}"
                    def customImage = docker.build(fullImageName, ".")

                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        echo "Enviando a imagem para o registry..."
                        customImage.push()
                        customImage.push('latest')
                    }
                }
            }
        }

        stage('Clone e Sincronização do Repositório K3s (DevOps)') {
            steps {
                echo "Clonando o repositório de DevOps..."
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: DEVOPS_REPO_URL,
                        credentialsId: DEVOPS_REPO_CREDENTIALS_ID
                    ]],
                    extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'k3s-repo']]
                ])

                dir('k3s-repo') {
                    script {
                        echo "Limpando o diretório de destino (exceto a pasta .git)..."
                        sh 'find . -mindepth 1 -path ./.git -prune -o -exec rm -rf {} +'

                        echo "Copiando o CONTEÚDO da pasta 'k3s' do workspace para a raiz do repo de devops..."
                        sh 'cp -a ../k3s/. .'

                        echo "Verificando se há alterações para commitar..."
                        def changes = sh(script: 'git status --porcelain', returnStdout: true).trim()

                        if (changes) {
                            withCredentials([usernamePassword(credentialsId: DEVOPS_REPO_CREDENTIALS_ID, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                                echo "Alterações detectadas. Configurando o Git..."
                                sh "git config user.email 'jenkins@muitaconta.com.br'"
                                sh "git config user.name 'Jenkins CI'"

                                echo "Adicionando e commitando as alterações..."
                                sh "git add ."
                                sh "git commit -m 'Sync: Atualizando manifestos do Kubernetes a partir do backend [ci skip]'"

                                echo "Enviando as alterações para o repositório de DevOps..."
                                sh "git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/devErenNildo/muita-conta_devops.git"
                                sh "git push origin HEAD:main"

                                echo "Repositório de DevOps atualizado com sucesso!"
                            }
                        } else {
                            echo "Nenhuma alteração detectada. Nenhum commit necessário."
                        }
                    }
                }
            }
        }
    }
}