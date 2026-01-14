@echo off
chcp 65001 >nul
title MINIMA - Logai

echo ========================================
echo   MINIMA Aplikacijos logai
echo ========================================
echo.
echo Rodomi realaus laiko logai...
echo Spauskite Ctrl+C norėdami sustabdyti
echo.

docker compose logs -f
