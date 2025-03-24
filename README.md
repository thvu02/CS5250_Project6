# CS 5250 Projects 6, 7, and 8
Repository for CS 5250 projects 6, 7, and 8. I'll rename the repo once the semester is over.

## Project 6
All files are in the ```Project 6``` folder with input files under the ```input``` directory and program outputs in the ```output``` directory.

Simply run ```Main.java``` without any arguments to run the project.

## Project 7
All files are in the ```Project 7``` folder. All input/output files are under the ```test``` directory. Inputs files are ```XXX.vm``` files are output files are the respective ```XXX.asm``` files. To run the code, do ```java Main.java test``` to create ```.asm``` files for all the ```.vm``` files or do ```java Main.java test/XXX.vm``` to create an ```.asm``` file for one specified ```.vm``` file.

## Project 8
All files are in the ```Project 8``` folder. All input/output files are under the ```test``` directory, with each subfolder being for a specific test provided by Nand2Tetris. Since some of these tests comprise of multiple ```.vm``` files, we must feed the subfolder path as the argument like so: ```java Main.java test/XXX``` where ```XXX``` is the subfolder name. This will generate one ```.asm``` file for that test which can then be verified via the Nand2Tetris CPU Emulator. Note that running the code like this is a matter of necessity and not a matter of convenience as it was for Project 7.