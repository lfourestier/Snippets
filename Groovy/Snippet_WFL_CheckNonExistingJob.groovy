stage "Existing job"
build "LFOR_Free"

stage "Non exiting job"
try {
    build "NonExisting"
}
catch (hudson.AbortException e) {
    println e.message
}