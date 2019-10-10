class: inverse, center, middle


# Perzisztencia, adatbázis programozás JPA technológiával

---

class: inverse, center, middle



# Többértékű attribútumok

---

## Collectionök

* `Collection`, `List`, `Set`, `Map`
* Egyszerű típusok, vagy `@Embeddable` annotációval ellátott osztályok
* `@ElementCollection` annotáció

---

## Egyszerű típus

```java
@ElementCollection
private Set<String> nickNames;
```


---

## Példa embeddable

```java
@Embeddable
public class VacationEntry {

    private LocalDate startDate;

    private int daysTaken;

    // ...
}
```

```java
@ElementCollection
private Set<VacationEntry> vacationBookings;
```

---

## Személyre szabás

```java
@ElementCollection
@CollectionTable(name="NICKNAMES", joinColumns=@JoinColumn(name="EMP_ID"))
@Column(name="NICKNAME")
private Set<String> nickNames;
```

```java
@ElementCollection(targetClass=VacationEntry.class)
@CollectionTable(name="VACATIONS", joinColumns=@JoinColumn(name="EMP_ID"))
@AttributeOverride(name="daysTaken", column=@Column(name="DAYS_ABS"))
private Set<VacationEntry> vacationBookings;
```

---

## Map

* Egyszerű és embeddable típusok tetszőleges kombinációja

```java
@ElementCollection
@CollectionTable(name="EMP_PHONE")
@MapKeyColumn(name="PHONE_TYPE")
@Column(name="PHONE_NUM")
private Map<String, String> phoneNumbers;
```

---

## Lazy kapcsolat

* Lazy: csak szükség esetén tölti be az attribútumhoz tartozó értékeket
* Felülbírálása az `@ElementCollection` `fetch` attribútumával (`FetchType.EAGER`)
	* Nem javasolt, mert ez statikus

---

## N + 1 probléma

* Egy lekérdezés az entitásra, majd entitásonként a kapcsolódó attribútumokra
* Megoldás: `join fetch`

---

## Lazy kapcsolat

* `LazyInitializationException`, csak Hibernate esetén
* Detach-elt entitáson lazy kapcsolat betöltése
* Megoldás: `join fetch`

---

class: inverse, center, middle



# Kapcsolatok

---

## Kapcsolatok tulajdonságai

* Számosság
    * Egy-egy
    * Egy-több
    * Több-több
* Irányítottság
    * Egyirányú
    * Kétirányú

---

## Egyirányú egy-egy kapcsolat

* `@OneToOne` annotáció
* `@JoinColumn` annotáció

---

## Kétirányú egy-egy kapcsolat

* `@OneToOne` annotáció
* `@JoinColumn` annotáció
* `mappedBy` attribútum
* Külön metódus a két irány beállítására

---

## Kétirányú egy-több kapcsolat

* `@OneToMany` annotáció `mappedBy` attribútummal
* `@ManyToOne` annotáció
* Külön metódus a két irány beállítására

---

## getReference

* `EntityManager.getReference()`
* Ha a kapcsolathoz be kell tölteni az entitást
* Proxy-t ad vissza

```java
Department dept = em.getReference(Department.class, 30);
```

---

## Kaszkádolt műveletek

* Művelet elvégzése az entitáson és a kapcsolódó entitáson is

```java
@Entity
public class Employee {
    // ...

    @ManyToOne(cascade=CascadeType.PERSIST)
    private Address address;

    // ...
}
```

---

## Orphan removal

* Szülő rekord eltávolításakor árva marad, törölhető (hasonló, mint a `CascadeType.REMOVE` funkcionalitása)
* Ami több: nem csak törléskor, hanem kapcsolat megszűntetésekor is törli a kapcsolt entitást

```java
@Entity
public class Employee {
    @Id 
    private int id;

    @OneToMany(orphanRemoval = true)
    private List<Evaluation> evals;
    
    // ...
}
```

---

## Sorrendezés

* Attribútum alapján: `@OrderBy` annotáció
* Erre kijelölt mező alapján, JPA tartja karban: `@OrderColumn` annotáció

---

## Szülő oldal

* Owner of relationship, owner side
    * Másik oldal: inverse side
* Ahol a `mappedBy` van, az az inverse oldal
* Ahol a join column van, az az owner side

