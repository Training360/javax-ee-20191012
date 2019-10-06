# Java EE képzés

```
standalone.bat -c=standalone-full.xml

jms-queue add --queue-address=EmployeeQueue --entries=java:/jms/queue/EmployeeQueue,java:jboss/exported/queue/EmployeeQueue

jms-queue count-messages --queue-address=EmployeeQueue
jms-queue list-messages-as-json --queue-address=EmployeeQueue

jms-topic add --topic-address=WarningTopic --entries=java:/jms/queue/WarningTopic,java:jboss/exported/queue/WarningTopic
```

`add-user`, szerepkör: `guest`

```
data-source add --name=EmployeeDS --jndi-name=java:/jdbc/EmployeeDS \
  --driver-name=mariadb-java-client-2.4.4.jar \
  --connection-url=jdbc:mysql://localhost/employees \
  --user-name=employees \
  --password=employees
/subsystem=datasources:read-resource
/subsystem=datasources:read-resource(recursive=true)
```

```
create schema if not exists employees default character set utf8 collate utf8_hungarian_ci;
create user 'employees'@'localhost' identified by 'employees';
grant all on *.* to 'employees'@'localhost';
```