/**
 * Created Apr 28, 2007
 * By josh
 * Copyright 2005 Slashdev.ca
 */
package ca.slashdev.rapc;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * @author josh
 */
public class RapcTask extends Task
{
   private File jdeHome;
   
   private File srcDir;
   private File destDir;
   
   private TypeAttribute type;
   private String output;
   
   private Collection<Path> srcs = new Vector<Path>();
   private Collection<Path> imports = new Vector<Path>();
   
   private JdpType jdp;
   
   @Override
   public void init() throws BuildException {
      // try and use jde.home property from the project
      String prjJdeHome = getProject().getProperty("jde.home");
      if (prjJdeHome != null) {
         jdeHome = new File(prjJdeHome);
         if (!jdeHome.exists() || !jdeHome.isDirectory()) {
            throw new BuildException("jde.home property must be a directory");
         }
      }
   }
   
   public void setType(TypeAttribute type) {
      this.type = type;
   }
   
   public void setOutput(String output) {
      this.output = output;
   }
   
   public void setJdeHome(File jdeHome) {
      this.jdeHome = jdeHome;
   }
   
   public void setSrcDir(File srcDir) {
      this.srcDir = srcDir;
   }
   
   public void setDestDir(File destDir) {
      this.destDir = destDir;
   }
   
   public void setImport(Path importPath) {
      imports.add(importPath);
   }
   
   public void setImportRef(Reference importRef) {
      Object obj = importRef.getReferencedObject();
      
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
      if (srcs.size() == 0 && srcDir == null) {
         throw new BuildException("srcdir attribute or <src> element required!");
      }
      
   }
}
