javac -classpath . com/botbox/util/*.java
javac -classpath . se/sics/isl/transport/*.java
javac -classpath . se/sics/isl/util/*.java
javac -classpath . se/sics/tasim/props/*.java
javac -classpath . se/sics/tasim/logtool/*.java
javac -classpath . se/sics/tasim/tac03/*.java
javac -classpath . se/sics/tasim/tac04/*.java
javac -classpath . se/sics/tasim/tac05/*.java
javac -classpath . se/sics/tasim/visualizer/*.java
javac -classpath . se/sics/tasim/visualizer/gui/*.java
javac -classpath . se/sics/tasim/visualizer/info/*.java
javac -classpath . se/sics/tasim/visualizer/util/*.java
javac -classpath . se/sics/tasim/visualizer/monitor/*.java

jar cfm scmlogtool.jar RManifest.txt se/sics/tasim/visualizer/gui/images/*.* com/botbox/util/*.class se/sics/isl/transport/*.class se/sics/isl/util/*.class se/sics/tasim/props/*.class se/sics/tasim/logtool/*.class se/sics/tasim/tac03/*.class se/sics/tasim/tac04/*.class se/sics/tasim/tac05/*.class se/sics/tasim/visualizer/*.class se/sics/tasim/visualizer/gui/*.class se/sics/tasim/visualizer/info/*.class se/sics/tasim/visualizer/util/*.class se/sics/tasim/visualizer/monitor/*.class
