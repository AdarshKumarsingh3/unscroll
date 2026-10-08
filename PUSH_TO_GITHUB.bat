@echo off
title Push Android App to GitHub
cls
echo ============================================================
echo   UNSCROLL: Pushing Android App with 'app' folder to GitHub
echo ============================================================
echo.
cd /d "c:\Users\adars\Downloads\unscroll-repo"
echo Running git push to your repository (adarshkumarsingh3/unscroll)...
echo If a GitHub sign-in window appears in your browser, please approve it!
echo.
git -c http.sslBackend=openssl push -u origin main
if %errorlevel% equ 0 (
    echo.
    echo ============================================================
    echo   SUCCESS! All files and the 'app' folder are pushed!
    echo.
    echo   Now go to:
    echo   https://github.com/adarshkumarsingh3/unscroll/actions
    echo.
    echo   GitHub is building your APK right now!
    echo   Once done, download 'unscroll-debug-apk' under Artifacts.
    echo ============================================================
) else (
    echo.
    echo ------------------------------------------------------------
    echo Retrying with default SSL backend...
    echo ------------------------------------------------------------
    git push -u origin main
)
echo.
pause