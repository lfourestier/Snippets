package Utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class BuildUnitTestJob {
    
    String dir_path
    String svn_url
    String nexus_url
    String nexus_snapshot_repo
    
    // Create Build_Unit_Tests
    // Remark: In the order that you see it under Jenkins on configuration view!!
    Job Create(DslFactory dslFactory) {
        
        dslFactory.mavenJob (dir_path + "Build_Unit_Test") {
            // Description
            description("Build and unit test")
            
            // Discard old builds
            logRotator {
                numToKeep(5)
                artifactNumToKeep(5)
            }
            
            // JDK
            jdk ("JavaEE-7")
            
            // Restrict where this project can be run
            label ("DEV-JAVA")
            
            // Subversion
            scm { 
                svn { 
                    location (svn_url) { 
                        credentials("2e99a81b-1a58-43d0-80ad-96c914aa8652") 
                    } 
                } 
            }
            
            // Properties
            // Link it to template
//          configure { project -> 
//              project / 'properties' / 'com.cloudbees.hudson.plugins.modeling.impl.jobTemplate.JobPropertyImpl'(plugin: "cloudbees-template@4.21") / 'instance' {
//                  'model'(root_folder + "JT-DEV-WASA-CB")
//                  'values'(class: "tree-map") {
//                      // Attributes
//                      'entry' {
//                          'string'('name')
//                          'string'('Build_Unit_Tests')
//                      }
//                  }
//              }
//          }
            
            // Build Environment
            wrappers {
                // Delete workspace before build starts
                preBuildCleanup {
                    includePattern('*/')
                    deleteDirectories()
                }
                // Provide Configuration files
                configFiles {
                    mavenSettings("settings-devfmk.xml") {
                        variable("SETTING")
                    }  
                }
            }
            
            // Pre steps

            // Build
            mavenInstallation("maven-3.0.4")
            rootPOM('pom.xml')
            goals ("clean package")
            mavenOpts("-Xmx1024m -Xss1024k -XX:MaxPermSize=512m -Dmaven.test.failure.ignore=false")
            configure { project -> // TODO To be refactored with "providedSettings("settings-devfmk.xml")"
                project / 'settings'(class: "jenkins.mvn.FilePathSettingsProvider") {
                    'path'('\$SETTING')
                }
            } 
            
            // Post steps
            
            // Post-build actions
            publishers {
                // Deploy artifacts to maven repository
                deployArtifacts {
                    repositoryUrl("${nexus_url}/${nexus_snapshot_repo}")
                    repositoryId(nexus_snapshot_repo)
                    uniqueVersion()
                }
                // Delete workspace when build done
                wsCleanup {
                    includePattern('*/')
                    deleteDirectories(true)
                    cleanWhenAborted(true)
                    cleanWhenFailure(true)
                    cleanWhenNotBuilt(true)
                    cleanWhenSuccess(true)
                    cleanWhenUnstable(true)
                }
            }
        }
    }
}