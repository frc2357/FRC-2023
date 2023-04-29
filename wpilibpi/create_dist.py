import zipfile

"""
 Run this file to package up all necessary files into a zip file
 to load on the RPI webserver (http://10.23.57.13/#application)
 This script will take the run_vision.py file and rename it to 
 uploaded.py since this is what the default wpilibpi uploaded python script mode is looking for
 assuming on http://wpilibpi.local/#application it is set to "Uploaded Python File"

 From windows cmd prompt at location of this script:
 >python create_dist.py
 >scp dist.zip pi@10.23.57.13:/home/pi
  (enter password (raspberry))

 then from ssh shell (i.e.>ssh pi@10.23.57.13)
 >unzip -o build.zip

"""

files = [
    "run_vision.py",
    "cameravision.py",
    "calibration.py",
    "detect_apriltags.py",
    "detect_colors.py",
    "gamepiece.py",
    "frc.json",
]

if __name__ == "__main__":
    print("Creating zip for distribution on RPI")
    f = zipfile.ZipFile(r".\dist.zip", mode="w")
    for file in files:
        print(f"Adding {file}")
        if file == "run_vision.py":
            f.write(file, "uploaded.py")
        else:
            f.write(file)
    print("...Done!")
