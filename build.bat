@echo off
chcp 65001 >nul
title MINIMA - Build ir paleidimas

echo ========================================
echo   MINIMA Aplikacijos build procesas
echo ========================================
echo.

echo [1/3] Stabdome seną konteinerį...
docker compose down
if %ERRORLEVEL% NEQ 0 (
    echo Klaida stabdant konteinerį
    pause
    exit /b 1
)

echo.
echo [2/3] Kuriame naują Docker image...
docker compose build
if %ERRORLEVEL% NEQ 0 (
    echo Klaida kuriant Docker image
    pause
    exit /b 1
)

echo.
echo [3/3] Paleidžiame aplikaciją...
docker compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo Klaida paleidžiant aplikaciją
    pause
    exit /b 1
)

echo.
echo ========================================
echo   ✓ Build sėkmingas!
echo ========================================
echo.
echo Aplikacija pasiekiama: http://localhost:8080
echo.
echo Norėdami sustabdyti: docker compose down
echo Norėdami matyti logus: docker compose logs -f
echo.
pause
