# CS 5250 Projects
Repository for CS 5250 projects 6 to 12 (+ final project). I'll rename the repo once the semester is over.

## Completed to Date
- 6
- 7 / 8
- 9
- 10 / 11
- 12
- Final Project

## Project 6
All files are in the ```Project 6``` folder with input files under the ```input``` directory and program outputs in the ```output``` directory.

Simply run ```Main.java``` without any arguments to run the project.

## Project 7
All files are in the ```Project 7``` folder. All input/output files are under the ```test``` directory. Inputs files are ```XXX.vm``` files are output files are the respective ```XXX.asm``` files. To run the code, do ```java Main.java test``` to create ```.asm``` files for all the ```.vm``` files or do ```java Main.java test/XXX.vm``` to create an ```.asm``` file for one specified ```.vm``` file.

## Project 8
All files are in the ```Project 8``` folder. All input/output files are under the ```test``` directory, with each subfolder being for a specific test provided by Nand2Tetris. Since some of these tests comprise of multiple ```.vm``` files, we must feed the subfolder path as the argument like so: ```java Main.java test/XXX``` where ```XXX``` is the subfolder name. This will generate one ```.asm``` file for that test which can then be verified via the Nand2Tetris CPU Emulator. Note that running the code like this is a matter of necessity and not a matter of convenience as it was for Project 7.

## Project 9
Series of .jack files which build upon Square Dance code of Nand2Tetris to add a tailwind behind whereever the square moves. Import to the Nand2Tetris Jack Compiler and verify results using the UI.

## Project 10
Run the JackCompiler.java code and feed a desired directory containing .jack files to it. ```java JackAnalyzer.java test/{directory_name}```. Nand2Tetris provided tests and outputs of program are stored in the `test` directory. For each {name}.jack file, a corresponding {name}.xml and {name}T.xml file will generate. Nand2Tetris .xml files for checking are named starting with "nand...". Nand2Tetris's TextComparer utility is used to compare and verify similarity between .xml file contents.

## Project 11
Run the JackCompiler.java code and feed a desired directory containing .jack files to it. ```java JackCompiler.java test/{directory_name}```. Nand2Tetris provided tests and outputs of program are stored in the `test` directory. 

## Project 12
Compile each .jack file using the Nand2Tetris Jack Compiler. Test each .jack file in isolation by putting them into their respective folders. Do a final test after all are successful in isolation by putting everything into `Pong` directory, running the Jack Compiler, and verifying the contents are as expected.

## Final Project
Your final assignment is to build a program that runs on a quantum computer. Then explain how a quantum computer works as if to a child and how your program utilizes the architecture.
1. First you need to understand the architecture of how a quantum computer works in comparison to how a classical computer works like the MIPS based system we learned in this class. GOAL: When trying something new first try to understand what it is you are working with conceptually before diving into coding guides.
2. Next look at the IBM tutorial . I recommend going through the entire youtube series to get started.
3. Build something new! Create a program that runs on the IBM Quantum computer.
4.Create your presentation for someone who does not know or understand quantum computing, think of a child. 
    a. You should explain how quantum computing works 5-10 mins
    b. Explain what your program does and why you ran it on a Quantum computer. 10-15 mins
    c.  Your demo and explanation should last ~20 min. 
    Your presentation is due Friday May 16th by 11:59p.m.. NOTE: There will be no late submissions!