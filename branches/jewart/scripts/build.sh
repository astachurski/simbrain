CLASSPATH=".:./src:./lib/utils.jar:./lib/hisee.jar:./lib/Jama-1.0.1.jar:./lib/simnet.jar:./lib/piccolo.jar:./lib/piccolox.jar:./lib.:./lib/calpahtml.jar:./lib/castor.jar:./lib/snarli.jar:./lib/jlinalg.jar:./lib/xerxes.jar"
javac -d ./bin -classpath $CLASSPATH ./src/org/simnet/*.java
javac -d ./bin -classpath $CLASSPATH ./src/org/simnet/*/*.java
javac -d ./bin -classpath $CLASSPATH ./src/org/simnet/*/*/*.java
javac -d ./bin -classpath $CLASSPATH ./src/org/simbrain/*/*.java 
javac -d ./bin -classpath $CLASSPATH ./src/org/simbrain/*/*/*.java 
cp ./src/org/simbrain/resource/*.gif  ./bin/org/simbrain/resource