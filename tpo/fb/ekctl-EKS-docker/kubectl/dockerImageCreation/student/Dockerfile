# Use official Tomcat image that includes Java
FROM tomcat:9.0.0

# Remove default webapps5(optional)
#RUN rm -rf /usr/local/tomcat/webapps/*

# Remove all default webapps except ROOT
RUN find /usr/local/tomcat/webapps/ ! -name ROOT -type d -exec rm -rf {} +
COPY index.jsp /usr/local/tomcat/webapps/ROOT/index.jsp


# Copy your WAR file
COPY student.war /usr/local/tomcat/webapps/student.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
