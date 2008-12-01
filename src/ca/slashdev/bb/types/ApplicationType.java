/*
 * Copyright 2008 Josh Kropf
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
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ca.slashdev.bb.util.Utils;

/**
 * @author josh
 */
public class ApplicationType extends DataType {
   private String id;
   private String name;
   private String description;
   private String version;
   private String vendor;
   private String copyright;
   private Vector<CodSetType> codSets = new Vector<CodSetType>();
   
   public void setId(String id) {
      this.id = id;
   }
   
   public String getId() {
      return id;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
   
   public void setTitle(String name) {
      this.name = name;
   }
   
   public void setDescription(String desc) {
      this.description = desc;
   }
   
   public String getDescription() {
      return description;
   }
   
   public void setVersion(String version) {
      this.version = version;
   }
   
   public String getVersion() {
      return version;
   }
   
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }
   
   public String getVendor() {
      return vendor;
   }
   
   public void setCopyright(String copyright) {
      this.copyright = copyright;
   }
   
   public String getCopyright() {
      return copyright;
   }
   
   public void addCodSet(CodSetType codSet) {
      codSets.add(codSet);
   }
   
   public void setFile(File file) throws BuildException {
      FileInputStream in = null;
      
      if (!file.isFile()) {
         throw new BuildException("file attribute must be a properties file");
      }
      
      try {
         in =  new FileInputStream(file);
         
         Properties props = new Properties();
         props.load(in);
         
         id = props.getProperty("id");
         name = props.getProperty("name");
         if (name == null)
            name = props.getProperty("title");
         description = props.getProperty("description");
         version = props.getProperty("version");
         vendor = props.getProperty("vendor");
         copyright = props.getProperty("copyright");
      } catch (IOException e) {
         throw new BuildException("error loading properties", e);
      } finally {
         if (in != null) {
            try { in.close(); }
            catch (IOException e) { }
         }
      }
   }
   
   @SuppressWarnings("unchecked")
   public void generate(Document xmldoc, Element parent) {
      if (id == null) {
         throw new BuildException("id attribute is reqired");
      }
      
      Element child, appNode = xmldoc.createElement("application");
      parent.appendChild(appNode);
      
      appNode.setAttribute("id", id);
      
      if (name != null) {
         appNode.appendChild(child = xmldoc.createElement("name"));
         child.setTextContent(name);
      }
      
      if (description != null) {
         appNode.appendChild(child = xmldoc.createElement("description"));
         child.setTextContent(description);
      }
      
      if (version != null) {
         appNode.appendChild(child = xmldoc.createElement("version"));
         child.setTextContent(version);
      }
      
      if (vendor != null) {
         appNode.appendChild(child = xmldoc.createElement("vendor"));
         child.setTextContent(vendor);
      }
      
      if (copyright != null) {
         appNode.appendChild(child = xmldoc.createElement("copyright"));
         child.setTextContent(copyright);
      }
      
      for (CodSetType codSet : codSets) {
         String bbVer = null;
         if (codSet.hasVersionMatch()) {
            bbVer = codSet.getVersionMatch();
         }
         
         if (codSet.getDir() != null) {
            child = xmldoc.createElement("directory");
            appNode.appendChild(child);
            child.setTextContent(codSet.getDir());
            
            if (bbVer != null) {
               child.setAttribute("_blackberryVersion", bbVer);
               bbVer = null;
            }
         }
         
         child = xmldoc.createElement("fileset");
         appNode.appendChild(child);
         
         child.setAttribute("Java", "1.0");
         
         if (bbVer != null) {
            child.setAttribute("_blackberryVersion", bbVer);
         }
         
         StringBuffer files = new StringBuffer();
         Iterator<Resource> i = codSet.getResources().iterator();
         while (i.hasNext()) {
            files.append('\n').append(Utils.getFilePart(i.next()));
         }
         files.append('\n');
         
         Element filesNode = xmldoc.createElement("files");
         filesNode.setTextContent(files.toString());
         
         child.appendChild(filesNode);
      }
   }
   
   @SuppressWarnings("unchecked")
   public void copyCodFiles(File destDir) throws IOException {
      Resource r;
      
      for (CodSetType codSet : codSets) {
         if (codSet.getDir() != null) {
            destDir = new File(destDir, codSet.getDir());
            if (!destDir.exists())
               if (!destDir.mkdirs())
                  throw new IOException("unable to create cod files director");
         }
         
         FileResource destFile;
         Iterator<Resource> i = codSet.getResources().iterator();
         while (i.hasNext()) {
            r = i.next();
            destFile = new FileResource(destDir, Utils.getFilePart(r));
            
            if (!r.equals(destFile))
               ResourceUtils.copyResource(r, destFile);
         }
      }
   }
}
