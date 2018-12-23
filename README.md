# Le2LeJosEv3-El3ectricGuitar
This is the LEGO® Mindstorms EV3 **El3ectric Guitar** example program in the Java programming language that uses a Java Implementation of _LEGO® Mindstorms EV3 Programming Blocks (icons)_ on LeJOS EV3. 

You can find the building instructions and the LEGO® icon-based program of the _El3ectric Guitar_ in the LEGO® icon based (LabView-based) Programming Environment (Home Edition).
The _El3ectric Guitar_ is one of the Robot Remix models.
You can download the LEGO® Programming Environment at https://www.lego.com/en-us/mindstorms/downloads/download-software (or one of the other language pages).

## Dependencies
This project depends on the **Le2LeJosEv3** Library (see https://github.com/robl0377/Le2LeJosEv3) that sits on top of the current version of the LeJOS EV3 framework. 
Please copy the classes in the _Le2LeJosEv3_ Library packages _le2lejosev3.pblocks_ and _le2lejosev3.logging_ into this project's source directory before running it.

In this project I am using the **LeJOS EV3 v0.9.1beta** framework (see https://sourceforge.net/projects/ev3.lejos.p/) and a standard LEGO® Mindstorms EV3 Brick.

## Resources
The program uses an image file _LOGO.lni_ that is in the project's _resources_ directory. 
Please upload them to your EV3 Brick via SCP to the directory _/home/lejos/lib_.

The **image file** was converted from the _LOGO.rgf_ found in the building instructions:
1. Convert RGF to BMP using the ImageMagick convert program (download ImageMagick static from https://www.imagemagick.org/script/download.php.
Then run **convert -monochrome LOGO.rgf LOGO.bmp**

2. Convert the BMP file to the needed LNI format with the LeJOS EV3 Image Converter program that is part of the EV3 Control Center or can run stand-alone from the bin directory of the LeJOS EV3 installation (ev3image.bat). Alas, the Image Converter is still adapted to the old Mindstorms NXT Brick and displays a warning if the loaded image exceeds 100x64 pixels. However, it correctly converts images for the Mindstorms EV3 with up to 178x128 pixels.


---
LEGO® is a trademark of the LEGO Group of companies which does not sponsor, authorize or endorse this site.
