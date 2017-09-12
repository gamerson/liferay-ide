@echo off
::delete all cache

if exist C:\Users\%username%\.liferay\token del /p %user.home.dir%\.liferay\token

if exist C:\Users\%username%\liferay-workspace rd /s  %user.home.dir%\liferay-workspace

if exist C:\Users\%username%\.jpm rd /s %user.home.dir%\.jpm

if not exist C:\Users\%username%\.jpm\windows\bin\blade.exe echo The blade has been removed.

pause