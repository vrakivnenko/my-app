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
                        'failure',
                        'somme',
                        'text', 
                        'https://github.com/vrakivnenko/my-app/pull/2'
                    ) 
                }
                sh "./script.sh"
                script {
                    pullRequest.createStatus(
                        'success',
                        'somme',
                        'text', 
                        'https://github.com/vrakivnenko/my-app/pull/2'
                    ) 
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
                    }
                script {
                    if (test_result) {
                        println "your script have good syntax"
                    } else {
                        return false
                    }
                }
                // user_input = input "Does staging looking good?"
                // script {
                //     if (user_input == "Yes") {
                //         echo "Start deployment"
                //     } else {
                //         echo "Make it right!"
                //         sh "exit 2"
                //     }
                // }
            }
        }
    //     stage ('Deploy') {
    //         when 
    //     }
    // }

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