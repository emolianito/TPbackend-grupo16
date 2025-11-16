FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR generado
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV KEYCLOAK_ISSUER_URI=http://keycloak:8080/realms/tpi-backend

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]