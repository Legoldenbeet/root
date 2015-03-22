#-*-coding: UTF-8-*-
from distutils.core import setup
import py2exe
# Powered by ***
INCLUDES = []
options = {"py2exe" :  
    {"compressed" : 1,  
     "optimize" : 2,  
     "bundle_files" : 1,  
     "includes" : INCLUDES,  
     "dll_excludes": [ "MSVCP90.dll", "mswsock.dll", "powrprof.dll","w9xpopen.exe"] }}  
setup(
    options = options, 
    description = "³ÌÐò1",  
    zipfile=None,
    console=[{"script": "__init__.py", "icon_resources": [(1, "logo.ico")] }],
    )