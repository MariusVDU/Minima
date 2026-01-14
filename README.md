# MINIMA - Prekybos Tinklo Valdymo Sistema

Sistema prekybos tinklo valdymui su inventoriumi, pardavimais ir darbuotojų administravimu.

## � Turinys

1. [Greitasis pradžia](#greitasis-pradžia)
2. [Reikalavimai](#reikalavimai)
3. [Technologijos](#technologijos)
4. [Diegimas](#diegimas)
5. [REST API](#rest-api)
6. [Problemų sprendimas](#problemų-sprendimas)
7. [Funkcionalumas](#funkcionalumas)
8. [Darbas iš kodo](#darbas-iš-kodo)

---

## 🚀 Greitasis pradžia

**Jei turite MySQL ir Docker:**

```bash
# 1. Klonuoti
git clone https://github.com/your-username/minima.git
cd minima

# 2. Nustatyti slaptažodį
cp .env.example .env
# Redaguoti .env ir pakeisti MYSQL_PASSWORD

# 3. Sukurti duomenų bazę
mysql -u root -p
CREATE DATABASE minima;
EXIT;

# 4. Paleisti
docker-compose up --build
```

**Sistema pasiekiama:** http://localhost:8080

---

## 📦 Reikalavimai

### Docker paleidimas (REKOMENDUOJAMA)
- **MySQL 8.0** - įdiegtas ir paleistas
- **Docker Desktop** (Windows/Mac) arba **Docker + Docker Compose** (Linux)
- **Git** - kodo versijų valdymui

**Parsisiųsti:**
- [MySQL](https://dev.mysql.com/downloads/mysql/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Git](https://git-scm.com/)

### Vietinis paleidimas (bez Docker)
- **Java 17** arba naujesnė
- **Maven 3.8+**
- **MySQL 8.0** - lokalus
- **IDE** (IntelliJ, Eclipse, VS Code)

---

## 💻 Technologijos

- **Backend**: Java 17, Spring Boot 2.7.14, Spring Data JPA
- **ORM**: Hibernate 5.6.15
- **Database**: MySQL 8.0
- **Frontend**: Vanilla JavaScript, Bootstrap 5
- **Konteinerizacija**: Docker, Docker Compose
- **Build**: Maven 3.8
- **Versijų valdymas**: Git

---

## 🔧 Diegimas

### Variantas 1: Docker (REKOMENDUOJAMA)

**Žingsnis 1 - Paruoškite MySQL**

```bash
mysql -u root -p
CREATE DATABASE IF NOT EXISTS minima;
EXIT;
```

**Žingsnis 2 - Konfigūracija**

```bash
# Kopijuoti šablonui
cp .env.example .env

# Redaguoti .env failą
# Pakeisti: MYSQL_PASSWORD=jūsų_slaptažodis
```

**Žingsnis 3 - Paleisti**

```bash
docker-compose up --build
```

**Žingsnis 4 - Paskutiniai žingsniai**
- Laukite, kol Maven parsisiunčia priklausomybes (~5 min pirmo karto)
- Atidarykite naršyklėje: http://localhost:8080

**Sustabdymas:**
```bash
docker-compose down
```

---

### Variantas 2: Maven (Vietinis)

**Žingsnis 1 - Paruoškite MySQL**

```bash
mysql -u root -p
CREATE DATABASE IF NOT EXISTS minima;
EXIT;
```

**Žingsnis 2 - Diegimas ir paleidimas**

```bash
mvn clean package
mvn spring-boot:run
```

**Žingsnis 3 - Atidarykite**
http://localhost:8080

---

### Variantas 3: IDE (IntelliJ, Eclipse, VS Code)

1. Atidarykite projektą IDE'je
2. Leiskite Maven parsisiųsti priklausomybes
3. Redaguokite `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=jūsų_slaptažodis
   ```
4. Paleiskite `src/main/java/com/example/Main.java`
5. Atidarykite http://localhost:8080

---

## 🌐 REST API

### Endpointai

Sistema palaiko CRUD operacijas šiems resursams:

| Resursas | Endpoint | Aprašymas |
|----------|----------|-----------|
| Parduotuvės | `/api/parduotuves` | Parduotuvių tinklo informacija |
| Pareigos | `/api/pareigos` | Darbuotojų pareigų klasifikatorius |
| Darbuotojai | `/api/darbuotojai` | Darbuotojų registras |
| Kategorijos | `/api/kategorijos` | Prekių kategorijos |
| Prekės | `/api/prekes` | Prekių katalogas |
| Inventorius | `/api/inventorius` | Prekių likučiai |
| Pardavimai | `/api/pardavimai` | Pardavimų antraštės |
| Pardavimo eilutės | `/api/pardavimo-eilutes` | Pardavimų pozicijos |

### API Pavyzdžiai

**Gauti visus resursus:**
```bash
GET http://localhost:8080/api/parduotuves
```

**Gauti resursą pagal ID:**
```bash
GET http://localhost:8080/api/parduotuves/1
```

**Sukurti naują resursą:**
```bash
POST http://localhost:8080/api/parduotuves
Content-Type: application/json

{
  "miestas": "Vilnius",
  "gatve": "Gedimino pr. 1",
  "telefonas": "+37060000000",
  "el_pastas": "vilnius@parduotuve.lt"
}
```

**Atnaujinti resursą:**
```bash
PUT http://localhost:8080/api/parduotuves/1
Content-Type: application/json

{
  "miestas": "Kaunas",
  "gatve": "Laisvės al. 100",
  "telefonas": "+37061111111",
  "el_pastas": "kaunas@parduotuve.lt"
}
```

**Ištrinti resursą:**
```bash
DELETE http://localhost:8080/api/parduotuves/1
```

---

## 🐛 Problemų sprendimas

### "Communications link failure" arba "Connection refused"

**Problema:** MySQL nepasiekiamas

**Sprendimai:**

1. **Patikrinkite, ar MySQL paleistas**
   - Windows: Services → MySQL80 → Start
   - Mac: System Preferences → MySQL → Start
   - Linux: `sudo systemctl start mysql`

2. **Patikrinkite portą 3306**
   ```bash
   mysql -u root -p
   ```
   Jei prisijungiama - MySQL veikia.

3. **Patikrinkite slaptažodį**
   ```bash
   # .env failas
   MYSQL_PASSWORD=teisingas_slaptazodis
   ```

### "Access denied for user 'root'"

**Problema:** Neteisingas MySQL slaptažodis

**Sprendimas:**
```bash
# Patikrinkite .env failą
cat .env

# Arba per terminal
$env:MYSQL_PASSWORD="teisingas_slaptazodis"
docker-compose up --build
```

### "Unknown database 'minima'"

**Problema:** Schema nesukurta

**Sprendimas:**
```bash
mysql -u root -p
CREATE DATABASE minima;
EXIT;
```

### "Port 8080 is already in use"

**Problema:** Kitas procesas naudoja 8080 portą

**Sprendimas:**

**Windows:**
```powershell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Linux/Mac:**
```bash
lsof -i :8080
kill -9 <PID>
```

Arba pakeiskite portą `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Pasiekiama: http://localhost:8081
```

### "Cannot connect to Docker daemon"

**Problema:** Docker Desktop nepaleistas

**Sprendimas:** Paleiskite Docker Desktop ir pabandykite dar kartą.

### Lentelės nesukuriamos

**Problema:** Hibernate nesukuria lentelių

**Sprendimas:**
Patikrinkite `src/main/resources/application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Jei šios eilutės nėra, pridėkite ją.

---

## ✅ Funkcionalumas

- ✅ **Prekių katalogas** su kategorijomis
- ✅ **Inventoriaus valdymas** su žemų atsargų įspėjimais
- ✅ **Pardavimų sistema** su krepšeliu
- ✅ **Parduotuvių tinklo** administravimas
- ✅ **Darbuotojų ir pareigų** valdymas
- ✅ **REST API** visoms operacijoms
- ✅ **Automatinis schemos** kūrimas (Hibernate)
- ✅ **Duomenų saugumo** išsaugojimas

---

## 🛠️ Darbas iš kodo

### Projekto struktūra

```
src/main/
├── java/com/example/
│   ├── Main.java                    # Spring Boot aplikacijos entry point
│   ├── controller/                  # REST API kontroleriai
│   │   ├── ParduotuveController.java
│   │   ├── DarbuotojasController.java
│   │   ├── PrekeController.java
│   │   └── ...
│   ├── model/                       # JPA Entity klasės
│   │   ├── Parduotuve.java
│   │   ├── Darbuotojas.java
│   │   ├── Preke.java
│   │   └── ...
│   ├── repository/                  # Spring Data Repositories
│   │   ├── ParduotuveRepository.java
│   │   ├── DarbuotojasRepository.java
│   │   └── ...
│   ├── service/                     # Verslo logika
│   │   └── (tuščias - galite pridėti services)
│   └── config/                      # Konfigūracija
│       └── DatabaseInitializer.java # Startup diagnostika
└── resources/
    ├── application.properties        # Spring Boot konfigūracija
    └── static/                       # Frontend failai
        ├── index.html
        ├── app.js
        └── styles.css
```

### Naujos kontrolerio pridėjimas

**1. Sukurti Entity klasę** (`src/main/java/com/example/model/ManoEntity.java`):
```java
@Entity
@Table(name = "mano_entitai")
public class ManoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String pavadinimas;
    // getters, setters...
}
```

**2. Sukurti Repository** (`src/main/java/com/example/repository/ManoEntityRepository.java`):
```java
@Repository
public interface ManoEntityRepository extends JpaRepository<ManoEntity, Long> {
}
```

**3. Sukurti Controller** (`src/main/java/com/example/controller/ManoEntityController.java`):
```java
@RestController
@RequestMapping("/api/mano-entity")
public class ManoEntityController {
    @Autowired
    private ManoEntityRepository repository;
    
    @GetMapping
    public List<ManoEntity> getAll() {
        return repository.findAll();
    }
    
    @PostMapping
    public ManoEntity create(@RequestBody ManoEntity entity) {
        return repository.save(entity);
    }
    // ... daugiau metodų
}
```

**4. Perkompiliuokite:**
```bash
docker-compose up --build
```

Hibernate automatiškai sukurs lentelę! 🎉

### Hibernate konfigūracija

Failas: `src/main/resources/application.properties`

```properties
# Automatinis schemos valdymas
spring.jpa.hibernate.ddl-auto=update  # update=sukuria/atnaujina, validate=tik tikrina

# SQL debug
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# MySQL dialektas
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

---

## 🔐 Saugumas

### Slaptažodžiai

Slaptažodžiai saugomi `.env` faile, kuris yra `.gitignore`:
- ✅ **GitHub'e nematyti** slaptažodžiai
- ✅ **Kiekvienoje mašinoje** galima turėti skirtingus slaptažodžius
- ✅ **Automatiškai** įkeliami iš `.env`

### Nustatymai production'i

Production'i keiskite:
```properties
# Neleidžiama keisti schemą
spring.jpa.hibernate.ddl-auto=validate

# Neslepiamos SQL komandos
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

---

## 📝 Git workflow

### Pirmas commit

```bash
git init
git add .
git commit -m "Initial commit: MINIMA system"
git remote add origin https://github.com/your-username/minima.git
git branch -M main
git push -u origin main
```

### Nauji commitai

```bash
git add .
git commit -m "Aprašymas ką keičiau"
git push origin main
```

### Klonuoti į kitą kompiuterį

```bash
git clone https://github.com/your-username/minima.git
cd minima
cp .env.example .env
# Redaguoti .env su savo duomenimis
docker-compose up --build
```

---

## 📞 Pagalba

Jei kažkas neveikia:
1. Patikrinkite [Problemų sprendimas](#problemų-sprendimas) skyrių
2. Žiūrėkite Docker logus: `docker-compose logs -f`
3. Patikrinkite `.env` failą - ar teisingas slaptažodis?
4. Patikrinkite, ar MySQL paleistas

---

## 📄 Licencija

MIT License - naudokite laisvai!

---

**Paskutinis update:** 2026-01-14  
**Versija:** 1.0-SNAPSHOT  
**Autoriai:** MINIMA Development Team