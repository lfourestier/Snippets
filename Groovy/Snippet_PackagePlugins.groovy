import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import groovy.io.FileType
import java.util.zip.ZipOutputStream  
import java.util.zip.ZipEntry  
import java.nio.channels.FileChannel  

// Change Directory
def setCurrentDirectory(new_dir_path) { 
    System.setProperty("user.dir", new_dir_path);
}

// List file in directory
def listFile(dir_path, filter) {
    def list = []
    def filter_list = filter.tokenize(',')

    def dir = new File(dir_path)
    dir.eachFile { file ->
        filter_list.each { filt ->
            if (file.name.endsWith(filt)) {       
                list << file
                println file
            }
        }
    }
    
    return list
}

// ### MAIN ###
//setCurrentDirectory ("E:\\Jenkins\\plugins")
//def cwd = System.getProperty("user.dir")
//println cwd

// Get jpi file from plugins directory
listFile("E:\\Jenkins\\plugins", ".jpi,.jpi.pinned,.jpi.disabled")


// Create zip
ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream("Plugins.zip"))

