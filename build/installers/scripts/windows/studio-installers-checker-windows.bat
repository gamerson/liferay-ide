@echo off
::add all variable
set user.home.dir=C:\Users\liferay
set liferay.workspace.home.dir="C:\Program Files (x86)\LiferayDeveloperStudio\liferay-workspace"
set liferay.developer.studio.home.dir="C:\Program Files (x86)\LiferayDeveloperStudio\liferay-developer-studio"
set start.menu.program.dir="C:\ProgramData\Microsoft\Windows\Start Menu\Programs"
set sdk.bundle.zip=com.liferay.portal.plugins.sdk-1.0.11-withdependencies.zip
set portal.bundle.zip=liferay-dxp-digital-enterprise-tomcat-7.0-sp4-20170705142422877.zip
set expect.blade.version=2.2.0.201707061805

::check ${system_temp_directory} does not exist
if not exist %user.home.dir%\AppData\Local\Temp\LiferayWorkspace (echo The LiferayWorkspace folder doesn't exist.^(Successfully^)) else echo The LiferayWorkspace folder exists.(Error)

::check pwd file does not exist
if not exist %user.home.dir%\AppData\Local\Temp\LiferayWorkspace\pwd (echo The pwd file doesn't exist.^(Successfully^)) else echo The pwd file exists.(Error)

::check jpm is installed, jpm/bin exists, .jpm exists
if exist %user.home.dir%\.jpm (echo The jpm folder exists.^(Successfully^)) else echo The jpm folder doesn't exist.(Error)

if exist %user.home.dir%\.jpm\windows\bin (echo The jpm bin folder exists.^(Successfully^)) else echo The jpm bin folder doesn't exist.(Error)

::check blade is installed, jpm/blade exists
if exist %user.home.dir%\.jpm\windows\bin\blade.exe (echo The blade file exists.^(Successfully^)) else echo The blade file doesn't exist.(Error)

for /f %%b in ('blade version')do set bladeVersion=%%b 
if %bladeVersion%==%expect.blade.version% (echo Blade %bladeVersion% is installed correctly.^(Successfully^)) else echo Blade version is not %expect.blade.version%.(Error)

::check token file generated
if exist %user.home.dir%\.liferay\token (echo The token file exists.^(Successfully^)) else echo The token file doesn't exist.(Error)

::check liferay workspace home dir
if exist %liferay.workspace.home.dir% (echo The liferay-workspace exists.^(Successfully^)) else echo The liferay-workspace doesn't exist.(Error)

::check Studio home dir
if exist %liferay.developer.studio.home.dir% (echo The liferay-developer-studio exists.^(Successfully^)) else echo The iferay-developer-studio doesn't exist.(Error)

::check bundle.url in gradle.properties
if exist %liferay.workspace.home.dir%\gradle.properties findstr "%portal.bundle.zip%" %liferay.workspace.home.dir%\gradle.properties

echo.

::check plugins sdk in bundles folder
if exist %user.home.dir%\.liferay\bundles\%sdk.bundle.zip% (echo Thd sdk bundle exists.^(Successfully^)) else echo Thd sdk bundle doesn't exist.(Error)

::check dxp tomcat in bundles folder
if exist %user.home.dir%\.liferay\bundles\%portal.bundle.zip% (echo The portal bundle exists.^(Successfully^)) else echo The portal bundle doesn't exists.(Error)

::check Liferay Workspace doesn't exist
if not exist %start.menu.program.dir%\"Liferay Workspace" (echo The Liferay Workspace doesn't exist.^(Successfully^)) else echo The Liferay Workspace exists.(Error)

pause