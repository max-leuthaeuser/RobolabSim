cd server/
sbt assembly
cp target/scala-2.10/RobolabSim.jar builds/RobolabSim-latest.jar
cp target/scala-2.10/RobolabSim.jar ../package/RobolabSim.jar

cd ../testing/
sbt assembly
cp target/scala-2.10/RobolabSimTest.jar builds/RobolabSimTest-latest.jar
cp target/scala-2.10/RobolabSimTest.jar ../package/RobolabSimTest.jar