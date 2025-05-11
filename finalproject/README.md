# CS 5250 Final Project: Quantum Computing for Drug Discovery

## YouTube Video Link
https://youtu.be/UqrfxJBg3cA

## Description
This folder contains the slideshow, demo, and environment file for my final project which explains what is quantum computing, how quantum computing works, and leverages IBM Qiskit to implement a simple demo on calculating the electronic ground state energy of a hydrogen (H2) molecule using classical and quantum computing methods.

The goal of this demo is to demonstrate how quantum computing can be used to calculate the electronic ground state energy of any molecule and produce similarly accurate results to classical computation methods. As more complex molecules are examined, classical computing methods cannot complete in a reasonable amount of time. Quantum computing can solve this issue without any drawbacks.

By using quantum computing for drug discovery, we can accelerate the time it takes to create and test new drugs, allowing society to find cures to diseases faster while maintaining the same level of reliability. Quantum computing will broaden the horizons of drug discovery, allowing society to reach new horizons in the the field of medicine.

## Prerequisites
- miniconda
    - https://www.anaconda.com/download/success
- Linux-based system (MacOS, Linux, WSL)
    - the code will **NOT** work on normal Windows due to a package dependency that only works on Linux systems
    - To install WSL: https://learn.microsoft.com/en-us/windows/wsl/install

## To Run the Code
- ensure you are running a Linux-based system (MacOS, Linux, WSL)
- clone the repository
- `cd` into the repository
- activate miniconda
    - ```source ~/miniconda3/bin/activate```
- create a new environment using the `env.yml` file
    - ```conda env create --file environment.yml```
- activate newly created environment
    - ```conda activate demo```
- run all the cells in `demo.ipynb`

## Folder Contents
- demo.ipynb
    - code for demo
    - **NOTE:** this only runs on MacOS or Linux systems (Windows users can use WSL) due to dependence on PySCF package 
- environment.yml
    - miniconda env to replicate results
- Quantum Computing for Drug Discovery.pptx/.pdf (slideshow in different formats)