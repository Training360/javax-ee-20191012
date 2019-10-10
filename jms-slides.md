class: inverse, center, middle

# JMS

---
class: inverse, center, middle

# Bevezetés a Java Message Service használatába

---

## Message Oriented Middleware

* Rendszerek közötti üzenetküldés
* Megbízható üzenetküldés: store and forward
* Következő esetekben alkalmazható hatékonyan
  * Hívott fél megbízhatatlan
  * Kommunikációs csatorna megbízhatatlan
  * Hívott fél lassan válaszol
  * Terheléselosztás
  * Heterogén rendszerek
* Lazán kapcsolt rendszerek: nem kell ismerni a <br /> címzettet

---

## Point to point

![Point to point](images/jms-ptp.png)

---

## Publish and subscribe

<img src="images/jms-pas.png" alt="Publish and subscribe" width="600"/>

---

## JMS

* Szabványos Java API MOM-ekhez való hozzáféréshez
* Java EE része, de Java SE-ben is használható
* JMS provider
  * IBM MQ, Apache ActiveMQ (ActiveMQ 5 "Classic", ActiveMQ Artemis), RabbitMQ
* Hozzáférés JMS API-n keresztül

---

## Destination

* Az üzenet küldésének célja
* Az üzenet fogadásának forrása
* Point to point környezetben: sor (queue)
* Publish and subscribe környezetben: téma (topic)
* JNDI vagy dependency injection

---

## Üzenet küldése

```java
@ApplicationScoped
public class MessageSender {

  @Inject
  private JMSContext context;

  @Resource(mappedName = "java:/jms/queue/EmployeeQueue")
  private Queue queue;

  public void sendMessage(String name) {
    context.createProducer().send(queue, name);
  }
}
```

---

## Message

* `javax.jms.TextMessage extends javax.jms.Message`
* További interfészek: `BytesMessages`, `MapMessage`, `ObjectMessage`, `StreamMessage`, `Message`
* Factory metódusok: pl. `createTextMessage(String text)`, stb.

---

## Message Driven Bean

* EJB üzenetek fogadására
* Állapotmentes, a konténer hívja
* Esemény alapú (aszinkron) – üzenet beérkezés
* `@MessageDriven` annotation
* `MessageListener` interfész `onMessage` metódusa
* Tipikusan típuskényszerítés a megfelelő üzenet <br /> típusra, majd delegálás session beanhez

---

## Üzenetfogadás MDB-vel

```java
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/EmployeeQueue"
        )
})
public class EmployeesMessageDrivenBean implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("Message has arrived: " +
                    textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
```

---

## WildFly támogatás

* `full` konfigurációval kell indítani, ekkor aktiválódik a JMS

```
$ standalone.bat -c=standalone-full.xml
```

* Adminisztrációs felületen (cli/webes) sor felvétele

```
$ jboss-cli.bat --connect

jms-queue add --queue-address=EmployeeQueue --entries=java:/jms/queue/EmployeeQueue,java:jboss/exported/queue/EmployeeQueue

jms-queue count-messages --queue-address=EmployeeQueue
jms-queue list-messages-as-json --queue-address=EmployeeQueue

```

---
class: inverse, center, middle

# Java SE üzenetküldés

---

## Java SE üzenetküldés

```java
Properties jndiProperties = new Properties();
jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.jboss.naming.remote.client.InitialContextFactory");
jndiProperties.put(Context.URL_PKG_PREFIXES,
        "org.jboss.ejb.client.naming");
jndiProperties.put(Context.PROVIDER_URL,
        "http-remoting://localhost:8080");
jndiProperties.put("jboss.naming.client.ejb.context", true);

Context ctx = new InitialContext(jndiProperties);

ConnectionFactory connectionFactory = (ConnectionFactory) ctx
        .lookup("jms/RemoteConnectionFactory");

Destination destination = (Destination) ctx.lookup("/queue/EmployeeQueue");

try (JMSContext jmsContext = connectionFactory.createContext("guest1", "guest1")) {
    jmsContext.createProducer().send(destination, name);
}
```

---

## WildFly támogatás

* JNDI név: `java:jboss/exported/queue/EmployeeQueue`
* Autentikáció: `bin/add-user` (`guest` szerepkörrel)

---

## pom.xml állomány

