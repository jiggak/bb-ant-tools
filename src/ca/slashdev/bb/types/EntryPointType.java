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

import org.apache.tools.ant.Project;

/**
 * This data type contains attributes for specifying an alternate entry point
 * for CLDC applications.  It contains a subset of attributes available in
 * {@link JdpType}.  Attributes can also be read from a properties file
 * by setting the file attribute of this type.  The properties file must
 * contain keys that match the attributes of this type exactly.
 * @author josh.kropf
 */
public class EntryPointType extends BaseJdpType {
   private String ifCond;
   private String unlessCond;
   
   /**
    * Sets the if attribute.  The entry point is included during compilation
    * when the property named by this attribute is defined in the project.
    * @param ifCond
    */
   public void setIf(String ifCond) {
      this.ifCond = ifCond;
   }

   /**
    * Sets the unless attribute.  The entry point is included during compilation
    * when the property named by this attribute is NOT defined in the project.
    * @param unlessCond
    */
   public void setUnless(String unlessCond) {
      this.unlessCond = unlessCond;
   }
   
   public boolean enabled(Project p) {
      if (ifCond != null && p.getProperty(ifCond) == null) {
         return false;
      } else if (unlessCond != null && p.getProperty(unlessCond) != null) {
         return false;
      } else {
         return true;
      }
   }
}
