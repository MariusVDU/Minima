# MINIMA - Projekto Paleidimo Instrukcijos

## Reikalavimai

1. **MySQL 8.0** - turėti įdiegtą ir paleistą MySQL serverį
2. **Docker Desktop** (rekomenduojama) ARBA Java 17 + Maven vietiniam paleidimui

## MySQL Nustatymas

### 1. Sukurti schemą "minima"

Prisijunkite prie MySQL ir sukurkite schemą:

```sql
CREATE DATABASE IF NOT EXISTS minima;
```

Arba per MySQL Workbench:
- Right click → Create Schema
- Schema Name: `minima`
- Apply

### 2. Patikrinti prisijungimo duomenis

Failas: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/minima
spring.datasource.username=root
spring.datasource.password=root
```

**Svarbu:** Jei jūsų MySQL vartotojas ar slaptažodis kitokie, pakeiskite juos `application.properties` faile.

## Paleidimas

### Per Docker (rekomenduojama)

```bash
# Sukurti MySQL schemą
mysql -u root -p
CREATE DATABASE minima;
EXIT;

# Paleisti aplikaciją
docker-compose up --build
```

Sistema bus pasiekiama: http://localhost:8080

### Per Maven (vietinis paleidimas)

```bash
mvn clean package
mvn spring-boot:run
```

### Per IDE (IntelliJ IDEA, Eclipse, VS Code)

1. Atidarykite projektą
2. Leiskite Maven parsisiųsti priklausomybes
3. Paleiskite `Main.java` klasę

## Ką programa daro automatiškai

1. ✅ **Prisijungia** prie MySQL serverio
2. ✅ **Patikrina** duomenų bazės būseną (DatabaseInitializer)
3. ✅ **Sukuria trūkstamas lenteles** automatiškai (Hibernate ddl-auto=update)
4. ✅ **Išsaugo esamus duomenis** - neprarandami įrašai
5. ✅ **Parodo** visų lentelių struktūrą startup metu
6. ✅ **Paleidžia** REST API serverį ant 8080 porto

## REST API Endpointai

Visi endpointai palaiko CRUD operacijas:

- `/api/parduotuves` - Parduotuvės
- `/api/pareigos` - Pareigos  
- `/api/darbuotojai` - Darbuotojai
- `/api/kategorijos` - Prekių kategorijos
- `/api/prekes` - Prekės
- `/api/inventorius` - Inventorius
- `/api/pardavimai` - Pardavimai
- `/api/pardavimo-eilutes` - Pardavimo eilutės

### Pavyzdžiai:

```bash
# Gauti visas parduotuves
GET http://localhost:8080/api/parduotuves

# Gauti parduotuvę pagal ID
GET http://localhost:8080/api/parduotuves/{id}

# Sukurti naują parduotuvę
POST http://localhost:8080/api/parduotuves
Content-Type: application/json

{
  "miestas": "Vilnius",
  "gatve": "Gedimino pr. 1",
  "telefonas": "+37060000000",
  "el_pastas": "vilnius@parduotuve.lt"
}
```

## Darbas iš kelių kompiuterių

Programa automatiškai prisitaiko prie esamos duomenų bazės būsenos:

- **Pirmą kartą paleidus**: sukuria lenteles
- **Vėliau paleidus**: naudoja esamas lenteles ir duomenis
- **Skirtinguose kompiuteriuose**: tik reikia turėti MySQL su schema "minima"

## Pagrindinės Lentelės

Sistema automatu sukuria 8 lenteles:

1. **parduotuves** - Parduotuvių tinklo informacija
2. **pareigos** - Darbuotojų pareigų klasifikatorius  
3. **darbuotojai** - Darbuotojų registras
4. **kategorijos** - Prekių kategorijos
5. **prekes** - Prekių katalogas
6. **inventorius** - Prekių likučiai parduotuvėse
7. **pardavimai** - Pardavimų antraštės
8. **pardavimo_eilutes** - Pardavimų pozicijos

## Troubleshooting

### Klaida: "Communications link failure"
- Patikrinkite ar MySQL serveris paleistas
- Patikrinkite ar port 3306 prieinamas

### Klaida: "Access denied for user"
- Patikrinkite `application.properties` vartotoją ir slaptažodį

### Klaida: "Unknown database 'minima'"
- Sukurkite schemą: `CREATE DATABASE minima;`
- Arba pridėkite `createDatabaseIfNotExist=true` prie URL

### Lentelės nesukuriamos
- Patikrinkite ar `spring.jpa.hibernate.ddl-auto=update` yra application.properties
- Žiūrėkite logus - Hibernate rodo SQL komandas
