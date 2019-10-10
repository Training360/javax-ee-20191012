class: inverse, center, middle

# Arquillian

---

class: inverse, center, middle

## Integrációs tesztelés Arquillian használatával

---

## Arquillian

* Integrációs és funkcionális tesztek futtatása eredeti futtatókörnyezet (konténeren) belül
	* Java EE alkalmazásszerver
	* Servlet container (Tomcat, Jetty, stb.)
	* CDI implementáció
	* OSGi konténer
* Tulajdonságai
	* Konténer életciklusának vezérlése (indítás, leállítás, telepítés)
	* Tesztek felruházása új lehetőségekkel (pl. dependency injection)

---

## Célkitűzések

* Legyen hasonlóan egyszerű, mint a unit tesztek futtatása
* Támogassa a konténer cserélhetőséget
* IDE-ből és build eszközből is futtatható legyen
* Létező teszt eszközökhöz integrálható legyen

---

## Működési mód

* In-container működési mód
* Teszt osztályonként elkészít egy telepítőcsomagot, melyben benne van a teszt eset is
* Telepíti a szerverre
* Konténeren belül futtatja
	* `System.out.println` alkalmazásszerver napló állományában jelenik meg
* Eredményt kijuttatja a teszt futtató keretrendszernek
* A `@BeforeClass` és `@AfterClass` metódusok a kliens JVM-ben futnak
* Egyelőre nem képes ugyanazt a deploymentet több teszt osztályon át használni (fejlesztés folyamatban: [ARQ-197](https://issues.jboss.org/browse/ARQ-197))

---

## Teszt eset

* Un. _micro deployment_ összeállítása `ShrinkWrap` segítségével
	* Mi kerüljön be a jar, war, ear állományba, mely telepítésre kerül
	* Akár pár komponens (esetleg mock függőségekkel)
	* Kisebb scope - hiba könnyebben azonosítható
* Konténer szolgáltatások elérése
	* Dependency injection (field és teszt metódus paraméterre is)
	* `@Inject`, `@Resource`, `@EJB`, `@PersistenceContext` és `@PersistenceUnit` annotációk

---

## Teszt eset implementáció

```java
@RunWith(Arquillian.class)
public class NameTrimmerIntegrationTest {

    @Inject
    private NameTrimmer nameTrimmer;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(NameTrimmer.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testTrim() {
        assertEquals("John Doe", nameTrimmer.trimName("  John Doe  "));
    }

}
```

---

## `pom.xml`

```xml
 <dependency>
	<groupId>org.jboss.arquillian.core</groupId>
	<artifactId>arquillian-core-api</artifactId>
	<version>1.4.1.Final</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.jboss.arquillian.junit</groupId>
	<artifactId>arquillian-junit-container</artifactId>
	<version>1.4.1.Final</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.wildfly.arquillian</groupId>
	<artifactId>wildfly-arquillian-container-remote</artifactId>
	<version>2.1.1.Final</version>
	<scope>test</scope>
</dependency>
```

---

## Deployment ellenőrzése

* `System.out.println(webArchive.toString(true));`
* `src/test/resources/arquillian.xml`

```xml
<?xml version="1.0"?>
<arquillian>
    <engine>
        <property name="deploymentExportPath">target/deployments</property>
    </engine>
</arquillian>
```

---

class: inverse, center, middle

## Adatbázis réteg integrációs tesztelése Arquilliannal

---

## Adatbázis tesztelés

* Nem unit teszteljük
* Konténerben érdemes a konténer által biztosított szolgáltatások miatt
	* Pl. deklaratív tranzakciókezelés
	* Dependency injection, pl. `@PersistenceUnit`

---

## Idempotencia és izoláltság

* Tesztesetek egymásra hatással vannak
    * Állapot: pl. adatbázis
* Ugyanazon tesztkörnyezeten több tesztelő vagy harness dolgozik
* Megoldás:
    * Teszteset "rendet tesz" maga előtt, un. set-up
    * "Rendet tesz" maga után, un. tear down
		* Rollback?
    * Test fixture
        * Legszélsőségesebb megoldás: adatbázis törlése

---

## Deployment

* Entitások és `Dao` osztály
* `persistence.xml`

```java
WebArchive webArchive =
        ShrinkWrap.create(WebArchive.class)
                .addClasses(Employee.class, EmployeeDaoBean.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
```

---

## Adatbázis inicializálás

* Séma inicializáció
  * Alkalmazáson kívül
  * JPA schema generation
  * Alkalmazáson belül: Flyway vagy Liquibase - `DataSource` injektálható
* Adat inicializáció
  * JDBC, SQL utasításokkal - `DataSource` injektálható
  * JPA-val - `@PersistenceContext EntityManager em;` használható
  * `@Transactional` annotáció csak a `@Test` annotációval ellátott metódusra tehető
  * Dao-val
  * DbUnit használatával, `arquillian-persistence-dbunit` Arquillian extension

---

## Séma inicializáció alkalmazáson belül

* Adott library (Flyway vagy Liquibase) deploymentben elhelyezendő (Maven függőség alapján)
* Konfigurációs állományok deploymentben elhelyezendőek

```xml
<dependency>
    <groupId>org.jboss.shrinkwrap.resolver</groupId>
    <artifactId>shrinkwrap-resolver-impl-maven</artifactId>
    <version>3.1.3</version>
    <scope>test</scope>
</dependency>
```

```java
.addAsLibraries(Maven.configureResolver().loadPomFromFile("pom.xml")
  .resolve("org.flywaydb:flyway-core").withoutTransitivity().asSingleFile())
```

---

## Resource állományok

A `resources` könyvtárban lévő állományok a deploymentbe

```java
Files.walk(Paths.get("src/main/resources"))
        .filter(p -> !p.toString().contains("META-INF"))
        .filter(p -> Files.isRegularFile(p))
        .map(p -> Paths.get("src/main/resources").relativize(p))
        .map(p -> p.toString().replace("\\", "/"))
        .forEach(s -> webArchive.addAsResource(s, s));
```

---

## Adatbázis inicializálás JDBC-vel

```java
@RunWith(Arquillian.class)
public class EmployeeDaoBeanIntegrationTest {

  // ...

  @Resource(lookup = "java:/jdbc/EmployeeDS")
  private DataSource dataSource;

  @Before
  public void init() throws Exception {
      try (Connection c = dataSource.getConnection();
           PreparedStatement ps = c.prepareStatement("delete from employees")) {
           ps.executeUpdate();           
      }
      try (Connection c = dataSource.getConnection();
           PreparedStatement ps = c.prepareStatement("insert into employees(name) values (?)")) {
           ps.setString("name", "John Doe");
           ps.executeUpdate();           
           ps.setString("name", "Jack Doe");
           ps.executeUpdate();           
      }
  }  
}
```

---

## Adatbázis inicializálás Dao-val

```java
@RunWith(Arquillian.class)
public class EmployeeDaoBeanIntegrationTest {

  // ...

  @Resource(lookup = "java:/jdbc/EmployeeDS")
  private DataSource dataSource;

  @Inject
  private EmployeeDaoBean employeeDaoBean;

  @Before
  public void init() throws Exception {
      try (Connection c = dataSource.getConnection();
           PreparedStatement ps = c.prepareStatement("delete from employees")) {
           ps.executeUpdate();           
      }
      employeeDaoBean.saveEmployee(new Employee("John Doe"));
      employeeDaoBean.saveEmployee(new Employee("Jack Doe"));
  }  
}
```

---

## Teszt eset

```java
@RunWith(Arquillian.class)
public class EmployeeDaoBeanIntegrationTest {

  @Inject
  private EmployeeDaoBean employeeDaoBean;

  @Test
  public void testFindEmployees() {
    List<Employee> employees = employeeDaoBean.findEmployees();

    // Assert esetén vigyázzunk a sorrendre: order by
    assertEquals(Arrays.asList("Jack Doe", "John Doe"), employees.stream()
                .map(Employee::getName).collect(Collectors.toList()));
  }
}
```

---

## Teszt eset - inicializáció a given részben

```java
@RunWith(Arquillian.class)
public class EmployeeDaoBeanIntegrationTest {

  @Inject
  private EmployeeDaoBean employeeDaoBean;

  @Test
  public void testFindEmployees() {
    // Given
    employeeDaoBean.saveEmployee(new Employee("John Doe"));
    employeeDaoBean.saveEmployee(new Employee("Jack Doe"));

    // When
    List<Employee> employees = employeeDaoBean.findEmployees();

    // Then
    assertEquals(Arrays.asList("Jack Doe", "John Doe"), employees.stream()
                .map(Employee::getName).collect(Collectors.toList()));
  }
}
```
