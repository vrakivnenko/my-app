#!/usr/bin/env groovy

// Name of recipents who will recive message if build will fail (can be changed to jenkins enviroment)
recipient = 'rakivnenko81@gmail.com'
//name of container which will be started in deploy stage
container_name = 'test_http'
//provide the name of script for syntax check (can be changed to PATH in git repo)
check_file = 'script.py'
pipeline {
    agent any
    // use trigger becouse runnnig jenkins locally and unable to configure hooks with github
    triggers { pollSCM('*/2 * * * *') }

    stages {
        // branch for test purpose
        stage ('print branch name') {
            steps {
                echo BRANCH_NAME
            }
        }
        stage ('Status check for pull request') {
            when {
                expression { BRANCH_NAME =~ /^PR-/ }
            }
            steps {
                echo 'We have a new pull requests. Need to run some tests on it'
                script {
                    //change status to pending for unable merge till tests finished 
                    pullRequest.createStatus(
                        'pending',
                        'somme',
                        'text', 
                        "${env.BUILD_URL}console"
                    ) 
                }
                script {
                    //run dummy test to make sure it changed status depending on test result 
                    def result_of_test = sh (
                        script: "./script.sh",
                        returnStdout: true
                    ).trim()
                    if (result_of_test == "0") {
                        echo 'you pass all required tests'
                        pullRequest.createStatus(
                            'success',
                            'pr_tests',
                            'run few tests to check it', 
                            "${env.BUILD_URL}console"
                        ) 
                    } else {
                        pullRequest.createStatus(
                            'failure',
                            'pr_tests',
                            'run few tests to check it', 
                            "${env.BUILD_URL}console"
                        ) 
                    }
                }

            }
        }
        //to be executed require all branch name start from "fb-"
        stage ('Regular branch') {
            when {
                expression { BRANCH_NAME =~ /^fb-/ }
            }
            steps {
                script {
                    // check type of script to execute right shared library syntax test 
                    if (check_file =~ /sh$/) {
                        def test_result = test()
                        if (test_result == "0") {
                            println "your script have good syntax"
                        } else {
                            // sending email if fail
                            emailext(
                                subject: "FAILED TEST: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]",
                                body:
                        """
                        FAILED: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]
                        Check console output at:
                        ${env.BUILD_URL}console
                        have a bad syntax
                        """,
                                to: recipient
                            )
                        }
                    } else if (check_file =~ /py$/) {
                        def test_result = py_check(check_file)
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
                        have a bad syntax
                        """,
                                to: recipient
                            )
                        }
                    }
                }
            }
        }
        stage ('Deploy') {
            when {
                expression { BRANCH_NAME == 'master' }
            }
            steps {
                // using ssh credentials from Jenkins for ssh to a node and deploy container
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
                    script {
                        // Check is container already exist or not
                        def check_container = sh ( script: "ssh -i $SSH_KEY $SSH_USER@localhost 'docker ps -a | grep $container_name'", returnStatus: true )
                        if (check_container) {
                            sh "ssh -i $SSH_KEY $SSH_USER@localhost 'docker run -d -p 80:80 --name $container_name httpd' "
                         } else {
                            echo "You already have container $container_name"
                        }
                        // sh "ssh -i $SSH_KEY $SSH_USER@localhost '
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
                to: recipient
            )
        }
        always {
            // clean workspace
            cleanWs()
        }
    }
}