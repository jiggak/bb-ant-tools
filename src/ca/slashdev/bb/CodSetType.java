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

import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * @author josh
 */
public class CodSetType extends DataType {
   private Vector<ResourceCollection> resources = new Vector<ResourceCollection>();
   
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
         val.append(greater.isInclusive()? "(" : "[");
         val.append(greater.getVersion()).append(',');
      } else {
         val.append("(,");
      }
      
      if (less != null) {
         val.append(less.getVersion()).append(less.isInclusive()? ")" : "]");
      } else {
         val.append(")");
      }
      
      return val.toString();
   }
   
   public Vector<ResourceCollection> getResources() {
      return resources;
   }
}

class VersionMatch {
   private String version;
   private boolean inclusive;
   
   public VersionMatch(String version, boolean inclusive) {
      this.version = version;
      this.inclusive = inclusive;
   }
   
   public boolean isInclusive() {
      return inclusive;
   }
   
   public String getVersion() {
      return version;
   }
   
   public void validate() throws BuildException {
      int dot1 = version.indexOf('.');
      if (dot1 == -1)
         throw new BuildException("version must be in the form 'x.y.z'");
      
      int dot2 = version.indexOf('.', dot1+1);
      if (dot2 == -1)
         throw new BuildException("version must be in the form 'x.y.z'");
      
      String num1 = version.substring(0, dot1);
      String num2 = version.substring(dot1+1, dot2);
      String num3 = version.substring(dot2+1);
      
      try {
         Integer.parseInt(num1);
         Integer.parseInt(num2);
         Integer.parseInt(num3);
      } catch (NumberFormatException e) {
         throw new BuildException("version must be in the form 'x.y.z'", e);
      }
   }
}
