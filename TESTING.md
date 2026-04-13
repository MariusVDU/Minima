# Automatinių testų dokumentacija

## 1. Apžvalga

Šiame projekte yra **217 automatinių testų** naudojant JUnit 5, Mockito ir MySQL integracinius testus. Testai dengia CRUD operacijas, validacijas, klaidų apdorojimą, HTTP sluoksnį ir realią MySQL prieigą.

### Testų statistika

| Kategorija | Klasė | Testų sk. |
|------------|-------|-----------|
| Vienetų testai | `KategorijaServiceTest` | 12 |
| Vienetų testai | `PrekeServiceTest` | 17 |
| Vienetų testai | `DarbuotojasServiceTest` | 16 |
| Vienetų testai | `PareigosServiceTest` | 12 |
| Vienetų testai | `ParduotuveServiceTest` | 13 |
| Vienetų testai | `InventoriusServiceTest` | 15 |
| Vienetų testai | `PardavimasServiceTest` | 7 |
| Vienetų testai | `PardavimoEiluteServiceTest` | 8 |
| Integraciniai testai | `KategorijaControllerTest` | 15 |
| Integraciniai testai | `PrekeControllerTest` | 18 |
| Integraciniai testai | `DarbuotojasControllerTest` | 18 |
| Integraciniai testai | `InventoriusControllerTest` | 10 |
| Integraciniai testai | `PardavimasControllerTest` | 10 |
| Integraciniai testai | `PardavimoEiluteControllerTest` | 10 |
| Integraciniai testai | `PareigosControllerTest` | 10 |
| Integraciniai testai | `ParduotuveControllerTest` | 10 |
| MySQL integraciniai testai | `KategorijaRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `InventoriusRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `PrekeRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `PareigosRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `ParduotuveRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `PardavimoEiluteRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `PardavimasRepositoryMySqlTest` | 2 |
| MySQL integraciniai testai | `DarbuotojasRepositoryMySqlTest` | 2 |
| **Viso** | | **217** |

---

## 2. Kaip paleisti testus

### Reikalavimai

- Java 17+ (rekomenduojama: `C:\Programos\JDK-24\jdk-24`)
- Apache Maven 3.9+ (`C:\Programos\apache-maven-3.9.5\bin\mvn.cmd`)
- Nereikalinga jokia duomenų bazė — testai naudoja H2 in-memory DB

### Testų paleidimas

**Visų testų paleidimas:**
```powershell
$env:JAVA_HOME = "C:\Programos\JDK-24\jdk-24"
cd "<projekto_katalogas>"
C:\Programos\apache-maven-3.9.5\bin\mvn.cmd test
```

**Konkretaus testo paleidimas:**
```powershell
C:\Programos\apache-maven-3.9.5\bin\mvn.cmd test -Dtest=KategorijaServiceTest
```

**Konkretaus metodo paleidimas:**
```powershell
C:\Programos\apache-maven-3.9.5\bin\mvn.cmd test -Dtest=KategorijaServiceTest#sukurtiKategorija_Sekmingai
```

**MySQL integracinių testų paleidimas:**
```powershell
$env:MYSQL_USERNAME = "root"
$env:MYSQL_PASSWORD = "jusu_slaptazodis"
$env:MYSQL_HOST = "localhost"
$env:MYSQL_PORT = "3306"
$env:MYSQL_DATABASE = "minima"
C:\Programos\apache-maven-3.9.5\bin\mvn.cmd test -Dspring.profiles.active=mysql -Dtest=*MySqlTest
```

### Padengimo ataskaita (JaCoCo)

Testai automatiškai generuoja HTML ataskaitą:
```
target/jacoco-report/index.html
```
Ataskaitą atidarykite naršyklėje, kad peržiūrėtumėte kodo padengimą pagal klases ir metodus.

---

## 3. Testavimo procesas

### Naudojami įrankiai

| Įrankis | Versija | Paskirtis |
|---------|---------|-----------|
| JUnit 5 | 5.9.3 | Testų karkasas |
| Mockito | 4.8.1 | Metodų imitavimas (mocking) |
| Spring MockMvc | 2.7.14 | HTTP užklausų testavimas |
| H2 | 2.1.214 | In-memory duomenų bazė testams |
| JaCoCo | 0.8.12 | Kodo padengimo ataskaita |

### AAA modelis (Arrange-Act-Assert)

Visi testai laikosi AAA struktūros:

