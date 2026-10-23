@echo off
cd /d "%~dp0"
call npm run build
echo.
echo Build completed. Press any key to exit...
pause > nul
