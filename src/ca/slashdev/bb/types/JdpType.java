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
package ca.slashdev.bb.types;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;

/**
 * Collection of project settings.  Project settings can be set using the
 * data type attributes, or by specifying a properties file that contains the
 * settings. The properties file must contains keys that match the attributes
 * of this data type exactly.
 * @author josh
 */
public class JdpType extends BaseJdpType {
   private TypeAttribute type;
   private String version = "0.0";
   private String vendor = "<unknown>";
   private String description;
   private String midletClass = "";
   
   private List<EntryPointType> entryPoints = new ArrayList<EntryPointType>();
   
   public JdpType() {
      type = new TypeAttribute();
      type.setValue(TypeAttribute.CLDC);
   }
   
   public TypeAttribute getType() {
      return type;
   }
   
   public void setType(TypeAttribute type) {
      this.type = type;
   }
   
   public String getMidletClass() {
      return midletClass;
   }
   
   public void setMidletClass(String midletClass) {
      this.midletClass = midletClass;
   }
   
   public String getVersion() {
      return version;
   }
   
   public void setVersion(String version) {
      this.version = version;
   }
   
   public String getDescription() {
      return description;
   }
   
   public void setDescription(String desc) {
      this.description = desc;
   }
   
   public void setStartupTier(int startupTier) {
      if (startupTier >= 0 && startupTier <= 7) {
         this.startupTier = startupTier;
      } else {
         throw new BuildException("startuptier must be between 0 and 7");
      }
   }
   
   public boolean isSystemModule() {
      return systemModule;
   }
   
   public void setSystemModule(boolean systemModule) {
      this.systemModule = systemModule;
   }
   
