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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

import ca.slashdev.bb.util.VersionMatch;

/**
 * @author josh
 */
public class CodSetType extends DataType {
   private Union resources = new Union();
   
   private String dir;
   private VersionMatch greater;
   private VersionMatch less;
   
   public void add(ResourceCollection res) {
      resources.add(res);
   }
   
   public void setDir(String dir) {
      if (dir.indexOf('/') != -1 || dir.indexOf('\\') != -1)
         throw new BuildException("cod file directory must not contain directory separator characters");
      
      this.dir = dir;
   }
   
   public String getDir() {
      return dir;
   }
   
   public void setGreaterThan(String version) throws BuildException {
      greater = new VersionMatch(version, false);
   }
   
   public void setGreaterThanEqual(String version) {
      greater = new VersionMatch(version, true);
   }
   
   public void setLessThan(String version) {
      less = new VersionMatch(version, false);
   }
   
   public void setLessThanEqual(String version) {
      less = new VersionMatch(version, true);
   }
   
   public boolean hasVersionMatch() {
      return greater != null || less != null;
   }
   
   public String getVersionMatch() {
      StringBuffer val = new StringBuffer();
      
      if (greater != null) {
         val.append(greater.isInclusive()? "[" : "(");
         val.append(greater.getVersion()).append(',');
      } else {
         val.append("(,");
      }
      
      if (less != null) {
         val.append(less.getVersion()).append(less.isInclusive()? "]" : ")");
      } else {
         val.append(")");
      }
      
      return val.toString();
   }
   
   public Union getResources() {
      return resources;
   }
}
