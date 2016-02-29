package com.BrowserStack;

import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

class LocalBinary {
  
  String http_path;
  String dest_parent_dir;
  String binary_path;
  String osname;
  String arch; 
  String orderedPaths[] = { 
    System.getProperty("user.home")+"/.browserstack", 
    System.getProperty("user.dir"),
    System.getProperty("java.io.tmpdir") 
  };
  
  LocalBinary() throws Exception {
    initialize();
    getBinary();
  }
  
  void initialize(){
      osname = System.getProperty("os.name");
      arch = System.getProperty("os.arch");

      if(osname.contains("Mac") || osname.contains("Darwin"))
        http_path="https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip";
      
      else if(osname.contains("Windows"))
        http_path="https://www.browserstack.com/browserstack-local/BrowserStackLocal-win32.zip";
      
      else if (osname.contains("Linux") && arch.contains("64"))
        http_path="https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip";
      
      else
        http_path="https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip";
  }
  
  Boolean getBinary() throws Exception {
    dest_parent_dir = getAvailableDirectory();
    
    if (osname.contains("Windows"))
      binary_path = dest_parent_dir + "/BrowserStackLocal.exe";
    else
      binary_path = dest_parent_dir + "/BrowserStackLocal";
    
    if (new File(binary_path).exists())
      return true;
    else
      return downloadBinary(dest_parent_dir);
      
  }
  
  String getAvailableDirectory() throws Exception {
    int i=0;
    while(i<orderedPaths.length){
      String path = orderedPaths[i]; 
      if (makePath(path))
        return path;
      else
        i++;
    }
    throw new BrowserStackLocalException("Error trying to download BrowserStackLocal binary");
  }

  boolean makePath(String path) {
    try {
      if (!new File(path).exists())
        new File(path).mkdirs();
      return true;
      
    }
    catch (Exception e){
      return false;
    }
  }

  Boolean downloadBinary(String dest_parent_dir) throws BrowserStackLocalException{
    try{  
      if (!new File(dest_parent_dir).exists())
        new File(dest_parent_dir).mkdirs();
      
      System.out.println("Downloading Local Testing binaries...");
      URL url = new URL(http_path);
      String source = dest_parent_dir + "/Download.zip";
      File f = new File(source);
      FileUtils.copyURLToFile(url, f);
      
      System.out.println("Unzipping...");
      unzipFile(source, dest_parent_dir);
      System.out.println("Changing permissions...");
      changePermissions(binary_path);
      System.out.println("Download complete.");
      
      return true;
    }
    catch (Exception e){
      System.out.println(e);
      throw new BrowserStackLocalException("Error trying to download BrowserStackLocal binary");
    }
  }

  void unzipFile(String source, String dest) {
    try {
         ZipFile zipFile = new ZipFile(source);
         zipFile.extractAll(dest);    
    } 
    catch (ZipException e) {
        e.printStackTrace();
    }
  }
  
  void changePermissions (String path){
    File f = new File(path);
    f.setExecutable(true, true);
    f.setReadable(true, true);
    f.setWritable(true, true);
  }
}
