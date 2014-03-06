#!/bin/sh
cd server/
echo "*** Building server jar ***"
sbt assembly
echo "*** Copying to builds/ ***"
cp target/scala-2.10/RobolabSim.jar builds/RobolabSim-latest.jar
echo "*** Copying to package/ ***"
cp target/scala-2.10/RobolabSim.jar ../package/RobolabSim.jar

<<<<<<< HEAD
cd ../testing/
echo "*** Building testing jar ***"
sbt assembly
echo "*** Copying to builds/ ***"
cp target/scala-2.10/RobolabSimTest.jar builds/RobolabSimTest-latest.jar
echo "*** Copying to package/ ***"
cp target/scala-2.10/RobolabSimTest.jar ../package/RobolabSimTest.jar

=======
>>>>>>> upstream/master
cd ..
echo "*** Copying content from client/ to package/ ***"
cp -r client/* package/solution
