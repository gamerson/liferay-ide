@echo off
::add all variable
set user.home.dir=C:\Users\liferay
set liferay.workspace.home.dir="C:\Program Files (x86)\LiferayDeveloperStudio\liferay-workspace"
set liferay.developer.studio.home.dir="C:\Program Files (x86)\LiferayDeveloperStudio\liferay-developer-studio"
set LiferayDeveloperStudio.dir="C:\Program Files (x86)\LiferayDeveloperStudio"

::delete all cache
if exist %user.home.dir%\.liferay\bundles del %user.home.dir%\.liferay\bundles\*.*

if exist %user.home.dir%\.liferay\token del /p %user.home.dir%\.liferay\token

if exist %liferay.workspace.home.dir% rd /s %liferay.workspace.home.dir%

if exist %liferay.developer.studio.home.dir% rd /s %liferay.developer.studio.home.dir%

if exist %LiferayDeveloperStudio.dir% rd /s %LiferayDeveloperStudio.dir%

if exist %user.home.dir%\.jpm rd /s %user.home.dir%\.jpm

if not exist %user.home.dir%\.jpm\windows\bin\blade.exe echo The blade has been removed.

pause