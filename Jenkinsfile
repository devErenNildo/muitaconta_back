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

    stages {
        stage('Cleanup Workspace') {
            steps {
                echo "Limpando o workspace..."
                cleanWs()
            }
        }

        stage('Checkout Repositório do Backend') {
            steps {
                echo "Clonando o repositório da aplicação (backend)..."
                checkout scm
            }
        }

        stage('Clone Repositório K3s (DevOps)') {
            steps {
                echo "Clonando o repositório de DevOps..."
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/devErenNildo/muita-conta_devops.git',
                        credentialsId: 'k3s-repo-credentials'
                    ]],
                    extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'k3s-repo']]
                ])
            }
        }

        stage('Sincronizar e Atualizar Repositório K3s') {
            steps {
                dir('k3s-repo') {
                    script {
                        echo "Limpando o diretório de destino (exceto a pasta .git)..."
                        sh 'find . -mindepth 1 -path ./.git -prune -o -exec rm -rf {} +'

                        echo "Copiando o CONTEÚDO da pasta 'k3s' para a raiz..."
                        sh 'cp -a ../k3s/. .'

                        echo "Verificando se há alterações para commitar..."
                        def changes = sh(script: 'git status --porcelain', returnStdout: true).trim()

                        if (changes) {
                            withCredentials([usernamePassword(credentialsId: 'k3s-repo-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
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