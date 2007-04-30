/**
 * Created Apr 29, 2007
 * By josh
 * Copyright 2005 Slashdev.ca
 */
package ca.slashdev.bb;

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
