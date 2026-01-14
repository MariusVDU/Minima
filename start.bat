@echo off
chcp 65001 >nul
title MINIMA - Paleidimas

echo ========================================
echo   MINIMA Aplikacijos paleidimas
echo ========================================
echo.

echo Paleidžiame aplikaciją...
docker compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo Klaida paleidžiant aplikaciją
    echo.
    echo Galbūt reikia pirmiausia paleisti build.bat?
    pause
    exit /b 1
)

echo.
echo ========================================
echo   ✓ Aplikacija paleista!
echo ========================================
echo.
echo Aplikacija pasiekiama: http://localhost:8080
echo.
echo Norėdami sustabdyti: paleiskite stop.bat
echo Norėdami matyti logus: paleiskite logs.bat
echo.
pause
