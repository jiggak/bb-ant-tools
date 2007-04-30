/**
 * Created Apr 28, 2007
 * By josh
 * Copyright 2005 Slashdev.ca
 */
package ca.slashdev.bb;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * @author josh
 */
public class RapcTask extends BaseTask
{
   private File rapcJar;
   
   private File destDir;
   private String output;
   private boolean quiet;
   
   private Path srcs;
   private Path imports;
   
   private JdpType jdp = new JdpType();
   
   @Override
   public void init() throws BuildException {
      srcs = new Path(getProject());
      imports = new Path(getProject());
      super.init();
   }
   
   @Override
   public void setJdeHome(File jdeHome) {
      super.setJdeHome(jdeHome);
      
      File bin = new File(jdeHome, "bin");
      if (bin.isDirectory()) {
         rapcJar = new File(bin, "rapc.jar");
      } else {
         throw new BuildException("jde home missing \"bin\" directory");
      }

      File lib = new File(jdeHome, "lib");
      if (lib.isDirectory()) {
         Path apiPath = new Path(getProject());
         apiPath.setLocation(new File(lib, "net_rim_api.jar"));
         imports.add(apiPath);
      } else {
         throw new BuildException("jde home missing \"lib\" directory");
      }
   }
   
   public void setOutput(String output) {
      this.output = output;
   }
   
   public void setQuiet(boolean quiet) {
      this.quiet = quiet;
   }
   
   public void setDestDir(File destDir) {
      this.destDir = destDir;
   }
   
   public void setSrcDir(File srcDir) {
      FileSet srcFiles = new FileSet();
      srcFiles.setDir(srcDir);
      srcs.addFileset(srcFiles);
   }
   
   public void setImport(Path importPath) {
      imports.add(importPath);
   }
   
   public void setImportRef(Reference importRef) {
      Object obj = importRef.getReferencedObject(getProject());
      
      if (!(obj instanceof Path)) {
         throw new BuildException("importref must be a path");
      }
      
      imports.add((Path)obj);
   }
   
   public void addSrc(Path srcPath) {
      srcs.add(srcPath);
   }
   
   public void addImport(Path importPath) {
      imports.add(importPath);
   }
   
   public void addJdp(JdpType jdp) {
      this.jdp = jdp;
   }
   
   public void execute() throws BuildException {
      if (jdeHome == null) {
         throw new BuildException("jdehome not set");
      }
      
      if (output == null) {
         throw new BuildException("output is a required attribute");
      }
      
      if (destDir == null) {
         destDir = getProject().getBaseDir();
      } else {
         if (!destDir.isDirectory()) {
            throw new BuildException("destdir must be a directory");
         }
      }
      
      if (srcs.size() == 0) {
         throw new BuildException("srcdir attribute or <src> element required!");
      }
      
      // create manifest file to be passed to rapc command
      jdp.writeManifest(new File(destDir, output+".rapc"), output);
      
      if (isOutOfDate(srcs, new File(destDir, output+".cod"))) {
         executeRapc();
      } else {
         log("cod file up to date", Project.MSG_VERBOSE);
      }
   }
   
   protected void executeRapc() {
      Java java = (Java)getProject().createTask("java");
      java.setTaskName(getTaskName());
      
      // must fork in order to set working directory
      java.setFork(true);
      java.setDir(destDir);
      
      // set rapc executable jar file
      java.setJar(rapcJar);
      
      if (quiet) java.createArg().setValue("-quiet");
      
      java.createArg().setValue("import="+imports.toString());
      java.createArg().setValue("codename="+output);
      java.createArg().setValue(output+".rapc");
      
      // add each of the items in the srcs path as file args
      for (String file : srcs.list()) {
         java.createArg().setFile(new File(file));
      }
      
      java.execute();
   }
}
