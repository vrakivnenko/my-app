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
                sh "./script.sh"
            }
        }
        stage ('Regular branch') {
            when {
                expression { BRANCH_NAME =~ /^fb-/ }
            }
            steps {
                script {
                    def test_result = test(BRANCH_NAME)
                    if (test_result == 'pass') {
                        println "your script have good syntax"
                    } else {
                        return false
                    }
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