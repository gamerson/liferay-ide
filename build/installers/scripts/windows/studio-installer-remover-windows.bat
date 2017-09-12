@echo off
:: include properties file
for /f "delims=" %%a in (../config/env-win.properties) do %%~a

::delete all cache
if exist C:\Users\%username%\.liferay\bundles del %user.home.dir%\.liferay\bundles\*.*

if exist C:\Users\%username%\.liferay\token del /p %user.home.dir%\.liferay\token

if exist %liferay.workspace.home.dir% rd /s %liferay.workspace.home.dir%

if exist %liferay.developer.studio.home.dir% rd /s %liferay.developer.studio.home.dir%

if exist %LiferayDeveloperStudio.dir% rd /s %LiferayDeveloperStudio.dir%

if exist C:\Users\%username%\.jpm rd /s %user.home.dir%\.jpm

if not exist C:\Users\%username%\.jpm\windows\bin\blade.exe echo The blade has been removed.

pause