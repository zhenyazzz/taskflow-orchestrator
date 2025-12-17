@echo off
cd /d %~dp0
mvn exec:java -Dexec.mainClass="org.example.analyticsservice.DataSeederApplication" -Dexec.args="--spring.profiles.active=seeder,dev"
pause

