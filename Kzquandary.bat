@echo off
set jar=%CD%

REM Start XAMPP Apache dan MySQL
start /B /D "C:\xampp" apache\bin\httpd.exe
start /B /D "C:\xampp" mysql\bin\mysqld.exe

REM Tunggu beberapa detik untuk memastikan Apache dan MySQL telah dimulai
timeout /t 5 /nobreak >nul

REM Navigasi ke direktori proyek Laravel
cd C:\xampp\htdocs\KzquandaryLAB

REM Jalankan perintah untuk menjalankan server Laravel
start /B php artisan serve

REM Tunggu beberapa detik untuk memastikan server Laravel telah dimulai
timeout /t 5 /nobreak >nul

cd %jar%

REM Jalankan JAR Java
java --module-path "C:\Program Files\Java\javafx-sdk-19.0.2.1\lib" --add-modules javafx.media,javafx.controls,javafx.fxml -jar out/artifacts/AslabApp_jar/AslabApp.jar

REM Hentikan XAMPP Apache
taskkill /F /IM httpd.exe

REM Hentikan XAMPP MySQL
taskkill /F /IM mysqld.exe

REM Tunggu beberapa detik untuk memastikan proses telah berhenti
timeout /t 1 /nobreak >nul