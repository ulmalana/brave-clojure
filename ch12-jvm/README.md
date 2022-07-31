# Working with JVM

In this chapter we try to understand how Clojure works as a hosted language in JVM.

## Packages and Imports

* `package`: similar to Clojure's namespaces. It defines how to organize the code.
* `import`: allows us to import classes.

## JAR files

JAR (*Java Archive*) files are used to bundle `.class` files into a single file. In the following example, we try to bundle some classes with entry point `PirateConversation` and we name it `conversation.jar`
```bash
$ jar cvfe conversation.jar PirateConversation PirateConversation.class pirate_phrases/*.class
$ java -jar conversation.jar
```
