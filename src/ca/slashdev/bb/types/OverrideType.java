/*
 * Copyright 2009 Josh Kropf
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

/**
 * Simple key value structure representing field override in .jad files.
 * @author josh
 */
public class OverrideType extends DataType {
   private String key;
   private String value;
   
   public String getKey() {
      return key;
   }
   
   public void setKey(String key) {
      this.key = key;
   }
   
   public String getValue() {
      return value;
   }
   
   public void setValue(String value) {
      this.value = value;
   }
   
   public void validate() {
      if (key == null || "".equals(key.trim()))
         throw new BuildException("override key is required");
   }
}
