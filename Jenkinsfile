#!/usr/bin/env groovy
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
            steps {
                when { BRANCH_NAME =~ '^PR' }
                echo 'We have a new pull requests. Need to run some tests on it'
                test (BRANCH_NAME)
            }
        }
        stage ('Regular branch') {
            steps {
                when {
                    BRANCH_NAME ==~ '^FB'
                }
                test (BRANCH_NAME)
            }
        }
    }
}