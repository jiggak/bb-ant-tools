/**
 * Created Apr 29, 2007
 * By josh
 * Copyright 2005 Slashdev.ca
 */
package ca.slashdev.rapc;

import org.apache.tools.ant.types.DataType;

/**
 * @author josh
 *
 */
public class JdpType extends DataType {
   private String title;
   private String vendor;
   private String desc;
   private String arguments;
   private boolean systemModule;
   private boolean runOnStartup;
   private int startupTier;
   private int ribbonPosition;
   private String icon;
   
   public String getArguments() {
      return arguments;
   }
   
   public void setArguments(String arguments) {
      this.arguments = arguments;
   }
   
   public String getDesc() {
      return desc;
   }
   
   public void setDesc(String desc) {
      this.desc = desc;
   }
   
   public int getRibbonPosition() {
      return ribbonPosition;
   }
   
   public void setRibbonPosition(int ribbonPosition) {
      this.ribbonPosition = ribbonPosition;
   }
   
   public boolean isRunOnStartup() {
      return runOnStartup;
   }
   
   public void setRunOnStartup(boolean runOnStartup) {
      this.runOnStartup = runOnStartup;
   }
   
   public int getStartupTier() {
      return startupTier;
   }
   
   public void setStartupTier(int startupTier) {
      this.startupTier = startupTier;
   }
   
   public boolean isSystemModule() {
      return systemModule;
   }
   
   public void setSystemModule(boolean systemModule) {
      this.systemModule = systemModule;
   }
   
   public String getTitle() {
      return title;
   }
   
   public void setTitle(String title) {
      this.title = title;
   }
   
   public String getVendor() {
      return vendor;
   }
   
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }
   
   public String getIcon() {
      return icon;
   }
   
   public void setIcon(String icon) {
      this.icon = icon;
   }
}
