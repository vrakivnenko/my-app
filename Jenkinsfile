#!/usr/bin/env groovy
pipeline {
    agent any
    triggers { pollSCM('*/2 * * * *') }
    stages {
        stage ('print branch name') {
            echo BRANCH_NAME
        }
    }
}