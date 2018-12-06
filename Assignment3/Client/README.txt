This is	version 0.9.6 beta of the TAC SCM AgentWare for Java.

The purpose of this release is to provide a basic set of tools for
developing TAC SCM agents.  This release contains:

- preliminary APIs for TAC SCM agents
- basic setup for communicating with the TAC SCM server
- better support for TAC05 SCM games
- example agent implementation

More information about TAC SCM agent development is available at
http://www.sics.se/tac/page.php?id=14

You will need Java SDK 1.4.2 or newer (you can find it at
http://java.sun.com) to be able to develop and run this AgentWare.


Getting the AgentWare to run.
------------------------------

Compiling
---------
Type "compile.bat" (or "compile.sh" for unix) to compile the AgentWare

Running
-------
Register your agent at http://tac3.sics.se:8080/ and then enter
your agent name and password in the configuration file 'aw.conf'.

Then type "java -jar scmaw.jar" to run an example agent

(or "java -classpath scmaw.jar:$CLASSPATH se.sics.tasim.aw.client.Main"
 if you need to use the standard Java class path).

If everything is all right the AgentWare will open a simple status
window showing the agent's current status.

Simple game results can be found at http://tac3.sics.se:8080/
For information about other TAC SCM servers please see
http://www.sics.se/tac/scmserver/


Configuring the AgentWare
-------------------------
The AgentWare reads the configuration file 'aw.conf' at startup. This
file allows, among other things, the configuration of agent
implementations. See the file 'aw.conf' for more information.

Note: by default the AgentWare will automatically create and join a
new game after a game has ended. You can specify how many games it
automatically should create and join by setting the 'autojoin' option
in the configuration file 'aw.conf'.


If you have any questions or comments regarding this AgentWare please
contact tac-dev@sics.se

-- The SICS TAC Team
