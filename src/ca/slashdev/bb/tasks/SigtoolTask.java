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
package ca.slashdev.bb.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

/**
 * Sigtool task will launch the signature tool on a given cod file or set of
 * cod files.  If a single cod file is specified, then a file will be created
 * along side the cod file to mark it as signed.
 * @author josh
 */
public class SigtoolTask extends BaseTask
{
   private File sigtoolJar;
   
   private boolean forceClose = false;
   private boolean close = true;
   private boolean request = true;
   private String password;
   
   private File codFile;
   private Union codFiles = new Union();
   
   /**
    * Sets the jde home directory and attempts to locate the
    * SignatureTool.jar file.
    */
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
   
   /**
    * Closes signature tool regardless of its success, defaults to false.
    * @param forceClose
    */
   public void setForceClose(boolean forceClose) {
      this.forceClose = forceClose;
   }
   
   /**
    * Closes signature tool after request if no errors occur, defaults to true.
    * @param close
    */
   public void setClose(boolean close) {
      this.close = close;
   }
   
   /**
    * Requests signatures when the tool launches, defaults to true.
    * @param request
    */
   public void setRequest(boolean request) {
      this.request = request;
   }
   
   /**
    * Sets password for automatic signature requesting.
    * Setting the passowrd implicitly sets request and close
    * properties to true.
    * @param password
    */
   public void setPassword(String password) {
      this.password = password;
   }
   
   /**
    * Sets the cod file to sign
    * @param codFile
    */
   public void setCodFile(File codFile) {
      this.codFile = codFile;
   }
   
   /**
    * Adds collection of cod files to sign (<fileset>, <filelist>, etc).
    * @param rc
    */
   public void add(ResourceCollection rc) {
      codFiles.add(rc);
   }
   
   @Override
   public void execute() throws BuildException {
      super.execute();
      
      if (jdeHome == null) {
         throw new BuildException("jdehome not set");
      }
      
      if (codFiles.size() == 0 && codFile == null) {
         throw new BuildException("codfile attribute or nested resource collection element required");
      }
      
      if (codFile != null && codFiles.size() > 0) {
         throw new BuildException("codfile attribute cant be used in conjunction with nested elements");
      }
      
      File touchFile = new File(".signed");
      if (touchFile.exists()) {
         long lastSigned = touchFile.lastModified();
         
         if (codFile != null) {
            if (codFile.lastModified() < lastSigned) {
               log("cod file does not appear to be modified since last signature", Project.MSG_WARN);
               return;
            }
         } else {
            boolean upToDate = true;
            
            for (Resource r : codFiles.listResources()) {
               if (r.getLastModified() >= lastSigned) {
                  upToDate = false;
                  break;
               }
            }
            
            if (upToDate) {
               log("cod files do not appear to be modified since last signature", Project.MSG_WARN);
               return;
            }
         }
      }
      
      executeSigtool();
   }
   
   private void executeSigtool() {
      Java java = (Java)getProject().createTask("java");
      java.setTaskName(getTaskName());
      
      java.setFailonerror(true);
      java.setFork(true);
      java.setJar(sigtoolJar);
      
      if (password != null) {
         close = true;
         request = true;
         java.createArg().setValue("-p");
         java.createArg().setValue(password);
      }
      
      if (forceClose) java.createArg().setValue("-C");
      if (close) java.createArg().setValue("-c");
      if (request) java.createArg().setValue("-a");
      
      for (String file : codFiles.list()) {
         java.createArg().setFile(new File(file));
      }
      
      if (java.executeJava() == 0) {
         File touchFile = new File(".signed");
         touch(touchFile);
      }
   }
   
   /*
    * Creates a file (if it doesn't already exist) and explicitly sets
    * the last modified date to the current time.
    */
   private void touch(File file) {
      try {
         // create file if it doesn't already exist
         if (!file.exists()) file.createNewFile();
         
         // update last modified time
         file.setLastModified(System.currentTimeMillis());
      } catch (IOException e) {
         throw new BuildException("error touching file", e);
      }
   }
}
