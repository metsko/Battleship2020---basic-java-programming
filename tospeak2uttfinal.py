#specify wav_path as first args
import sys
import os
given_path = sys.argv[1]
out = ""
len_dir = len(given_path.split(os.path.sep))
for (root,dirs,files) in os.walk(given_path, topdown=True):
    vertical_level = len(root.split(os.path.sep)) - len_dir
    for file in files:
        splitted_root = root.split(os.path.sep)
        UTT_ID = ""
        speaker_ID = ""
        counter = 0
        for split in splitted_root[len(splitted_root)-vertical_level:len(splitted_root)]:
            print(split)
            if counter == 0:
                print(split)
                speaker_ID = split
            print(speaker_ID)
            UTT_ID = UTT_ID + split + "_"
            counter = counter +1
        UTT_ID = UTT_ID + file.split(".")[0]
        out = out + UTT_ID + " " + speaker_ID + "\n"
with open("utt2spk", "w") as txt_file:
    txt_file.write(out)     
    txt_file.close