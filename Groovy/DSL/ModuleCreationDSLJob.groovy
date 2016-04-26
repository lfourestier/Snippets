import Utilities.BuildUnitTestJob
import Utilities.ReleaseJob

// ### MAIN ###

// ################
// Read the setup configuration
def config = [:]
def config_txt = readFileFromWorkspace(PARAM_CONFIG)
println config_txt
config_txt.splitEachLine("=") {fields ->
    config[fields[0].trim()] = fields[1].trim()
}

// TODO: Verify value's validity before!!!
def root_folder = ""
if (config['ROOT_FOLDER'] != "" && config['ROOT_FOLDER'] != null) {
    root_folder = config['ROOT_FOLDER'] + "/"
}
def application = config['APPLICATION']
def module_list = config['MODULES'].tokenize(',')
def group = config['GROUP']
def group_role = config['GROUP_ROLE']
def user_list = config['USER_LIST'].tokenize(',')
def svn_url = config['SVN_URL']
def svn_user = config['SVN_USER']
def nexus_url = config['NEXUS_URL']
def nexus_snapshot_repo = config['NEXUS_SNAPSHOT_REPO']
def nexus_release_repo = config['NEXUS_RELEASE_REPO']

// ################
// Create the top application folder with security
def app_folder = root_folder + application
println "Creating application folder ${app_folder}"
folder (app_folder) {
    configure { folder ->
        if (group != "") {
            folder / 'properties' / 'com.cloudbees.hudson.plugins.folder.properties.FolderProxyGroupContainer'(plugin: "nectar-rbac@5.6") / 'groups' / 'nectar.plugins.rbac.groups.Group' {
                'name'(group)
                'role'(group_role)
                user_list.each { user ->
                    'member'(user)
                }
            }
        }
    }
}
app_folder += "/"
        
// ################
// Construct the application module folder tree
module_list.each { module ->
    // Create module tree
    dir_list = module.tokenize('/')
    dir_path = app_folder
    dir_list.each { dir ->
        dir_path += dir
        println "Creating folder ${dir_path}"
        folder (dir_path)
        dir_path += "/"
    }
    
    // ################
    // POC job
    job (dir_path + "HelloWorld") {
        // Restrict where this project can be run
        label ("DEV-JAVA")
        
        // Build
        steps {
            groovyCommand("println \"Hello world!\"") {
                groovyInstallation('groovy-1.8.6')
            }
        }
    }
    
    // ################
    // Build_Unit_Tests
    new BuildUnitTestJob(
        dir_path: dir_path,
        svn_url: svn_url,
        nexus_url: nexus_url,
        nexus_snapshot_repo: nexus_snapshot_repo,
    ).Create(this)
    
    // ################
    // Create Release 
    new ReleaseJob(
        dir_path: dir_path,
        svn_url: svn_url,
        svn_user: svn_user,
    ).Create(this)  
    
    // ################
    // TODO: Complete 
    // Create Feed
    job (dir_path + "Feed")
    
    // ################
    // TODO: Complete 
    // Create Deploy
    job (dir_path + "Deploy")
    
    // ################
    // Create Release_Workflow
    workflowJob(dir_path + "Release_Workflow") {
        // Description
        description("Pipe the release job and deploy")
        
        // Discard old builds
        logRotator {
            numToKeep(30)
        }
        
        // Pipeline
        definition {
            cps {
                script(readFileFromWorkspace("ReleaseWorkflow.groovy")) // Get it from svn
            }
        }
    }
}


