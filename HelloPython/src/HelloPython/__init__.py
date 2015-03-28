import os


print("helloworld")
print(os.name)
print(os.sep)
#os.system("cmd.exe")
print(os.getpid())
print(os.getenv("java_home", "eh"))
print(os.getcwd())
print(os.getcwdu())
print(os.curdir)
print("hel:"+os.linesep)

print(os.path.exists("G:\\1000_hotel_csv.csv"))
print(os.path.isfile("G:\\1000_hotel_csv.csv"))

name,ext=os.path.splitext("G:\\1000_hotel_csv.csv")
print(name)
print(ext)

L=os.listdir("G:\\")
print L

if os.path.exists("G:\\1234\\456")==False:
    os.makedirs("G:\\1234\\456")
os.system("pause")
