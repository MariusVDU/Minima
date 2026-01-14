@echo off
chcp 65001 >nul
title MINIMA - Build, Paleidimas ir Logai

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   MINIMA - Kompletinis Paleidimas
echo ========================================
echo.

REM Patikrinti ar Docker paleistas
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ✗ KLAIDA: Docker Desktop nėra paleistas!
    echo.
    echo Paleiskite Docker Desktop ir bandykite dar kartą.
    echo.
    pause
    exit /b 1
)

REM Patikrinti ar .env failas egzistuoja
if not exist .env (
    echo ✗ KLAIDA: .env failas neegzistuoja!
    echo.
    echo Sukurkite jį iš šablono:
    echo   copy .env.example .env
    echo Tada redaguokite .env ir nustatykite MySQL slaptažodį.
    echo.
    pause
    exit /b 1
)

REM ŽINGSNIS 1: Sustabdyti seną konteinerį
echo [1/4] Stabdome seną konteinerį (jei yra paleistas)...
docker compose down >nul 2>&1
echo ✓ Sustabdyta
echo.

REM ŽINGSNIS 2: Kompiliuoti Docker image
echo [2/4] Kuriame Docker image ir kompiliuojame projektą...
docker compose build
if %ERRORLEVEL% NEQ 0 (
    echo ✗ Klaida kuriant Docker image
    echo.
    pause
    exit /b 1
)
echo ✓ Build sėkmingas
echo.

REM ŽINGSNIS 3: Paleisti konteinerį
echo [3/4] Paleidžiame aplikaciją...
docker compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo ✗ Klaida paleidžiant aplikaciją
    echo.
    pause
    exit /b 1
)
echo ✓ Aplikacija paleista
echo.

REM Palaukti, kol aplikacija pasiruošia
echo [4/4] Laukiame, kol aplikacija pasiruošta (15 sekundžių)...
timeout /t 15 /nobreak

echo.
echo ========================================
echo   ✓ MINIMA PALEISTA SĖKMINGAI!
echo ========================================
echo.
echo Aplikacija pasiekiama:
echo   http://localhost:8080
echo.
echo Visi 8 API endpointai pasiekiami:
echo   /api/parduotuves
echo   /api/pareigos
echo   /api/darbuotojai
echo   /api/prekes
echo   /api/kategorijos
echo   /api/inventorius
echo   /api/pardavimai
echo   /api/pardavimo-eilutes
echo.
echo ========================================
echo   LOGŲ STEBĖJIMAS
echo ========================================
echo.
echo Rodomi realaus laiko logai...
echo Spauskite Ctrl+C sustabdymui (bus paklaustas patvirtinimas)
echo.

REM PAGRINDINĖ DALIS: Stebėti logus ir laukti Ctrl+C
docker compose logs -f

REM Kai vartotojas spaustute Ctrl+C
echo.
echo ========================================
echo   Sustabdymo patvirtinimas
echo ========================================
echo.
set /p choice="Ar tikrai sustabdyti aplikaciją? (Y/N): "
if /i "!choice!"=="Y" (
    echo.
    echo Stabdome konteinerius...
    docker compose down
    echo.
    echo ✓ Aplikacija sustabdyta!
    echo.
) else (
    echo.
    echo Grįžtama prie logų stebėjimo...
    docker compose logs -f
    goto :restart_choice
)

pause
