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
package ca.slashdev.bb.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;

/**
 * Set of utility methods for working with Resource objects and File objects.
 * @author josh
 */
public final class Utils {
   private static final byte[] ZIP_MAGIC = new byte[] {0x50, 0x4b, 0x03, 0x04};
   
   /**
    * Returns the file name portion of the resource.
    * @param r resource object
    * @return file name
    */
   public static String getFilePart(Resource r) {
      String path = r.getName();
      if (path.indexOf('/') != -1)
         path = path.substring(path.lastIndexOf('/')+1);
      return path;
   }
   
   /**
    * Returns true if the resource is a zip file.
    * @param r resource object
    * @return true when resource is a zip file
    * @throws IOException
    */
   public static boolean isZip(Resource r) throws IOException {
      InputStream in = null;
      try {
         in = r.getInputStream();
         
         byte[] magic = new byte[4];
         if (in.read(magic) != 4)
            throw new IOException("unable to read 4 bytes from resource");
         
         return Arrays.equals(magic, ZIP_MAGIC);
      } finally {
         FileUtils.close(in);
      }
   }
   
   /**
    * Extracts zip file resource to the destination directory and returns an
    * array of the zip file entry names.
    * @param r resource object
    * @param destDir destination directory
    * @return an array of zip file entry names
    * @throws IOException
    */
   public static String[] extract(Resource r, File destDir) throws IOException {
      FileOutputStream output = null;
      
      Vector<String> entries = new Vector<String>();
      byte[] buf = new byte[1024];
      int sz;
      
      ZipInputStream input = null;
      try {
         input = new ZipInputStream(r.getInputStream());
         ZipEntry entry = input.getNextEntry();
         while (entry != null) {
            try {
               entries.add(entry.getName());
               output = new FileOutputStream(new File(destDir, entry.getName()));
               
               while ((sz=input.read(buf)) != -1) {
                  output.write(buf, 0, sz);
               }
            } finally {
               input.closeEntry();
               FileUtils.close(output);
            }
            
            entry = input.getNextEntry();
         }
      } finally {
         FileUtils.close(input);
      }
      
      return entries.toArray(new String[] {});
   }
   
   /**
    * Calculates the SHA hash of the given file and returns the result as
    * a string of hex digits separated by spaces.
    * @param file input file
    * @return SHA string
    * @throws IOException
    */
   public static String getSHA1(File file) throws IOException {
      InputStream in = null;
      try {
         in = new FileInputStream(file);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         
         int sz;
         byte[] buf = new byte[1024];
         
         while ((sz=in.read(buf)) != -1) {
            buffer.write(buf, 0, sz);
         }
         
         MessageDigest md = MessageDigest.getInstance("SHA");
         md.update(buffer.toByteArray());
         
         byte[] hash = md.digest();
         
         StringBuffer str = new StringBuffer();
         for (byte b : hash) {
            str.append(' ');
            str.append(String.format("%02x", (b & 0xff)));
         }
         
         return str.toString();
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("weird, everyone has this algo", e);
      } finally {
         FileUtils.close(in);
      }
   }
   
   @SuppressWarnings("unchecked")
   public static boolean isUpToDate(ResourceCollection src, File target) {
      if (!target.exists())
         return false;
      
      long targetLastModified = target.lastModified();
      
      Iterator<Resource> i = src.iterator();
      while (i.hasNext()) {
         if (i.next().getLastModified() > targetLastModified)
            return false;
      }
      
      return true;
   }
   
   /**
    * Returns true if target is newer than (or same age as) source.
    * @param src source file (the one used to create target)
    * @param target target file (depends on source file)
    * @return true if target is up to date
    */
   public static boolean isUpToDate(File src, File target) {
      if (!target.exists())
         return false;
      
      return target.lastModified() >= src.lastModified();
   }
}
