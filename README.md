# MINIMA - Prekybos Tinklo Valdymo Sistema

Sistema prekybos tinklo valdymui su inventoriumi, pardavimais ir darbuotojų administravimu.

## 🚀 Paleidimas Bet Kuriame Kompiuteryje

**Reikia:**
- MySQL 8.0 (lokalus)
- Docker Desktop

```bash
# 1. Klonuoti projektą
git clone https://github.com/your-username/minima.git
cd minima

# 2. Sukonfigūruoti MySQL slaptažodį (jei nereikia)
cp .env.example .env
# Atidaryti .env ir pakeisti MYSQL_PASSWORD savo slaptažodžiu

# 3. Sukurti MySQL schemą
mysql -u root -p
CREATE DATABASE minima;

# 4. Paleisti aplikaciją
docker-compose up --build
```

Sistema bus pasiekiama: [http://localhost:8080](http://localhost:8080)

📖 Detalios instrukcijos: [QUICK_START.md](QUICK_START.md)

## Technologijos

- **Backend**: Java 17, Spring Boot 2.7, Spring Data JPA, Hibernate
- **Database**: MySQL 8.0
- **Frontend**: Vanilla JavaScript, Bootstrap 5
- **Deployment**: Docker, Docker Compose

## REST API Endpointai

Sistema palaiko CRUD operacijas šiems objektams:

- `/api/prekes` - Prekės
- `/api/kategorijos` - Kategorijos
- `/api/inventorius` - Inventorius
- `/api/pardavimai` - Pardavimai
- `/api/pardavimo-eilutes` - Pardavimo eilutės
- `/api/parduotuves` - Parduotuvės
- `/api/darbuotojai` - Darbuotojai
- `/api/pareigos` - Pareigos

## Funkcionalumas

✅ Prekių katalogas su kategorijomis  
✅ Inventoriaus valdymas su žemų atsargų įspėjimais  
✅ Pardavimų sistema su krepšeliu  
✅ Parduotuvių tinklo administravimas  
✅ Darbuotojų ir pareigų valdymas  
✅ Dashboard su statistika  

## Struktūra

```
src/main/
├── java/com/example/
│   ├── controller/     # REST API kontroleriai
│   ├── model/         # JPA entity klasės
│   ├── repository/    # Spring Data repositories
│   └── service/       # Verslo logika
└── resources/
    ├── static/        # Frontend failai
    └── application.properties
```

## Licencija

MIT License