   public String getVendor() {
      return vendor;
   }
   
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }
   
   public void addEntry(EntryPointType entry) {
      entryPoints.add(entry);
   }
   
   /**
    * Loads project attributes from a properties file.
    * @param file
    * @throws BuildException
    */
   public void setFile(File file) throws BuildException {
      super.setFile(file);
      
      FileInputStream in = null;
      
      if (!file.isFile()) {
         throw new BuildException("file attribute must be a properties file");
      }
      
      try {
         in =  new FileInputStream(file);
         
         Properties props = new Properties();
         props.load(in);
         
         type.setValue(props.getProperty("type", TypeAttribute.CLDC));
         version = props.getProperty("version", "0.0");
         vendor = props.getProperty("vendor", "<unknown>");
         description = props.getProperty("description");
         midletClass = props.getProperty("midletclass", "");
      } catch (IOException e) {
         throw new BuildException("error loading properties", e);
      } finally {
         if (in != null) {
            try { in.close(); }
            catch (IOException e) { }
         }
      }
   }
   
   /**
    * Generates a manifest file compatible with the rapc compiler.
    * @param file
    * @param output
    * @throws BuildException
    */
   public void writeManifest(File file, String output) throws BuildException {
      PrintStream out = null;
      
      try {
         // create file if it does not exist
         if (!file.exists()) file.createNewFile();
         
         out = new PrintStream(file);
         
         out.printf("MIDlet-Name: %s\n", output);
         out.printf("MIDlet-Version: %s\n", version);
         out.printf("MIDlet-Vendor: %s\n", vendor);
         
         if (description != null) {
            out.printf("MIDlet-Description: %s\n", description);
         }
         
         out.printf("MIDlet-Jar-URL: %s.jar\n", output);
         out.println("MIDlet-Jar-Size: 0");
         out.println("MicroEdition-Profile: MIDP-2.0");
         out.println("MicroEdition-Configuration: CLDC-1.1");
         
         if (TypeAttribute.CLDC.equals(type.getValue())) {
            out.printf("MIDlet-1: %s,%s,%s\n", title, getFirstIcon(), arguments);
            
            if (ribbonPosition > 0) {
               out.printf("RIM-MIDlet-Position-1: %d\n", ribbonPosition);
            }

            if (nameResourceBundle != null && nameResourceId > -1) {
               out.printf("RIM-MIDlet-NameResourceBundle-1: %s\n", nameResourceBundle);
               out.printf("RIM-MIDlet-NameResourceId-1: %d\n", nameResourceId);
            }
            
            int iconIndex = 0;
            for (int i=1; i<icons.length; i++) {
               iconIndex ++;
               out.printf("RIM-MIDlet-Icon-1-%d: %s\n", iconIndex, icons[i]);
            }
            
            for (String icon : focusIcons) {
               iconIndex ++;
               out.printf("RIM-MIDlet-Icon-1-%d: %s,focused\n", iconIndex, icon);
            }
            
            if (iconIndex > 0) {
               out.printf("RIM-MIDlet-Icon-Count-1: %d\n", iconIndex);
            }
            
            int flags = 0x00;
            if (runOnStartup) flags |= 0xE1-((2*startupTier)<<4);
            if (systemModule) flags |= 0x02;
            out.printf("RIM-MIDlet-Flags-1: %d\n", flags);
            
            if (entryPoints.size() != 0) {
               int entryIndex = 2;
               for (EntryPointType entryPoint : entryPoints) {
                  if(entryPoint.enabled(getProject())) {
                     out.printf("MIDlet-%d: %s,%s,%s\n", entryIndex, entryPoint.getTitle(),
                           entryPoint.getFirstIcon(), entryPoint.getArguments());
                  
                     if (entryPoint.getRibbonPosition() > 0) {
                        out.printf("RIM-MIDlet-Position-%d: %d\n", entryIndex, entryPoint.getRibbonPosition());
                     }
   
                     if (entryPoint.getNameResourceBundle() != null && entryPoint.getNameResourceId() > -1) {
                        out.printf("RIM-MIDlet-NameResourceBundle-%d: %s\n", entryIndex, entryPoint.getNameResourceBundle());
                        out.printf("RIM-MIDlet-NameResourceId-%d: %d\n", entryIndex, entryPoint.getNameResourceId());
                     }
                     
                     iconIndex = 0;
                     for (int i=1; i<entryPoint.icons.length; i++) {
                        iconIndex ++;
                        out.printf("RIM-MIDlet-Icon-%d-%d: %s\n", entryIndex, iconIndex, entryPoint.icons[i]);
                     }
                     
                     for (String icon : entryPoint.focusIcons) {
                        iconIndex ++;
                        out.printf("RIM-MIDlet-Icon-%d-%d: %s,focused\n", entryIndex, iconIndex, icon);
                     }
                     
                     if (iconIndex > 0) {
                        out.printf("RIM-MIDlet-Icon-Count-%d: %d\n", entryIndex, iconIndex);
                     }
   
                     flags = 0x00;
                     if (entryPoint.isRunOnStartup()) {
                        flags |= 0xE1-((2*entryPoint.getStartupTier())<<4);
                     }
                     
                     if (entryPoint.isSystemModule()) {
                        flags |= 0x02;
                     }
                     
                     out.printf("RIM-MIDlet-Flags-%d: %d\n", entryIndex, flags);
                     
                     entryIndex ++;
                  }
               }
            }
         } else if (TypeAttribute.MIDLET.equals(type.getValue())) {
            out.printf("MIDlet-1: %s,%s,%s\n", title, getFirstIcon(), midletClass);
            
            if (ribbonPosition > 0) {
               out.printf("RIM-MIDlet-Position-1: %d\n", ribbonPosition);
            }
            
            int flags = 0xE0;
            if (systemModule) flags |= 0x02;
            out.printf("RIM-MIDlet-Flags-1: %d\n", flags);
         } else {
            int flags = 0x02;
            if (runOnStartup) flags |= 0xE1-((2*startupTier)<<4);
            out.printf("RIM-Library-Flags: %d\n", flags);
         }
      } catch (IOException e) {
         throw new BuildException("error creating manifest", e);
      } finally {
         if (out != null) out.close();
      }
   }
}
