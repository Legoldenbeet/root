from distutils.core import setup
import sys

import py2exe


INCLUDES = ["encodings", "encodings.*"]   
sys.argv.append("py2exe")  
options = {"py2exe" :  
    {"compressed" : 1,
     "optimize" : 2,
     "bundle_files" : 1,
     "includes" : INCLUDES,
     "dll_excludes": ["MSVCP90.dll", "mswsock.dll", "powrprof.dll", "w9xpopen.exe"] }}  
setup(
    options=options,
    description="gorunme",
    zipfile=None,
    console=[{"script": "__init__.py", "icon_resources": [(1, "logo.ico")] }],
    ) 
