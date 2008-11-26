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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;

/**
 * This data type contains attributes for specifying an alternate entry point
 * for CLDC applications.  It contains a subset of attributes available in
 * {@link JdpType}.  Attributes can also be read from a properties file
 * by setting the file attribute of this type.  The properties file must
 * contain keys that match the attributes of this type exactly.
 * @author josh.kropf
 */
public class EntryPointType extends DataType {
   private String title;
   private String arguments;
   private boolean systemModule;
   private boolean runOnStartup;
   private int startupTier;
   private int ribbonPosition;
   private String icon;
   private String nameResourceBundle;
   private int nameResourceId;
   
   public EntryPointType() {
      title = "";
      icon = "";
      arguments = "";
      startupTier = 7;
      ribbonPosition = 0;
      nameResourceId = -1;
   }
   
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getArguments() {
      return arguments;
   }
   
   public void setArguments(String arguments) {
      this.arguments = arguments;
   }
   
   public String getIcon() {
      return icon;
   }
   
   public void setIcon(String icon) {
      this.icon = icon;
   }
   
   public int getRibbonPosition() {
      return ribbonPosition;
   }
   
   public void setRibbonPosition(int ribbonPosition) {
      this.ribbonPosition = ribbonPosition;
   }
   
   public boolean isRunOnStartup() {
      return runOnStartup;
   }
   
   public void setRunOnStartup(boolean runOnStartup) {
      this.runOnStartup = runOnStartup;
   }
   
   public int getStartupTier() {
      return startupTier;
   }
   
   public void setStartupTier(int startupTier) {
      this.startupTier = startupTier;
   }
   
   public boolean isSystemModule() {
      return systemModule;
   }
   
   public void setSystemModule(boolean systemModule) {
      this.systemModule = systemModule;
   }

   public String getNameResourceBundle() {
      return nameResourceBundle;
   }

   public void setNameResourceBundle(String nameResourceBundle) {
      this.nameResourceBundle = nameResourceBundle;
   }

   public int getNameResourceId() {
      return nameResourceId;
   }

   public void setNameResourceId(int nameResourceId) {
      this.nameResourceId= nameResourceId;
   }
   
   /**
    * Load entry point attributes from properties file.
    * @param file
    * @throws BuildException
    */
   public void setFile(File file) throws BuildException {
      FileInputStream in = null;
      
      if (!file.isFile()) {
         throw new BuildException("file attribute must be a properties file");
      }
      
      try {
         in =  new FileInputStream(file);
         
         Properties props = new Properties();
         props.load(in);
         
         title = props.getProperty("title", "");
         arguments = props.getProperty("arguments", "");
         systemModule = Boolean.parseBoolean(props.getProperty("systemmodule", "false"));
         runOnStartup = Boolean.parseBoolean(props.getProperty("runonstartup", "false"));
         setStartupTier(Integer.parseInt(props.getProperty("startupTier", "7")));
         setRibbonPosition(ribbonPosition = Integer.parseInt(props.getProperty("ribbonposition", "0")));
         icon = props.getProperty("icon", "");
         nameResourceBundle = props.getProperty("nameresourcebundle");
         nameResourceId = Integer.parseInt(props.getProperty("nameresourceid", "-1"));
      } catch (IOException e) {
         throw new BuildException("error loading properties", e);
      } finally {
         if (in != null) {
            try { in.close(); }
            catch (IOException e) { }
         }
      }
   }
}