```xml
<dependency>
    <groupId>org.wildfly</groupId>
    <artifactId>wildfly-jms-client-bom</artifactId>
    <version>17.0.1.Final</version>
    <type>pom</type>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>jcl-over-slf4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## pom.xml állomány - folytatás

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.22</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.22</version>
</dependency>
```

---
class: inverse, center, middle

# Aszinkron üzenetküldés

---

## Aszinkron üzenetküldés

```java
jmsContext.createProducer()                    
  .setAsync(new CompletionListener() {
    @Override
    public void onCompletion(Message message) {
      // On complete
    }

    @Override
    public void onException(Message message, Exception exception) {
      // On exception
    }
  })
```

---
class: inverse, center, middle

# Java SE üzenetfogadás

---

## Üzenetfogadás szinkron módon

```java
try (JMSContext jmsContext = connectionFactory.createContext("guest1", "guest1")) {
    String name = jmsContext.createConsumer(queue).receiveBody(String.class);
    System.out.println(name);
}
```

Opcionális timeout paraméter megadható

---

## Üzenetfogadás aszinkron módon

```java
try (JMSContext jmsContext = connectionFactory.createContext("guest1", "guest1")) {
    jmsContext.createConsumer(queue).setMessageListener(new EmployeeNameMessageListener());
}
```

```java
public class EmployeeNameMessageListener implements MessageListener {
  @Override
  public void onMessage(Message message) {
      try {
          System.out.println(message.getBody(String.class));
      }
      catch (JMSException e) {
          e.printStackTrace();
      }
  }
}
```

---
class: inverse, center, middle

# Queue browsing

---

## Queue browsing

* Üzenetek olvasása úgy, hogy közben a sorban maradnak
* `JMSContext.createBrowser()` metódus
* `QueueBrowser` interfész, `getEnumeration()` metódus

---
class: inverse, center, middle

# Mérgezett üzenetek

---

## Mérgezett üzenet

* Feldolgozás közben kivétel
* Visszakerül a sorba
* Újra megpróbáljuk feldolgozni: végtelenciklus

---

## JMS támogatás

* `getJMSRedelivered()` - újra kézbesítésre került-e
* `getIntProperty("JMSXDeliveryCount")` - hányszor került újra kézbesítésre

---

## WildFly támogatás

* Három redelivery után a DLQ-ba (dead letter queue) rakja

```
jms-queue count-messages --queue-address=DLQ

jms-queue move-messages --queue-address=DLQ --other-queue-name=EmployeeQueue
```

---

## WildFly konfiguráció

```xml
<!-- delay redelivery of messages for 5s -->
<!-- default is 0 (no delay) -->
<redelivery-delay>5000</redelivery-delay>
<!-- default is 1.0 -->
<redelivery-delay-multiplier>1.5</redelivery-delay-multiplier>
<!-- default is redelivery-delay * 10 -->
<max-redelivery-delay>50000</max-redelivery-delay>
```

---
class: inverse, center, middle

# Kérés-válasz üzenet


---

## Üzenet részei

* Fejléc
  * Szabványos JMS fejlécek
  * Message provider is definiálhat ilyeneket
  * Programozó is megadhat
  * Üzenetszűrésre és üzenetirányításra
* Törzs

---

## Kérés-válasz üzenet

* Külön sor kell
* `JMSMessageID` üzenet azonosító (explicit módon is megadható)
* `JMSCorrelationID` válaszban a kérés üzenet azonosítójára hivatkozás
* `JMSDestination` melyik sorba került az üzenet
* `JMSTimestamp`: az az időpont, mikor a JMS provider-nek az üzenet át lett adva küldésre
* `JMSReplyTo` melyik sorba várja a küldő az üzenetet

---

## Sor és topic létrehozása futás közben

* `JMSContext.createQueue(String)`
* `JMSContext.createTopic(String)`

---

## Temporary Destinations

* Ideiglenes destination létrehozása programozottan
* `JMSContext.createTemporaryQueue()`, `JMSContext.createTemporaryTopic()`
* Nincs neve, kívülről nem lehet hozzáférni
* Csak a létrehozó tud fogadni, bárki tud küldeni
* A kapcsolat bezárásakor megszűnik


---
class: inverse, center, middle

# Message selector

---

## Message selector

* Üzenet fogadásakor, browse esetén üzenetek szűrésére
* String típusú kifejezéssel adható meg
* Feltétel üzenet fejlécre és üzenet tulajdonságokra
* Nem lehet szűrni üzenet tartalomra

---

## Message selector nyelve

