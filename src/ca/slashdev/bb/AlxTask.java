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
package ca.slashdev.bb;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author josh
 */
public class AlxTask extends BaseTask {
   private String fileName;
   private File destDir;
   private Vector<ApplicationType> apps = new Vector<ApplicationType>();
   
   public void setDestDir(File destDir) {
      this.destDir = destDir;
   }
   
   public File getDestDir() {
      return destDir;
   }
   
   public void setFileName(String fileName) {
      this.fileName = fileName;
   }
   
   public String getFileName() {
      return fileName;
   }
   
   public void addApplication(ApplicationType app) {
      apps.add(app);
   }
   
   @Override
   public void execute() throws BuildException {
      super.execute();
      
      if (destDir == null) {
         throw new BuildException("destdir is a required attribute");
      }
      
      if (fileName == null) {
         throw new BuildException("filename is a required attribute");
      }
      
      executeAlx();
   }
   
   private void executeAlx() {
      try {
         if (!destDir.exists()) {
            if (!destDir.mkdirs())
               throw new BuildException("unable to create destination director");
         }
         
         File destFile = new File(destDir, fileName);
         if (!destFile.exists()) {
            destFile.createNewFile();
         }
         
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         DOMImplementation impl = builder.getDOMImplementation();
         Document xmldoc = impl.createDocument(null, "loader", null);
         
         Element root = xmldoc.getDocumentElement();
         root.setAttribute("version", "1.0");
         
         for (ApplicationType app : apps) {
            app.copyCodFiles(destDir);
            app.generate(xmldoc, root);
         }
         
         DOMSource domSource = new DOMSource(xmldoc);
         TransformerFactory tf = TransformerFactory.newInstance();
         Transformer serializer = tf.newTransformer();
         serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
         serializer.setOutputProperty(OutputKeys.INDENT, "yes");
         
         // this is needed to make indenting work, works with XALAN only
         serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
         
         StreamResult streamResult = new StreamResult(destFile);
         serializer.transform(domSource, streamResult);
      } catch (Exception e) {
         throw new BuildException(e);
      }
   }
}