```java
@Test
void sukurtiKategorija_Sekmingai() {
    // ARRANGE — paruošiama aplinka
    Kategorija kategorija = new Kategorija();
    kategorija.setPavadinimas("Elektronika");
    when(kategorijaRepository.findByPavadinimas("Elektronika")).thenReturn(Optional.empty());
    when(kategorijaRepository.save(any())).thenReturn(kategorija);

    // ACT — vykdomas testuojamas metodas
    Kategorija rezultatas = kategorijaService.create(kategorija);

    // ASSERT — tikrinamas rezultatas
    assertEquals("Elektronika", rezultatas.getPavadinimas());
    verify(kategorijaRepository).save(kategorija);
}
```

### Testų nepriklausomumas

- Kiekvienas testas naudoja šviežią `@Mock` būseną per `@ExtendWith(MockitoExtension.class)`
- Integraciniai testai naudoja `@WebMvcTest` — kraunamas tik kontrolerio sluoksnis
- Duomenų bazė nenaudojama vienetų testuose — vietoje jos naudojami Mockito imitavimai

---

## 4. Testavimo aprėptis

### Vienetų testai (Service sluoksnis)

Kiekvienos paslaugos klasės testai apima:

#### Kūrimas (Create)
- ✅ Sėkmingas sukūrimas — tikrinama, kad įrašas išsaugomas
- ✅ Dublikato prevencija — klaida, jei toks įrašas jau egzistuoja (pvz., tas pats pavadinimas, brūkšninis kodas, asmens kodas)
- ✅ Privalomos informacijos trūkumas — klaida, jei trūksta reikiamo lauko

#### Skaitymas (Read / Ataskaitos)
- ✅ Visų įrašų gavimas — grąžinamas pilnas sąrašas
- ✅ Paieška pagal ID — sėkminga ir nesėkminga (klaida, jei neegzistuoja)
- ✅ Paieška pagal pavadinimą / kodą — sėkminga ir nesėkminga
- ✅ Filtravimas pagal kategoriją, parduotuvę, miestą
- ✅ Filtravimas pagal žemą atsargų kiekį (`getLowStockItems`)

#### Redagavimas (Update)
- ✅ Sėkmingas atnaujinimas — tikrinama, kad pakeitimai išsaugomi
- ✅ Neegzistuojančio įrašo atnaujinimas — klaida (`ResourceNotFoundException`)
- ✅ Duplikato konfliktas atnaujinant — klaida, jei nauja reikšmė jau užimta

#### Trynimas (Delete)
- ✅ Sėkmingas ištrinimas — tikrinama, kad `deleteById` iškviečiamas
- ✅ Neegzistuojančio įrašo trynimas — klaida (`ResourceNotFoundException`)

### Integraciniai testai (Controller sluoksnis)

Kiekvieno kontrolerio testai tikrina HTTP atsakų kodus ir JSON struktūrą:

| HTTP metodas | Testuojamas scenarijus | Laukiamas atsakas |
|-------------|----------------------|-------------------|
| `GET /api/...` | Visų įrašų gavimas | `200 OK` + JSON masyvas |
| `GET /api/.../1` | Esamo įrašo paieška | `200 OK` + JSON objektas |
| `GET /api/.../99` | Neegzistuojančio paieška | `404 Not Found` |
| `GET /api/.../search?q=...` | Paieška pagal pavadinimą | `200 OK` + rezultatai |
| `POST /api/...` | Naujo įrašo kūrimas | `201 Created` |
| `POST /api/...` | Dublikato kūrimas | `400 Bad Request` |
| `POST /api/...` | Trūksta privalomo lauko | `400 Bad Request` |
| `PUT /api/.../1` | Esamo atnaujinimas | `200 OK` |
| `PUT /api/.../1` | Trūksta privalomo lauko | `400 Bad Request` |
| `PUT /api/.../99` | Neegzistuojančio atnaujinimas | `404 Not Found` |
| `DELETE /api/.../1` | Esamo ištrinimas | `204 No Content` |
| `DELETE /api/.../99` | Neegzistuojančio ištrinimas | `404 Not Found` |

---

## 5. Kodo padengimas (Coverage)

Paleidus `mvn test`, ataskaita automatiškai generuojama į:
```
target/jacoco-report/index.html
```

### Padengimo suvestinė (pagal sluoksnį)