* Header mezők
* Üzenet property-k
* String literál (aposztróffal)
* Egész és lebegőpontos literál (int és double)
* Boolean literál (`TRUE` és `FALSE`)
* Zárójelek

---

## Operátorok

* Logikai operátor (`AND`, `OR`, `NOT`)
* Összehasonlító operátorok (`=`, `<>`, `<`, `<=`, `>`, `>=`) - egyenlőség egy jel!
* String összehasonlítás wildcarddal (`LIKE` és `_` és `%` wildcard karakterekkel)
* `IN`
* `BETWEEN` mindkét oldalról zárt intervallum
* `IS NULL`, `IS NOT NULL`
* Aritmetikai operátorok (`*`, `+`, `-`, `/`)

---

## Message selector használata

```java
jmsContext.createProducer()
                    .setProperty("type", "John Doe")
                    .send(destination, "employee");
```

```java
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/EmployeeQueue"
        ),
        @ActivationConfigProperty(
                propertyName = "messageSelector",
                propertyValue = "type = 'employee'")
})
```

`JMSContext.createConsumer()` és `JMSContext.createBrowser()` metódus második paraméterének

---

class: inverse, center, middle

# Message Acknowledgement

---

## Acknowledgement

* Acknowledgement hatására kerül ki a sorból
* Ack elmaradása esetén az üzenet visszakerül a sorba, és újra kézbesítésre kerül
* MDB `acknowledgeMode` property
* `ConnectionFactory.createContext()` metódusának adható meg paraméterként
* Fajtái
    * `Session.AUTO_ACKNOWLEDGE`: automatikusan a provider
    * `Session.CLIENT_ACKNOWLEDGE`: `acknowledge()` metódus hívandó a session összes üzenetére
    * `Session.DUPS_OK_ACKNOWLEDGE`: több üzenetre, gyorsabb, de előfordulhat duplikált üzenet

---

class: inverse, center, middle

# Message Persistence

---

## Message Persistence

* Típusai
  * `DeliveryMode.PERSISTENT`: default, a JMS provider leállása után is megmaradnak
  * `DeliveryMode.NON_PERSISTENT`: vissza nem állítható üzenetek: gyorsabb, kevesebb tárhelyet foglalnak
* Beállítása `JMSProducer.setDeliveryMode()` metódussal
* `JMSDeliveryMode` fejléc

---

class: inverse, center, middle

# Prioritás

---

## Prioritás

* Skála 0-9-ig (legalacsonyabb - legmagasabb)
* Default: 4
* Beállítása `JMSProducer.setPriority()` metódussal
* `JMSPriority` fejléc

---

class: inverse, center, middle

# Message Expiration és Delivery Delay

---

## Message Expiration és Delivery Delay

* `JMSProcuder.setTimeToLive()` metódus
* `JMSExpiration` fejléc
* `JMSProducer.setDeliveryDelay()`
* Erőforrás megtakarítás
* Időszinkronizálás a szerverek között

---

class: inverse, center, middle

# Durable Subscription

---

## Durable Subscription

* Feliratkozás: subscription, kell neki egy nevet adni
* Tartós feliratkozás: ha nem csatlakozik, a provider megőrzi az üzeneteket, és a következő csatlakozásnál megkapja
    * Client ID kell
* Üzeneteknek perzisztensnek kell lenniük
* Leiratkozás: `JMSContext.unsubscribe()` metódus

---

## WildFly támogatás

`standalone-full.xml` fájlban:

```xml
<role name="guest" send="true" consume="true"
  create-durable-queue="true"
  delete-durable-queue="true"
  create-non-durable-queue="true"
  delete-non-durable-queue="true"/>
```

---

## Feliratkozás

```java
try (JMSContext jmsContext = connectionFactory.createContext("guest1", "guest1")) {
  jmsContext.setClientID("client1");
  JMSConsumer consumer = jmsContext
                .createDurableConsumer(topic, "sub1");
  consumer.setMessageListener(this);

  // Wait
}
```

---

## Shared durable subscription

* Egy feliratkozáshoz több consumer
* Terheléselosztáshoz

---

class: inverse, center, middle

# Tranzakciókezelés

---

## Tranzakciókezelés

* Request-response esetben a küldést és a válasz fogadását ne tegyük egy tranzakcióba (deadlock)
* Elosztott tranzakciókezelés

---

class: inverse, center, middle

# Bridge

---

## Bridge

* Üzenetek átemelésére másik JMS providerből
