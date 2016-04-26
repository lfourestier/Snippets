def States = ['NONE', 'DEV', 'TEST', 'QA']

// get the parameters
def BuildPackage = System.getenv("PARAM_BUILD_PACKAGE")
def NextEnv = System.getenv("PARAM_ENVIRONMENT")
def NextEnvIndex = States.indexOf(NextEnv)
println "PARAM_BUILD_PACKAGE: " + BuildPackage
println "PARAM_ENVIRONMENT: " + NextEnv

// get the map from the file
def StateMap = [:]
def MapFile = new File("LFOR_CopyArtifacts.txt")
MapFile.splitEachLine(",") {fields ->
  StateMap[fields[0].trim()] = fields[1].trim()
}
println "Read ENV: " + StateMap

// Get the current ENV of the package
def CurrentEnv = States[0]
if (StateMap[BuildPackage]) {
  CurrentEnv = StateMap[BuildPackage]
}
def CurrentEnvIndex = States.indexOf(CurrentEnv)
if (CurrentEnvIndex == -1) {
  println "Unknown env for " + BuildPackage + " : " + CurrentEnv
  System.exit(1)
}

if (NextEnvIndex <= CurrentEnvIndex+1) {
  // Do the promotion  println
  println "Promoting and deploying " + BuildPackage + " to " + NextEnv
  StateMap[BuildPackage] = NextEnv
  
  // Write back the file
  MapFile.withWriter{ out ->
        StateMap.each { entry ->
        out.println "$entry.key,$entry.value"
    }
  }
  println "Written back ENV: " + StateMap
}
else {
  println "Promotion forbidden!"
  System.exit(1)
}

System.exit(0)

