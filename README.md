# Java EE k�pz�s

```
standalone.bat -c=standalone-full.xml

jms-queue add --queue-address=EmployeeQueue --entries=java:/jms/queue/EmployeeQueue,java:jboss/exported/queue/EmployeeQueue
```