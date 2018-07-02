#!/usr/bin/env groovy

import jenkins.model.*
import hudson.security.*
import jenkins.install.*;
import hudson.triggers.SCMTrigger;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

def instance = Jenkins.getInstance()

println "--> creating local user 'admin'"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin','admin')
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()

instance.setAuthorizationStrategy(strategy)
instance.save()

jenkins = Jenkins.instance;

workflowJob = new WorkflowJob(jenkins, "workflow2");
jobName = "create-dsl-job2";
gitTrigger = new SCMTrigger("* * * * *");

dslProject = new hudson.model.FreeStyleProject(jenkins, jobName);
dslProject.addTrigger(gitTrigger);
jenkins.add(dslProject, jobName);
job = jenkins.getItem(jobName)
builders = job.getBuildersList()

hudson.tasks.Shell newShell = new hudson.tasks.Shell("echo \"Hello\" ")
builders.replace(newShell)

gitTrigger.start(dslProject, true);
