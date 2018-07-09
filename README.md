# 2-Pass Linker for Operating Systems by Luis Olivar

Instructions for use:

The following assumes a Unix-based enviornment.

## Set-up

- Make sure Java 8 or higher is installed on your system.
- Open a terminal to the LinkerLab folder.

## Compiling

Run the command

```console
$mkdir bin
```

To create an output folder to hold the .class files. Then run the command

```console
$javac -d bin/ src/oslinker/*.java
```

to build the program.

## Running

Still in this same directory run the command

```console
$java -cp bin oslinker.LinkerOS "INSERT_PATH_TO_TEST_FILE_HERE"
```

For example, if a test file called "test1.txt" is place in the provided testing directory,
you may run the program like so:

```console
$java -cp bin oslinker.LinkerOS "testing/test1.txt"
```
