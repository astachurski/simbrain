# Introduction #

This page is for those who want to contribute to Simbrain development. Notes for Eclipse users are below.   You will also need to use checkstyle, a program we use to ensure code consistency.    Of course, you will also need to contact an administrator to become a project member  before you can commit changes.

## Eclipse Compile Error Note ##

For some reason OSCWorld and 3dWorld are sometimes added to a new Simbrain project in an incorrect state.   To fix this go to Project > Properties > Source and remove everything but simbrain/src; also make sure that the "Excluded list" in the drop-down menu is empty.

## Checkstyle ##

### Install eclipse checkstyle ###

From eclipse click on Help > Install New Software...

Next click on the  Add button and enter "http://eclipse-cs.sourceforge.net/update" in the "Location" text field.  Enter a name for the configuration such as "Checkstyle"

Click OK and then put a check mark next to the entry http://eclipse-cs.sourceforge.net/update in the list and click Install.

After Eclipse has finished checking the program for requirements and dependancies, click Next, accept the license agreement and click Finish.

Finally, restart eclipse when prompted to do so by clicking Yes in the dialog box.

### Configure eclipse checkstyle ###

Right click on the Simbrain project in the package explorer and select Properties and click on Checkstyle in the left frame.

Select the "Local Check Configurations" tab, and click on New.

Select External Configuration File from the Type:  drop down menu.

Enter a name for the configuration such as "Simbrain Checkstyle"

Click on Browse and locate the  Simbrain\_Root/etc/checkstyle.xml file and click OK.

Click on OK and then select Main and select the configuration file that was just imported (e.g. "Simbrain Checkstyle - (Local)") (Note: if the window size is too small, use the scroll bar to scroll to the top of the checkstyle frame.)

Finally click the check box labeled Checkstyle active for this project to activate checkstyle and click OK to accept the settings. Eclipse will now ask to rebuild the project, click OK and any outstanding checkstyle errors will now be displayed.

### Configure Eclipse to fix checkstyle errors ###

One quick way to fix checkstyle formatting  errors is to invokoke the format command.

This still does not solve trailing whitespace problems, and in fact adds some trailing whitespace that checkstyle will complain about.  A quick way to fix this is by adding a key binding so that control-R calls the eclipse command "Remove Trailing Whitespace".  Here are the steps:

  1. Eclipse (Or Window) > Preferences > General > Keys > "Remove Trailing WhiteSpace"
  1. In binding click "control R".
  1. Click OK

With these two steps you can easily clear up a lot of checkstyle errors quickly, by doing invoking the format command (control-shift-F) then remove trailing white space (control-R).

Also, note that since our checkstyle requires spaces, this may have to be changed in older versions of  Eclipse.  Here's an entry on how to do it:

http://blog.dotkam.com/2007/03/21/changing-tabs-to-spaces-in-eclipse/

## Log4J ##

### Import log4j JAR into project ###

Right click on the project and select "properties" from the menu.

Select "Java Build Path" from the frame on the left and then Libraries from the right frame.

Make sure that the log4j jar file is listed. If it is not listed, add it to the build path by clicking on "Add JARs..." then selecting it from the "lib" directory within the build project. After the JAR is verified to be in the build path, click "Ok"

### Configure log4j ###

Click on the "Run" menu then go to "Run Configurations"   From here there are two cases:

(If Running Simbrain for the first time, you must create a run configuration.  Double click on "Java Application" in the left frame. Change the name to something like "Simbrain" or some other descriptive term. Next under "Project:" heading click "Browse" and select the Simbrain project folder. Then under the "Main class:" heading type "org.simbrain.workspace.gui.Splasher")

### Import log4j properties file ###

Assuming the Simbrain run configuration is selected from the left frame:

  1. Select "Classpath" from the right frame and click on "User Entries" in the main panel
  1. Click on the "Advanced" button on the right
  1. Select "Add Folders" in the dialog and click "OK,"
  1. Select the "etc" folder within the project directory. Click "OK" then "Apply" and run the project.

### Configure log4j file ###

The log4j file  ({Simbrain}/etc/log4j.properties) can now be configured to show as much debug information as you like.  For more on log4j property file configuration see http://logging.apache.org/log4j/1.2/manual.html and http://jessehu.wordpress.com/2009/11/17/log4j-levels-all-trace-debug-info-warn-error-fatal-off/

By default logging is turned off in the properties file.  To see a bunch of debug information you can, for example, change this line

```
log4j.logger.org.simbrain=INFO
```

to

```
log4j.logger.org.simbrain=DEBUG
```