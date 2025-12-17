cd backend\analytics-service
mvn exec:java "-Dexec.mainClass=org.example.analyticsservice.DataSeederApplication" "-Dexec.args=--spring.profiles.active=seeder,dev"

