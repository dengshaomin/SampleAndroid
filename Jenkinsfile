pipeline {
    agent any

    stages {
        stage('commit log'){
            steps{
                script{
                    commit = sh(returnStdout: true, script: 'git log -1 --oneline').trim()
                    String commitMsg = ""
                    List commitMsgPre = commit.split(" ")
                    echo "commitmsg-0:$commitMsgPre"
                    for(int i=0; i<commitMsgPre.size(); i++){
                      commitMsg += commitMsgPre.getAt(i) + " "
                    }
                    echo "commitmsg-1:$commitMsg"
                }
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}