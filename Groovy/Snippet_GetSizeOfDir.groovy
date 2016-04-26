import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import groovy.io.FileType

// Change Directory
def setCurrentDirectory(new_dir_path) { 
    System.setProperty("user.dir", new_dir_path);
}

// List file in directory
def listFile(dir_path) {
    def list = []

    def dir = new File(dir_path)
    dir.eachFile { file ->
        list << file
        println file
    }
    
    return list
}

// ### Main ###
setCurrentDirectory ("E:\\Jenkins\\jobs\\Local_V2\\jobs\\Development\\jobs")
def cwd = System.getProperty("user.dir")
println cwd

dir_list = ["\\Easy-Banking-Web\\jobs\\EBIA\\jobs\\EBIA-pr01", "\\Easy-Banking-Web\\jobs\\PFAL\\jobs\\PFAL-ap01", "\\Easy-Banking-Web\\jobs\\PFPL\\jobs\\PFPL-pr01",
            "\\Easy-Banking-Web\\jobs\\TFAL\\jobs\\TFAL-ap01", "\\Easy-Banking-Web\\jobs\\TFPL\\jobs\\TFPL-pr01", "\\EBAS\\jobs\\EBAS\\jobs\\EBAS-AP50", "\\EBAS\\jobs\\EBPG\\jobs\\EBPG-ap01",
            "\\FE-Daily\\jobs\\DBAL\\jobs\\DBAL-ap01", "\\FE-Daily\\jobs\\DBPL\\jobs\\DBPL-pr01"]


dir_list.each {
    println cwd + it
    Path p = Paths.get(cwd + it)
    if (Files.exists(p) || Files.isDirectory(p)) {
        BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes)
        println "Size: ${Files.size(p)}"
    }
}