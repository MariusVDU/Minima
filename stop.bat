@echo off
chcp 65001 >nul
title MINIMA - Sustabdymas

echo ========================================
echo   MINIMA Aplikacijos sustabdymas
echo ========================================
echo.

echo Stabdome konteinerius...
docker compose down
if %ERRORLEVEL% NEQ 0 (
    echo Klaida stabdant konteinerius
    pause
    exit /b 1
)

echo.
echo ========================================
echo   ✓ Aplikacija sustabdyta!
echo ========================================
echo.
pause
