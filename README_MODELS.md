# Démarrer en DEV
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Démarrer en PROD
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Démarrer en TEST
./mvnw test -Dspring.profiles.active=test


# Compiler
./mvnw clean package -DskipTests

# Exécuter en DEV
java -jar target/om_pay-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Exécuter en PROD
java -jar target/om_pay-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod