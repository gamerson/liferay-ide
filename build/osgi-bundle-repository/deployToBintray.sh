#!/bin/bash

# Deprecated: Deploys the generated p2 repository to Bintray.
#
# Deprecated: @see https://bintray.com/docs/api/#_upload_content
#
# Deploys the generated p2 repository to Linode

echo "please make sure your s3cmd has been configured"

if [ $# != 4 ]
then
  echo "Usage: $0 repo_path bt_repo bt_package bt_version"
  exit 1
fi

REPO_PATH=$1
BT_REPO=$2
BT_PACKAGE=$3
BT_VERSION=$4

echo "uploading to version $BT_VERSION ..."
s3cmd --no-mime-magic --acl-public --delete-removed --delete-after sync $REPO_PATH/ s3://devtools-s3.liferay.com/$BT_REPO/$BT_PACKAGE/$BT_VERSION/
echo