| Sluoksnis | Klasės | Padengimas |
|-----------|--------|------------|
| `com.example.service` | KategorijaService, PrekeService, PareigosService, DarbuotojasService, ParduotuveService, InventoriusService, PardavimasService, PardavimoEiluteService | aukštas |
| `com.example.controller` | KategorijaController, PrekeController, DarbuotojasController, InventoriusController, PardavimasController, PardavimoEiluteController, PareigosController, ParduotuveController | aukštas |
| `com.example.model` | Kategorija, Preke, Darbuotojas, Pareigos, Parduotuve, Inventorius, Pardavimas, PardavimoEilute | vidutinis-aukštas |

> **Pastaba:** šiame etape padengti visi pagrindiniai `service` ir `controller` komponentai.

### MySQL integraciniai testai

Papildomai pridėti testai, kurie veikia su realia MySQL baze:
- [src/test/java/com/example/repository/KategorijaRepositoryMySqlTest.java](src/test/java/com/example/repository/KategorijaRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/InventoriusRepositoryMySqlTest.java](src/test/java/com/example/repository/InventoriusRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/PrekeRepositoryMySqlTest.java](src/test/java/com/example/repository/PrekeRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/PareigosRepositoryMySqlTest.java](src/test/java/com/example/repository/PareigosRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/ParduotuveRepositoryMySqlTest.java](src/test/java/com/example/repository/ParduotuveRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/PardavimoEiluteRepositoryMySqlTest.java](src/test/java/com/example/repository/PardavimoEiluteRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/PardavimasRepositoryMySqlTest.java](src/test/java/com/example/repository/PardavimasRepositoryMySqlTest.java)
- [src/test/java/com/example/repository/DarbuotojasRepositoryMySqlTest.java](src/test/java/com/example/repository/DarbuotojasRepositoryMySqlTest.java)

Jiems reikalinga paleista MySQL instancija ir teisingi `MYSQL_*` aplinkos kintamieji.

---

## 6. Testų struktūra

```
src/test/
├── java/com/example/
│   ├── service/
│   │   ├── KategorijaServiceTest.java       (12 testų)
│   │   ├── PrekeServiceTest.java            (17 testų)
│   │   ├── DarbuotojasServiceTest.java      (16 testų)
│   │   ├── PareigosServiceTest.java         (12 testų)
│   │   ├── ParduotuveServiceTest.java       (13 testų)
│   │   ├── InventoriusServiceTest.java      (15 testų)
│   │   ├── PardavimasServiceTest.java       (7 testų)
│   │   └── PardavimoEiluteServiceTest.java  (8 testų)
│   └── controller/
│       ├── KategorijaControllerTest.java    (13 testų)
│       ├── PrekeControllerTest.java         (16 testų)
│       ├── DarbuotojasControllerTest.java   (16 testų)
│       ├── InventoriusControllerTest.java   (8 testų)
│       ├── PardavimasControllerTest.java    (8 testų)
│       ├── PardavimoEiluteControllerTest.java (8 testų)
│       ├── PareigosControllerTest.java      (8 testų)
│       └── ParduotuveControllerTest.java    (8 testų)
│   └── repository/
│       ├── KategorijaRepositoryMySqlTest.java      (2 testų)
│       ├── InventoriusRepositoryMySqlTest.java     (2 testų)
│       ├── PrekeRepositoryMySqlTest.java           (2 testų)
│       ├── PareigosRepositoryMySqlTest.java        (2 testų)
│       ├── ParduotuveRepositoryMySqlTest.java      (2 testų)
│       ├── PardavimoEiluteRepositoryMySqlTest.java (2 testų)
│       ├── PardavimasRepositoryMySqlTest.java      (2 testų)
│       └── DarbuotojasRepositoryMySqlTest.java     (2 testų)
└── resources/
    └── application.properties               (H2 konfigūracija)
```

---

## 7. Dažniausios klaidos paleidžiant testus

| Problema | Sprendimas |
|---------|-----------|
| `mvn` neatpažįstamas | Naudokite pilną kelią: `C:\Programos\apache-maven-3.9.14\bin\mvn.cmd` |
| `JAVA_HOME` neteisingas | Nustatykite: `$env:JAVA_HOME = "C:\Programos\JDK-24\jdk-24"` |
| Testai nepavyksta dėl DB | Patikrinkite `src/test/resources/application.properties` — turi būti H2 konfigūracija |
| JaCoCo ataskaita negeneruojama | Normalus veikimas su JDK 24 — `IllegalClassFormatException` įspėjimai JDK vidinėms klasėms yra nekritiniai |
