from distutils.core import setup
import sys

import py2exe


INCLUDES = ["encodings", "encodings.*"]   
sys.argv.append("py2exe")  
options = {"py2exe":   { "bundle_files": 1 } }   
setup(options = options,  
      zipfile=None,   
      console = [{"script": "__init__.py", "icon_resources": [(1, "logo.ico")]} ] )  