/**
 * Created Apr 29, 2007
 * By Josh Kropf
 * Copyright josh@slashdev.ca
 */
package ca.slashdev.bb;

import java.io.File;

import org.apache.tools.ant.BuildException;
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
}
