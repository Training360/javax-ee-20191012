# Java EE képzés

```
standalone.bat -c=standalone-full.xml

jms-queue add --queue-address=EmployeeQueue --entries=java:/jms/queue/EmployeeQueue,java:jboss/exported/queue/EmployeeQueue
```

```
data-source add --name=EmployeeDS --jndi-name=java:/jdbc/EmployeeDS \
  --driver-name=mariadb-java-client-2.4.4.jar \
  --connection-url=jdbc:mysql://localhost/employees \
  --user-name=employees \
  --password=employees
/subsystem=datasources:read-resource
/subsystem=datasources:read-resource(recursive=true)
```