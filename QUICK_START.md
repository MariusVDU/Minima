# Greitas Paleidimas Kitame Kompiuteryje

## Reikalavimai

1. **MySQL 8.0** - turi būti įdiegtas ir paleistas
2. **Docker Desktop** (Windows/Mac) arba **Docker + Docker Compose** (Linux)

Parsisiųsti:
- MySQL: https://dev.mysql.com/downloads/mysql/
- Docker: https://www.docker.com/products/docker-desktop

## Žingsnis po žingsnio

### 1. Paruoškite MySQL

```bash
# Prisijunkite prie MySQL
mysql -u root -p

# Sukurkite schemą
CREATE DATABASE IF NOT EXISTS minima;
EXIT;
```

### 2. Parsisiųskite projektą

```bash
git clone <repository-url>
cd java-mysql-docker-app
```

### 3. (Nebūtina) Nustatykite MySQL slaptažodį

Jei jūsų MySQL root slaptažodis nėra "root":

```bash
# Windows PowerShell
$env:MYSQL_PASSWORD="jusu_slaptazodis"

# Linux/Mac
export MYSQL_PASSWORD="jusu_slaptazodis"
```

Arba pakeiskite `docker-compose.yml` faile `SPRING_DATASOURCE_PASSWORD`.

### 4. Paleiskite viena komanda

```bash
docker-compose up --build
```

**Viskas!** Sistema automatiškai:
- ✅ Parsisiųs visas priklausomybes
- ✅ Sukompiliuos Java kodą
- ✅ Prisijungs prie jūsų lokalaus MySQL
- ✅ Hibernate sukurs lenteles automatiškai
- ✅ Paleis serverį

### 5. Atidarykite naršyklėje

```
http://localhost:8080
```

## Sustabdymas

```bash
docker-compose down
```

## Problemų sprendimas

### "Communications link failure" arba "Connection refused"
MySQL nepasiekiamas. Patikrinkite:

1. **Ar MySQL serveris paleistas?**
   - Windows: Services → MySQL80 → Start
   - Mac: System Preferences → MySQL → Start
   - Linux: `sudo systemctl start mysql`

2. **Ar MySQL veikia ant porto 3306?**
   ```bash
   mysql -u root -p
   ```
   Jei prisijungiate sėkmingai - MySQL veikia.

3. **Ar slaptažodis teisingas?**
   ```bash
   # Nustatykite aplinkos kintamąjį
   export MYSQL_PASSWORD="jusu_slaptazodis"
   docker-compose up --build
   ```

### "Access denied for user 'root'"
Neteisingas MySQL slaptažodis:

```bash
# Windows PowerShell
$env:MYSQL_PASSWORD="teisingas_slaptazodis"
docker-compose up --build

# Linux/Mac
export MYSQL_PASSWORD="teisingas_slaptazodis"
docker-compose up --build
```

### "Unknown database 'minima'"
Nesurkurta schema:

```sql
mysql -u root -p
CREATE DATABASE minima;
EXIT;
```

### "Port 8080 is already in use"
Kitas procesas naudoja 8080 portą:

1. Sustabdyti kitą programą ant 8080 porto
2. Arba pakeisti portą `docker-compose.yml`: `"8081:8080"`

### "Cannot connect to Docker daemon"
Docker Desktop nepaleistas - paleiskite jį.

### Lėtas pirmasis paleidimas
Normalus dalykas - Docker parsisiunčia Maven, Java images. Vėliau bus greičiau (~30 sek).

## Kūrėjams - Paleidimas be Docker

Jei norite dirbti tiesiogiai su Java:

```bash
# Įsitikinkite kad MySQL veikia ir schema 'minima' sukurta
mvn clean package
mvn spring-boot:run
```

Arba per IDE (IntelliJ, Eclipse, VS Code) paleiskite `Main.java`.

## Kas Vyksta Po Gaubtu

1. **Docker sukuria Java konteinerį**
   - Naudoja Maven image kompiliacijai
   - Sukuria executable JAR failą
   
2. **Aplikacija jungiasi prie jūsų lokalaus MySQL**
   - Per `host.docker.internal:3306`
   - Automatiškai sukuria schemą jei jos nėra
   
3. **Hibernate automatiškai sukuria lenteles**
   - Nuskaito JPA entity klases
   - Sukuria trūkstamas lenteles
   - Išsaugo egzistuojančius duomenis
   
4. **Spring Boot paleidžia serverį**
   - REST API: http://localhost:8080/api/
   - Frontend: http://localhost:8080/

## Patarimai

- ✅ Jūsų lokalus MySQL lieka nepalietas
- ✅ Duomenys išsaugomi MySQL (ne Docker volume)
- ✅ Galite dirbti su duomenimis per MySQL Workbench
- ✅ Pirmas paleidimas: ~5-10 min
- ✅ Vėlesni paleidimai: ~30 sek
- ✅ Kodo pakeitimai: reikia perkompiliuoti (`docker-compose up --build`)
