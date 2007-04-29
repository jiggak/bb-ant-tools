   <rapc>
==================

attributes
jdehome - jde home directory, optional if project has jde.home property set
type    - library,cldc,midlet
srcdir  - directory of source files
destdir - cod file output directory
output  - name of output files eg: <output>.cod, <output>.cso
import  - path of jar files to import
importref - reference to imports path

nested elements
<src> - path-like structure of source files
<import> - path-like structure of import jars
<jdp> - project attributes
   title - project title
   vendor - company name
   version - project version
   description - project description
   arguments - arguments passed to main method
   systemmodule - true if system module (run in background, no icon)
   runonstartup - true if application should start when device starts
   startuptier - startup priority in relation to other applications,
                 default value is 7, lower value = higher priority
   ribbonposition - position of icon in ribbon. 0 = default, higher
                    values move icon up priority
   icon - ribbon icon

eg:
<rapc jde="${jde.home}" type="cldc">
  <src path="${src}" />
  <import path="${jde.home}/lib/net_rim_api.jar" />
  <jdp title="Poo" output="my_poo" />
</rapc>
