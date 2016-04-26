println "Running " + System.env.get('JOB_NAME')

// Execute on a slave
node("DEV-JAVA") {
    for(int i=0; i<20; i++) {
        // Keep busy a while
        int a=0;
        for(int j=0; j<10000; j++) {
            a=+ j;
        }
        // Then sleep
        Thread.sleep(500);
    }
}

// Execute on the master
for(int i=0; i<20; i++) {
    // Keep busy a while
    int a=0;
    for(int j=0; j<10000; j++) {
        a=+ j;
    }
    // Then sleep
    Thread.sleep(500);
}
