package Utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class ReleaseJob {
    
    String dir_path
    String svn_url
    String svn_user
    
    // Create Release
    Job Create(DslFactory dslFactory) {
        dslFactory.mavenJob (dir_path + "Release") {
            // Description
            description("Release with maven release plugin")
            
            // Discard old builds
            logRotator {
                numToKeep(30)
                artifactNumToKeep(1)
            }
            
            // Restrict where this project can be run
            label ("DEV-JAVA")
            
            // JDK
            jdk ("JavaEE-7")
            
            // This build is parameterized
            parameters {
                stringParam("PARAM_TAG_PREFIX", null, "See SVN guidelines (SVN_TAG = PARAM_TAG_PREFIX . JENKINS_BUILD_NUMBER) [Mandatory]")
                stringParam("PARAM_NEXT_VERSION", null, "Next version [Mandatory]")
            }
            
            // Subversion
            scm { 
                svn { 
                    location (svn_url) { 
                        credentials("2e99a81b-1a58-43d0-80ad-96c914aa8652") 
                    } 
                } 
            } 
            
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
             // Inject passwords to the build as environment variables
//                injectPasswords() // TODO: Refactor. Only with 1.45
            }
            // TODO Remove: see above
            configure { project ->
                project / 'buildWrappers' / 'EnvInjectPasswordWrapper' {
                    'injectGlobalPasswords'('false')
                    'maskPasswordParameters'('true')
                    'passwordEntries' {
                        'EnvInjectPasswordEntry' {
                            'name'('SVN_PWD')
                            'value'('K+8bBBElCFGdBKuy2ZEvS/B7tQ5XrGE57xXhPgOcWNk=')
                        }
                    }
                }
            }
            
            // Pre Steps
            preBuildSteps {
                // Inject environment variables
                environmentVariables {
                    env('SVN_TAG', '\$PARAM_TAG_PREFIX.\$BUILD_NUMBER')
                }
                // Invoke top level maven target
                maven {
                    mavenInstallation("maven-3.0.4")
                    rootPOM('pom.xml')
                    goals("release:clean")
                    configure { maven -> // TODO To be refactored with "providedSettings("settings-devfmk.xml")"
                        'usePrivateRepository'('true')
                        maven / 'settings'(class: "jenkins.mvn.FilePathSettingsProvider") {
                            'path'('\$SETTING')
                        }
                    }    
                }
                // Invoke top level maven target
                maven {
                    mavenInstallation("maven-3.0.4")
                    rootPOM('pom.xml')
                    goals("-DdryRun=true -Dresume=false -Dusername=$svn_user -Dpassword=\$SVN_PWD -Dtag=\$SVN_TAG -DdevelopmentVersion=\$PARAM_NEXT_VERSION --batch-mode release:prepare -Dgoals=package-Darguments=\"-DskipTests -Dmaven.javadoc.skip=true -X help:active-profiles help:all-profiles help:effective-pom help:effective-settings help:system\"")
                    configure { maven -> // TODO To be refactored with "providedSettings("settings-devfmk.xml")"
                        'usePrivateRepository'('true')
                        maven / 'settings'(class: "jenkins.mvn.FilePathSettingsProvider") {
                            'path'('\$SETTING')
                        }
                    }    
                }
            }
            
            // Build
            mavenInstallation("maven-3.0.4")
            rootPOM('pom.xml')
            goals("--batch-mode release:prepare -Dresume=false -Dusername=$svn_user -Dpassword=\$SVN_PWD -Dtag=\$SVN_TAG -DdevelopmentVersion=\$PARAM_NEXT_VERSION -Dgoals=package -Darguments=\"-DskipTests -Dmaven.javadoc.skip=true -X help:active-profiles help:all-profiles help:effective-pom help:effective-settings help:system\"")
            mavenOpts("-Xmx1024m -Xss1024k -XX:MaxPermSize=512m -Dmaven.test.failure.ignore=false")
            configure { project ->  // TODO To be refactored with "providedSettings("settings-devfmk.xml")"
                project / 'settings'(class: "jenkins.mvn.FilePathSettingsProvider") {
                    'path'('\$SETTING')
                }
            }    
            
            // Post steps
            postBuildSteps ('FAILURE') {
                // Invoke top level maven target
                maven {
                    mavenInstallation("maven-3.0.4")
                    rootPOM('pom.xml')
                    goals("release:perform -Dresume=false -Dusername=$svn_user -Dpassword=\$SVN_PWD -Dgoals=deploy -Darguments=\"-DskipTests -Dmaven.javadoc.skip=true -X help:active-profiles help:all-profiles help:effective-pom help:effective-settings help:system\"")
                    configure { maven -> // TODO To be refactored with "providedSettings("settings-devfmk.xml")"
                        'usePrivateRepository'('true')
                        maven / 'settings'(class: "jenkins.mvn.FilePathSettingsProvider") {
                            'path'('\$SETTING')
                        }
                    }    
                }
                // Execute windows batch command
                batchFile('echo OFF\necho ********************\necho Released %PARAM_TAG_PREFIX% with tag %SVN_TAG%. Next version is %PARAM_NEXT_VERSION%.\necho ********************')
            }
            
            // Post-build actions
            publishers {
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