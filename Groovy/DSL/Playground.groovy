import Utilities.BuildUnitTestJob
import Utilities.ReleaseJob

new BuildUnitTestJob(
    dir_path: "",
    svn_url: "https://svn-qa.resnp.sysnp.shared.fortis/svn/TF01/phase3/presentation/branches/v2",
    nexus_url: "http://i-net1102e-qa:8081/nexus/content/repositories",
    nexus_snapshot_repo: "BNPPF_Local_Applications_Snapshots",
).Create(this)

new ReleaseJob(
    dir_path: "",
    svn_url: "https://svn-qa.resnp.sysnp.shared.fortis/svn/TF01/phase3/presentation/branches/v2",
    svn_user: "svnqa-techuser",
).Create(this)