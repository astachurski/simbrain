For those who simply want to run the current version of the code from source, you will need to install (1) the JDK, (2) Ant, and (3) subversion.   Installation notes on each of these is below.

Once you have installed all three, to run Simbrain, open up a command or terminal window.   To verify you have 1-3 above, try typing

```
javac
ant
svn
```

from the command line.   If something besides an error comes back, you have installed them successfully.

If you get an error, you may have to set an environment variable.  See the section on environment variables below.

To run Simbrain go to the directory where you installed it, and enter

```
ant run
```

## JDK ##

Download and install the latest jdk for your computer following instructions [here](http://java.sun.com/javase/downloads/index.jsp)

## Ant ##

(This is pre-installed on some systems)

Download ant [here](http://ant.apache.org/bindownload.cgi)

Extract zip file.  You will end up with an ant folder.  Installation simply corresponds to placing this folder in an appropriate location (for example, on windows, C:\\Program Files\)

See: http://ant.apache.org/manual/install.html

## Subversion ##

Download and install subversion [here](http://subversion.apache.org/packages.html). Windows users click on "windows binaries", Mac users click on "Mac OS X binaries", etc.   SlikSVN appears to work well for Windows.  For Mac use brew (see "Note to Mac Users" below)

## Getting the Code ##

Once subversion is installed, check out the latest version of Simbrain by following the directions [here](http://code.google.com/p/simbrain/source/checkout)

## Note for Mac Users ##

On Mac, it is much easier to install ant and subversion using brew (or apt-get, but that has not been tested).  To install brew, go here, and scroll to the bottom:

[http://brew.sh/](http://brew.sh/)

With your terminal still open, enter the following to install ant and subversion

```
brew install ant
brew install subversion
```

It's that easy!  Perhaps javac can also be installed this way (let us now if it can).

## Setting environment variables ##

For Windows users, ant and jdk must be added to your path, as follows:

  1. Right click on my computer then go to properties.
  1. Select the advanced tab and then click on the environment variables button at the bottom
  1. Under System variables, select the Path variable and click edit.
  1. At the end of the paths, make sure there is a ";" ending the line
  1. Add the path to the ant bin folder that was extracted earlier (ex C:\Program Files\apache-ant-1.7.1\bin)
  1. Add the path to the Java JDK folder making sure that there is a semicolon separating the two entries. (ex C:\Program Files\Java\jdk1.6.0\_03\bin)

For Windows users, the JAVA\_HOME system variable must be set in order for ant to find the jdk java compiler. To do so follow these steps:

  1. Right click on my computer then go to properties.
  1. Select the advanced tab (for Vista click the advanced system properties menu and click the advanced tab).
  1. Under the system variables check to see if there is a JAVA\_HOME variable.  If not, click the new button and enter JAVA\_HOME for the variable name.
  1. Enter the path the jdk folder (ex C:\Program Files\jdk) for the variable value.

## Further Configuration ##

For more on setting up your development environment see [Configuration](Configuration.md) and [RunningSimbrainFromEclipse](RunningSimbrainFromEclipse.md).