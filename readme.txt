All tasks require either the jdehome attribute to be set, or the
property jde.home must be set in the project.

   <sigtool>
==================

attributes
jdehome - optional: jde home directory
codfile - optional: cod file to sign
forceclose - optional: close regardless of its success, default false
close   - optional: close after request if no errors occur, default true
request - optional: requests signatures when the tool launches, default true

nested elements
<cod> - path-like structure of cod files

   <rapc>
==================

attributes
jdehome - optional: jde home directory
srcdir  - directory of source files
destdir - optional: cod file output directory, defaults to current directory
quiet   - optional: tells the rapc command to be less chatty, default to false
output  - required: name of output files eg: <output>.cod, <output>.cso
import  - path of jar files to import
importref - reference to imports path

nested elements
<src> - path-like structure of files (.java, .png, etc)
<import> - path-like structure of import jars
<jdp> - project attributes
   type - library,cldc,midlet defaults to library
   title - project title
   vendor - company name
   version - project version
   description - project description
   arguments - arguments passed to main method
   midletclass - MIDlet class name
   systemmodule - true if system module (run in background, no icon)
   runonstartup - true if application should start when device starts
   startuptier - startup priority in relation to other applications,
                 default value is 7, lower value = higher priority
   ribbonposition - position of icon in ribbon. 0 = default, higher
                    values move icon up priority
   icon - ribbon icon
   file - properties file containing the project attributes

eg:
<rapc jde="${jde.home}" type="cldc">
  <src path="${src}" />
  <import path="${jde.home}/lib/net_rim_api.jar" />
  <jdp title="Poo" output="my_poo" />
</rapc>
