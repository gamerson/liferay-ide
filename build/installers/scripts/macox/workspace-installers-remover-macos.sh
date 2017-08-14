#Add properties here
workspaceDir="/Users/liferay_cn/liferay-workspace"

#1 remove blade cache
rm -rf ~/.blade

#2 remove jpm cache 
rm -rf ~/.jpm

#3 remove token file
rm -rf ~/.liferay/token

#4 remove bundles
rm -rf ~/.liferay/bundles

#5 remove liferay workspce
rm -rf $workspaceDir

#6 eject liferay workspace
hdiutil eject "/Volumes/Liferay Workspace"