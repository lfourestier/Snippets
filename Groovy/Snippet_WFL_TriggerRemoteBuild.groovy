// Call a job remotely (from a master to another)$
// Full URL is of the form: 
//      - "http://${user}:${user_api_token}@${jenkins_url}/${job_path}/build?token=${job_token}"
//      - "http://${user}:${user_api_token}@${jenkins_url}/${job_path}/buildWithParameters?token=${job_token}&${build_param}=${value}"

// URL parts
def user = "g14981"
def user_api_token = "ee532d358cad0c65585337144a0f43f5"
def jenkins_url = "sdaw0802.resnp.sysnp.shared.fortis:8080"
def job_path = "jenkins/job/PLAYGROUND/job/LFOR/job/Trials/job/LFOR_Free"
def job_token = "LFOR_TOKEN"

// Add build parameters
def build_number = env.BUILD_NUMBER
def build_params = ["PARAM_STRING":"LFOR_QA_${build_number}"]

// Create URL
def url = "http://"

if (user_api_token == null || user_api_token == "") {
    url += "${jenkins_url}/${job_path}"
}
else {
    url += "${user}:${user_api_token}@${jenkins_url}/${job_path}"
}

if (build_params.size() > 0) {
    url += "/buildWithParameters?token=${job_token}"
    build_params.each{k,v -> url += "&${k}=${v}" }
}
else {
    url += "/build?token=${job_token}"
}

// Call URL
stage "Remote Call"
println "URL: " + url
def data = new URL(url).getText()