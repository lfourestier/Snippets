println "Building " + System.env.get('JOB_NAME')

def Duration = System.env.get('PARAM_DURATION').toInteger()

println "### PARAM_DURATION: " + Duration

// Trigger a failure after a while
//def build_to_fail = System.env.get('PARAM_BUILD_TO_FAIL').toInteger()
//def build_number = System.env.get('BUILD_NUMBER').toInteger()
//def before_failure = build_to_fail-build_number
//println "Still to go before failure: " + before_failure
//def fail_number = 1/(before_failure)

for(int i=0; i<Duration; i++) {
    // Keep busy a while
    int a=0;
    for(int j=0; j<1000; j++) {
        a=+ j;
    }
    // Then sleep
    Thread.sleep(1000);
}
