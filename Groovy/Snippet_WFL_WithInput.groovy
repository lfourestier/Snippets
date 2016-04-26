stage '? Promote (TEST)'
timeout(time:5, unit: 'MINUTES') {
  checkpoint 'Promote (TEST)'
  input "Can we deploy in TEST?"
}
println "Deploying build package: " + buildPackage + " in TEST (build number: " + Number + ")."
def PromoteJob = build job: "Promote", parameters: [[$class: 'StringParameterValue', name: 'PARAM_BUILD_PACKAGE', value: "$buildPackage"], [$class: 'StringParameterValue', name: 'PARAM_ENVIRONMENT', value: "TEST"]]