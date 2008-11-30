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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/**
 * Base class for bb tools tasks.  This class handles setting and
 * validating the jde home directory.
 * @author josh
 */
public abstract class BaseTask
   extends Task
{
   protected File jdeHome;
   
   @Override
   public void init() throws BuildException {
      // try and use jde.home property from the project
      String prjJdeHome = getProject().getProperty("jde.home");
      if (prjJdeHome != null) {
         setJdeHome(new File(prjJdeHome));
      }
   }
   
   /**
    * Sets jde home directory.  A BuildException is raised if the file
    * is not a directory (or does not exist).
    * @param jdeHome
    */
   public void setJdeHome(File jdeHome) {
      if (jdeHome.isDirectory()) {
         this.jdeHome = jdeHome;
      } else {
         throw new BuildException("jde home must be a directory");
      }
   }
   
   /**
    * Checks all elements in the source path against the target file
    * for "out of date" ness.
    * @param src
    * @param target
    * @return true if one or more source files are newer than the target
    */
   protected boolean isOutOfDate(Path src, File target) {
      boolean outOfDate = false;
      
      String[] items = src.list();
      for (int i=0; i<items.length && !outOfDate; i++) {
         if (!FileUtils.getFileUtils().isUpToDate(new File(items[i]), target)) {
            outOfDate = true;
         }
      }
      
      return outOfDate;
   }
   
   @Override
   public void execute() throws BuildException {
      String version = String.format("bb-ant-tools version %s",
            getClass().getPackage().getImplementationVersion());
      log(version, Project.MSG_DEBUG);
   }
}
