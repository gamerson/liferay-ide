@echo off
::add all variable
set user.home.dir=C:\Users\liferay

::delete all cache
if exist %user.home.dir%\.liferay\token del /p %user.home.dir%\.liferay\token

if exist %user.home.dir%\liferay-workspace rd /s  %user.home.dir%\liferay-workspace

if exist %user.home.dir%\.jpm rd /s %user.home.dir%\.jpm

if not exist %user.home.dir%\.jpm\windows\bin\blade.exe echo The blade has been removed.

pause