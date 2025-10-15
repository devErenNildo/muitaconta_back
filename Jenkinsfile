pipeline {
    agent any

    environment {
        K3S_REPO_URL = "https://github.com/devErenNildo/muita-conta_devops.git"
        K3S_REPO_CREDENTIALS_ID = "k3s-repo-credentials"
    }

    stages {
        stage('Checkout Repositório do Backend') {
            steps {
                echo "Clonando o repositório da aplicação..."
                checkout scm
            }
        }

        stage('Clone Repositório K3s') {
            steps {
                script {
                    echo "Clonando o repositório de DevOps: ${K3S_REPO_URL}"
                    withCredentials([gitUsernamePassword(credentialsId: K3S_REPO_CREDENTIALS_ID, gitToolName: 'Default')]) {
                        sh "git clone ${K3S_REPO_URL} k3s-repo"
                    }
                }
            }
        }

        stage('Sincronizar e Atualizar Repositório K3s') {
            steps {
                dir('k3s-repo') {
                    script {
                        echo "Limpando o diretório de destino (exceto a pasta .git)..."
                        sh 'find . -path ./.git -prune -o -exec rm -rf {} +'

                        echo "Copiando a pasta 'k3s' do backend para o repositório de DevOps..."
                        sh 'cp -R ../k3s/ .'

                        echo "Verificando se há alterações para commitar..."

                        def changes = sh(script: 'git status --porcelain', returnStdout: true).trim()

                        if (changes) {
                            echo "Alterações detectadas. Configurando o Git para fazer o commit..."
                            sh "git config user.email 'jenkins@muitaconta.com.br'"
                            sh "git config user.name 'Jenkins CI'"

                            echo "Adicionando e commitando as alterações..."
                            sh "git add ."

                            sh "git commit -m 'Sync: Atualizando manifestos do Kubernetes a partir do backend [ci skip]'"

                            echo "Enviando as alterações para o repositório de DevOps..."

                            sh "git push origin HEAD:main"
                            echo "Repositório de DevOps atualizado com sucesso!"
                        } else {
                            echo "Nenhuma alteração detectada nos arquivos do Kubernetes. Nenhum commit necessário."
                        }
                    }
                }
            }
        }
    }
}