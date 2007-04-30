/**
 * Created Apr 29, 2007
 * By Josh Kropf
 * Copyright josh@slashdev.ca
 */
package ca.slashdev.bb;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

/**
 * 
 * @author josh
 */
public class SigtoolTask extends BaseTask
{
   private File sigtoolJar;
   
   private boolean forceClose = false;
   private boolean close = true;
   private boolean request = true;
   
   private File codFile;
   private Path cods;
   
   @Override
   public void init() throws BuildException {
      cods = new Path(getProject());
      super.init();
   }
   
   @Override
   public void setJdeHome(File jdeHome) {
      super.setJdeHome(jdeHome);
      
      File bin = new File(jdeHome, "bin");
      if (bin.isDirectory()) {
         sigtoolJar = new File(bin, "SignatureTool.jar");
      } else {
         throw new BuildException("jde home missing \"bin\" directory");
      }
   }
   
   public void setForceClose(boolean forceClose) {
      this.forceClose = forceClose;
   }
   
   public void setClose(boolean close) {
      this.close = close;
   }
   
   public void setRequest(boolean request) {
      this.request = request;
   }
   
   public void setCodFile(File codFile) {
      this.codFile = codFile;
   }
   
   public void addCod(Path codPath) {
      cods.add(codPath);
   }
   
   @Override
   public void execute() throws BuildException {
      if (jdeHome == null) {
         throw new BuildException("jdehome not set");
      }
      
      if (cods.size() == 0 && codFile == null) {
         throw new BuildException("cods attribute or <cod> element required!");
      }
      
      if (codFile != null && cods.size() > 0) {
         throw new BuildException("codfile attribute cant be used in conjunction with <cod> element");
      }
      
      if (codFile != null) {
         String codFilePath = codFile.getAbsolutePath();
         File touchFile = new File(codFilePath.replace(".cod", ".signed"));
         if (!FileUtils.getFileUtils().isUpToDate(codFile, touchFile)) {
            cods.add(new Path(getProject(), codFilePath));
            executeSigtool();
            
            new FileResource(touchFile).touch(System.currentTimeMillis());
         } else {
            log("cod file already signed");
         }
      } else {
         executeSigtool();
      }
   }
   
   private void executeSigtool() {
      Java java = (Java)getProject().createTask("java");
      java.setTaskName(getTaskName());
      
      java.setFailonerror(true);
      java.setFork(true);
      java.setJar(sigtoolJar);
      
      if (forceClose) java.createArg().setValue("-C");
      if (close) java.createArg().setValue("-c");
      if (request) java.createArg().setValue("-a");
      
      for (String file : cods.list()) {
         java.createArg().setFile(new File(file));
      }
      
      java.execute();
   }
}
