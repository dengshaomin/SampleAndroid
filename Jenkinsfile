pipeline {
    agent any
    parameters {
        choice choices: ['Test', 'Release'], description: '包类型', name: 'PackageType'
    }

    stages {
        stage('commit log') {
            steps {
                script {
                    def changeLogSets = currentBuild.changeSets
                    for (int i = 0; i < changeLogSets.size(); i++) {
                        def entries = changeLogSets[i].items
                        for (int j = 0; j < entries.length; j++) {
                            def entry = entries[j]
                            echo "${entry.commitId} by ${entry.author} on ${changeTime(entry.timestamp)}: ${entry.msg}"
                        }
                    }
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
def changeTime(Date date){
    return date.format("yyyy-MM-dd HH:mm:ss")
}