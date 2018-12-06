TAC SCM Visualizer
------------------

 Release date: 2005-04-15
 Current Version: 0.4.1beta
 Contact: tac-dev@sics.se
 Requires: Java SDK 1.4.2


Release Notes
-------------

The SCM Visualizer tool is currently being developed to allow a better
understanding of the game by studying what really happened during a
game. Since it is still in an early stage of development it is
a bit rough around the edges.

If you have any suggestions or comments about the tool or if you have
discovered a bug please contact: tac-dev@sics.se


To Compile the SCM Visualizer
-----------------------------

Type "compile.bat" (or "compile.sh" for Unix) to compile the visualizer


To Run the SCM Visualizer
-------------------------

1) Acquire the log file of the game you wish to study.

   You can download them from the TAC server where the game was
   played. Please do not download more logs than you are _REALLY_
   interested in from the sics.se servers, since massive leaching will
   lead to poor performance for contestants in the games. This
   restriction will be lifted when we have transferred the log files
   to a real web server.

2) Start the visualizer by writing

   java -jar scmlogtool.jar [-xml] -file <game-log-file>

   The -xml option will generate an XML formated dump of the log file
   to stdout.

   Note: You do not need to unpack the log files before running the
   visualizer on them.

   Note: The -Xmx option that was required to run version 0.1 should no
   longer be needed. If however you get an Out of memory exception you
   can try using the -Xmx 500000000.


News in version 0.4
-------------------

 * Updated to handle changes in specification for TAC'05

 * Some bugs have been fixed.


News in version 0.3
-------------------

 * The concept of monitors has been added. Monitors allow you to tap in
   to the log files and generate your own visualization and perform
   your own analysis of the data. Some sample monitors are included.

 * Detailed information about PC orders has been enhanced to show more
   information.

 * Detailed information about component orders has been added.

 * The customer is now "click-able" displaying more detailed information
   about demand.

 * Some bugs have been fixed.


News in version 0.2.1
-----------------------

 * The suppliers are now "click-able" displaying more detailed information
   and some graphs.

 * The memory consumptions has been reduced to half of the previous version,
   eliminating the need for the -Xmx flag.

 * The day selecting slider has been modified to reduce CPU load while
   sliding.

 * New graphs have been added to the window displaying agent information.

 * Several bugs in the log file parser have been fixed, hopefully making
   it more stable.

 * The arrows displaying communication between the actors have been
   slightly separated to avoid them from hiding each other.

 * A bug causing erroneous information to be displayed about how many RFQs
   was sent to suppliers was fixed.

 * Formating has been applied to numeric values to make large numbers
   more readable.

 * A list with more detailed information about PC orders has been added.
