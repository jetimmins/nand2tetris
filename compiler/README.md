# Jack compiler
An analyser, tokeniser and Jack compiler written in Java. Can emit XML tokens, XML parse tree or compiled VM for the 
Hack platform. This covers chapters 10 & 11 of the [nand2tetris](https://www.nand2tetris.org/) project.

## Usage:
Compiles a provided .jack file or directory containing .jack files.
Emits compiled VM code to '[path]/target' directory as default.

```$ java -jar ./target/compiler-1.0-shaded.jar [filepath] [option]
    path:    .jack file or dir containing .jack files.
    options:
        --tokenise: Outputs result of tokenised code instead of compiled code.
        --xml:      Outputs in XML format instead of VM format.
        --target:   An output directory for the compiled files instead of [path]/target.
```

Runs all unit tests including an output comparison test within `CompilationEngineTest` using the [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/)
plugin.

`$ mvn package`

Create an executable jar using the [Maven Shade](https://maven.apache.org/plugins/maven-shade-plugin/) plugin. This
project has only test dependencies so no dependencies are bundled into the jar.

`$ mvn verify`

Translates all the project test programs using the packaged artifact, then runs all nand2tetris tests against the
project TextComparer to check for regressions. The [regression testing](./regression.sh) script is invoked using the
[Exec](https://www.mojohaus.org/exec-maven-plugin/) Maven plugin. The plugin is configured to look for the nand2tetris
project files in the parent directory. A failed regression test will yield a non-zero exit from the script causing the
goal to fail with an exception. Note that chapter 11 must be tested interactively with the VMEmulator as according to 
the text.