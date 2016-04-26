// Slave monitoring
for (aSlave in hudson.model.Hudson.instance.slaves) {
    println('Name: ' + aSlave.name);
    println('getNumExectutors: ' + aSlave.getNumExecutors());
}

// Master monitoring
Runtime runtime = Runtime.getRuntime();
println('Max memory: ' + runtime.maxMemory())
println('Total memory: ' + runtime.totalMemory())
println('Free memory: ' + runtime.freeMemory())

// Executor monitoring
Jenkins jenkins = Jenkins.instance
hudson.model.OverallLoadStatistics load = jenkins.overallLoad
println('Busy executors: ' + Math.round(load.busyExecutors.getLatest(hudson.model.MultiStageTimeSeries.TimeScale.SEC10)))
println('Online executors: ' + Math.round(load.onlineExecutors.getLatest(hudson.model.MultiStageTimeSeries.TimeScale.SEC10)))




