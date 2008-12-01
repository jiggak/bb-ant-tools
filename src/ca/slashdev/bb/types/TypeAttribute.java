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

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Enumerated attribute for project type
 * @author josh
 */
public class TypeAttribute extends EnumeratedAttribute {

   public static final String CLDC    = "cldc";
   public static final String LIBRARY = "library";
   public static final String MIDLET  = "midlet";
   
   private static final String[] VALUES = new String[] {
      CLDC, LIBRARY, MIDLET
   };
   
   /**
    * Returns a list of valid values for this attribute
    * @see org.apache.tools.ant.types.EnumeratedAttribute#getValues()
    */
   @Override
   public String[] getValues() {
      return VALUES;
   }

}
