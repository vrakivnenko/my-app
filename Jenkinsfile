#!/usr/bin/env groovy
recipient = 'rakivnenko81@gmail.com'
pipeline {
    agent any
    triggers { pollSCM('*/2 * * * *') }

    stages {
        stage ('print branch name') {
            steps {
                echo BRANCH_NAME
            }
        }
        stage ('check for pull request') {
            when {
                expression { BRANCH_NAME =~ /^PR-/ }
            }
            steps {
                echo 'We have a new pull requests. Need to run some tests on it'
                script {
                    pullRequest.createStatus(
                        'pending',
                        'somme',
                        'text', 
                        'https://github.com/vrakivnenko/my-app/pull/1'
                    ) 
                }
                script {
                    def return_code = sh (
                        script: "./script.sh",
                        returnStdout: true
                    ).trim()
                    println return_code
                    if (return_code == "0") {
                        echo 'it`s ok'
                        pullRequest.createStatus(
                            'success',
                            'somme',
                            'text', 
                            'https://github.com/vrakivnenko/my-app/pull/1'
                        ) 
                    } else {
                        pullRequest.createStatus(
                            'failure',
                            'somme',
                            'text', 
                            'https://github.com/vrakivnenko/my-app/pull/1'
                        ) 
                    }
                }

            }
        }
        stage ('Regular branch') {
            when {
                expression { BRANCH_NAME =~ /^fb-/ }
            }
            steps {
                script {
                    def test_result = test(BRANCH_NAME)
                    if (test_result == "0") {
                        println "your script have good syntax"
                    } else {
                        emailext(
                            subject: "FAILED TEST: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]",
                            body:
                    """
                    FAILED: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]
                    Check console output at:
                    ${env.BUILD_URL}console
                    have a basd syntax
                    """,
                            //recipientProviders: [[$class: recipientProvider]],
                            to: recipient
                       )
                    }
                }
            }
        }
        stage ('Deploy') {
            // when 
            steps {
                withCredentials(
                    bindings: [
                        sshUserPrivateKey(
                            credentialsId: 'd837ece2-b033-47db-97e1-4bb122adf8ee',
                            keyFileVariable: 'SSH_KEY',
                            passphraseVariable: '',
                            usernameVariable: 'SSH_USER'
                        )
                    ]
                ) {
                    sh "ssh -i $SSH_KEY $SSH_USER@localhost 'docker run -d -p 80:79 --name pipe ngnix' "
                }
                
            }
        }
    }

    post {
        failure {
            emailext(
                subject: "FAILED: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]",
                body:
        """
        FAILED: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]
        Check console output at:
        ${env.BUILD_URL}console
        """,
                //recipientProviders: [[$class: recipientProvider]],
                to: recipient
            )

        }
        always {
            cleanWs()
        }
    }
}