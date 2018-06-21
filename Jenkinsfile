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
                    def return_code = sh "./script.sh"
                    sh "echo $return_code"
                sh "sleep 20"
                script {
                    if (ca == "0") {
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