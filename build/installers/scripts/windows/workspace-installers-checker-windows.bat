@echo off
::add all variable
set user.home.dir=C:\Users\liferay
set start.menu.program.dir="C:\ProgramData\Microsoft\Windows\Start Menu\Programs"
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
if exist %user.home.dir%\liferay-workspace (echo The liferay-workspace exists.^(Successfully^)) else echo The liferay-workspace doesn't exist.(Error)

::check Liferay Workspace doesn't exist
if not exist %start.menu.program.dir%\"Liferay Workspace" (echo The Liferay Workspace doesn't exist.^(Successfully^)) else echo The Liferay Workspace exists.(Error)

pause