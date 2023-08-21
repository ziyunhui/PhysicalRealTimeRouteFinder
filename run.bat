@echo off
call mvn package
pause
call java -Dfile.encoding=utf-8 -jar ./target/PhysicalRealTimeRouteFinder-1.0-SNAPSHOT.jar
pause