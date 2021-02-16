import sys
import os
print(sys.argv[0])
given_path = sys.argv[1]
out = ""
len_dir = len(given_path.split(os.path.sep))
for (root,dirs,files) in os.walk(given_path, topdown=True):
    vertical_level = len(root.split(os.path.sep)) - len_dir
    print(root)
    print(vertical_level)
    for file in files:
        splitted_root = root.split(os.path.sep)
        ID = ""
        for split in splitted_root[len(splitted_root)-vertical_level:len(splitted_root)]:
            ID = ID + split + "_"
        #should be without the ".wav"
        path = root + "\\" + file
        ID = ID + file.split(".")[0]
        print(ID + "\t" + path)
        out = out + ID + "\t" + path + "\n"

with open("wav.scp", "w") as scp_file:
    scp_file.write(out)
    scp_file.close