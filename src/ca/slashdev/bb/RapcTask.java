/*
 * Copyright 2007 Josh Kropf
 * 
 * This file is part of bb-ant-tools.
 * 
 * bb-ant-tools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * bb-ant-tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with bb-ant-tools; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ca.slashdev.bb;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * @author josh
 */
public class RapcTask extends BaseTask
{
   private File jdkHome;
   
   private File rapcJar;
   
   private File destDir;
   private String output;
   private boolean quiet;
   
   private Path srcs;
   private Path imports;

   private File exePath;
   
   private JdpType jdp = new JdpType();
   
   @Override
   public void init() throws BuildException {
      srcs = new Path(getProject());
      imports = new Path(getProject());
      super.init();
   }
   
   /**
    * Sets the jde home directory and attempts to locate the rapc.jar file
    * and the net_rim_api.jar file.
    */
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
   
   /**
    * Sets the jdk (or jre) home directory of the jvm to use for launching
    * the rapc command.  Set this property if the rim jde requires an older
    * version of the jvm.
    * @param jdkHome jdk home directory
    */
   public void setJdkHome(File jdkHome) {
      File bin = new File(jdeHome, "bin");
      if (!bin.isDirectory()) {
         throw new BuildException("jdk home missing \"bin\" directory");
      }
      
      this.jdkHome = jdkHome;
   }

   /** 
    * Sets the path for the preverify tool.
    */
   public void setexePath(File exePath) {
      this.exePath = exePath;
   }
   
   /**
    * Sets output name (eg: ca_slashdev_MyApp).  This name is used by
    * the rapc compiler to create various output files such as the .cod,.cso,
    * and .jar files.
    * @param output
    */
   public void setOutput(String output) {
      this.output = output;
   }
   
   /**
    * Tells the rapc compiler to be less chatty, default is false.
    * @param quiet
    */
   public void setQuiet(boolean quiet) {
      this.quiet = quiet;
   }
   
   /**
    * Sets working directory in which to run rapc compiler.  All output
    * files will be generated here.
    * @param destDir
    */
   public void setDestDir(File destDir) {
      this.destDir = destDir;
   }
   
   /**
    * Creates an implicit FileSet and adds it to the path of source files.
    * @param srcDir
    */
   public void setSrcDir(File srcDir) {
      FileSet srcFiles = new FileSet();
      srcFiles.setDir(srcDir);
      srcs.addFileset(srcFiles);
   }
   
   /**
    * Adds importPath to the path of import jars.
    * @param importPath
    */
   public void setImport(Path importPath) {
      imports.add(importPath);
   }
   
   /**
    * Adds to path of import jars by reference.  If the referenced object
    * is not a Path object, an exception is raised.
    * @param importRef
    */
   public void setImportRef(Reference importRef) {
      Object obj = importRef.getReferencedObject(getProject());
      
      if (!(obj instanceof Path)) {
         throw new BuildException("importref must be a path");
      }
      
      imports.add((Path)obj);
   }
   
   /**
    * Add srcPath to the path of source files.
    * @param srcPath
    */
   public void addSrc(Path srcPath) {
      srcs.add(srcPath);
   }
   
   /**
    * Add importPath to the path of import jars.
    * @param importPath
    */
   public void addImport(Path importPath) {
      imports.add(importPath);
   }
   
   /**
    * Sets the project settings object.  This task only supports one
    * nested &lt;jdp&gt; element.
    * @param jdp
    */
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
      
      // blackberry jde will create this file and pass it to the rapc command
      jdp.writeManifest(new File(destDir, output+".rapc"), output);
      
      if (isOutOfDate(srcs, new File(destDir, output+".cod"))) {
         log(String.format("Compiling %d source files to %s", srcs.size(), output+".cod"));
         executeRapc();
      } else {
         log("Compilation skipped, cod is up to date", Project.MSG_VERBOSE);
      }
   }
   
   @SuppressWarnings("unchecked")
   protected void executeRapc() {
      Java java = (Java)getProject().createTask("java");
      java.setTaskName(getTaskName());
      java.setClassname("net.rim.tools.compiler.Compiler");
      
      // must fork in order to set working directory and/or new environment
      java.setFork(true);
      
      // we want to fail if rapc returns non-zero
      java.setFailonerror(true);
      
      Environment.Variable var = null;
      
      // loop through the systems environment variables looking for PATH
      Vector<String> env = Execute.getProcEnvironment();
      for (String line : env) {
         
         // setup our own path variable
         if (line.toUpperCase().startsWith("PATH")) {
            
            // create new env variable using jde bin directory as the value
            var = new Environment.Variable();
            var.setKey("PATH");
            var.setFile(new File(jdeHome, "bin"));
            
            // now add the systems current PATH value back into the new var
            var.setValue(String.format("%s%c%s",
                  line.substring(line.indexOf('=')+1),
                  File.pathSeparatorChar,
                  var.getValue()));
            
            break;
         }
      }
      
      if (var != null)
         java.addEnv(var);
      
      // if jdk home was specified, set the jvm command
      if (jdkHome != null) {
         java.setJvm(String.format("%s%c%s",
               new File(jdkHome, "bin").getAbsolutePath(),
               File.separatorChar, "java"));
      }
      
      // directory from which command will be launched
      java.setDir(destDir);
      
      // add rapc jar file to classpath
      java.createClasspath().setLocation(rapcJar);
      
      if (quiet) java.createArg().setValue("-quiet");

      if (exePath!=null)
         java.createArg().setValue("-exepath="+exePath.getAbsolutePath());
      
      java.createArg().setValue("import="+imports.toString());
      
      String type = jdp.getType().getValue();
      if (TypeAttribute.MIDLET.equals(type)) {
         java.createArg().setValue("codename="+output);
         java.createArg().setValue("-midlet");
      } else if (TypeAttribute.CLDC.equals(type)) {
         java.createArg().setValue("codename="+output);
      } else if (TypeAttribute.LIBRARY.equals(type)) {
         java.createArg().setValue("library="+output);
      }
      
      // manifest file is last parameter before file list
      java.createArg().setValue(output+".rapc");
      
      // add each of the items in the srcs path as file args
      for (String file : srcs.list()) {
         java.createArg().setFile(new File(file));
      }
      
      java.execute();
   }
}
