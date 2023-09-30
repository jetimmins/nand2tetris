# Hack Virtual Machine Translator
A virtual machine translator for the Hack platform, written in Java, covering chapters 7 & 8 of the [nand2tetris](https://www.nand2tetris.org/)
project.

## Usage:
`$ java -jar ./target/vmtranslator-1.0-shaded.jar [filepath] [option]`

- `path`: .vm file or dir containing .vm files.
- `options`:
  - `--no-bootstrap`: do not output bootstrap asm
  
`$ mvn test`

Runs all unit tests including an output comparison test `VMTranslatorTest` using the [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/)
plugin.

`$ mvn package`

Create an executable jar using the [Maven Shade](https://maven.apache.org/plugins/maven-shade-plugin/) plugin. This
project has only test dependencies so no dependencies are bundled into the jar.

`$ mvn verify`

Translates all the project test programs using the packaged artifact, then runs all nand2tetris tests against the
project CPUEmulator to check for regressions. The [regression testing](./regression.sh) script is invoked using the
[Exec](https://www.mojohaus.org/exec-maven-plugin/) Maven plugin. The plugin is configured to look for the nand2tetris
project files in the parent directory. A failed regression test will yield a non-zero exit from the script causing the
goal to fail with an exception. Emitting the bootstrap asm is required for all FunctionCalls tests bar SimpleFunction.