---

## Lazy kapcsolat

* Eager: betölti a kapcsolódó entitást (entitásokat)
* Lazy: csak szükség esetén tölti be a kapcsolódó entitásokat
* `@ElementCollection` esetén is, alapesetben eager
* `@OneToOne` és `@ManyToOne` alapesetben eager
* `@OneToMany` és `@ManyToMany` alapesetben lazy
* Felülbírálása a `fetch` attribútummal 	
    * `FetchType.EAGER` és `FetchType.LAZY`
    * Nem javasolt, mert ez statikus

---

## N + 1 probléma

* Egy lekérdezés az entitásra, majd entitásonként a kapcsolódó entitásra
* Megoldás: `join fetch`

---

## Lazy kapcsolat

* `LazyInitializationException`, csak Hibernate esetén
* Detach-elt entitáson lazy kapcsolat betöltése
* Megoldás: `join fetch`

---

## Entity graph

* Mi kerüljön betöltésre
* Lekérdezésben hint megadása
    * `javax.persistence.fetchgraph` - entitáshoz tartozó alapértelmezett entity graph-ot nem veszi figyelembe
    * `javax.persistence.loadgraph`
* Alapértelmezett entity graph: alapértelmezett és annotációkkal megadott lazy betöltési tulajdonságok
* Megadása annotációkkal vagy programozottan

---

## Entity graph deklarálása

```java
@NamedEntityGraph(name = "graph.Employee.phones",
    attributeNodes = @NamedAttributeNode("phones"),
    subgraphs = {
        @NamedSubgraph(name = "phones", 
            attributeNodes = {@NamedAttributeNode("type")})
})
```

---

## Entity graph használata

```java
Map hints = new HashMap();
hints.put("javax.persistence.fetchgraph", 
    em.getEntityGraph("graph.Employee.phones"));
return em.find(Employee.class, id, hints);
```


---

class: inverse, center, middle



# Több-több kapcsolatok

---

## Több-több kapcsolat

* Mindkét oldalon kollekció
* `@ManyToMany`, inverz oldalon `mappedBy`

---

## Több-több kapcsolat

```java
@Entity
public class Employee {

    @Id private long id;
    private String name;

    @ManyToMany
    private Collection<Project> projects;

    // ...
}

@Entity
public class Project {

    @Id private long id;
    private String name;

    @ManyToMany(mappedBy="projects")
    private Collection<Employee> employees;

    // ...
}
```

---

## Join table

```java
@ManyToMany
@JoinTable(name="EMP_PROJ",
joinColumns=@JoinColumn(name="EMP_ID"),
inverseJoinColumns=@JoinColumn(name="PROJ_ID"))
private Collection<Project> projects;
```

---

class: inverse, center, middle



# Deklaratív tranzakciókezelés

---

## Tranzakciókezelés

![Tranzakciókezelés](images/tranzakcio-kezeles.png)

---

## Propagáció

![Propagáció](images/propagacio.png)

---

## Propagációs tulajdonságok

* `REQUIRED` (default): ha nincs tranzakció, indít egyet, ha van csatlakozik hozzá
* `REQUIRES_NEW`: mindenképp új tranzakciót indít
* `SUPPORTS`: ha van tranzakció, abban fut, ha nincs, nem indít újat
* `MANDATORY`: ha van tranzakció, abban fut, ha nincs, kivételt dob
* `NOT_SUPPORTED`: ha van tranzakció, a tranzakciót felfüggeszti, ha nincs, nem indít újat
* `NEVER`: ha van tranzakció, kivételt dob, ha nincs, <br /> nem indít újat

---

## Izoláció

* Izolációs problémák:
    * dirty read
    * non-repetable read
    * phantom read
* Izolációs szintek:
    * read uncommitted
    * read commited 
    * repeatable read
    * serializable

---

## Visszagörgetési szabályok

* Kivételekre lehet megadni, hogy melyik esetén történjen rollback
* Rollbackre explicit módon megjelölni
* Konténer dönt a commitról vagy rollbackről

---

## Timeout

* Timeout esetén kivétel

---

## Csak olvasható

* Spring esetén további optimalizációkat tud elvégezni, cache-eléssel kapcsolatos