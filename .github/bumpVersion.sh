#!/bin/bash

NEWVERNAME=${GITHUB_REF_NAME/v/}
NEWVERCODE=${NEWVERNAME//.}

echo 'VTag<<EOF' >> $GITHUB_ENV
echo ${GITHUB_REF_NAME} >> $GITHUB_ENV
echo 'EOF' >> $GITHUB_ENV

echo 'VName<<EOF' >> $GITHUB_ENV
echo 'Dark Shadow Theme v'$NEWVERNAME >> $GITHUB_ENV
echo 'EOF' >> $GITHUB_ENV

sed -i 's/versionCode.*/versionCode = '$NEWVERCODE'/' app/build.gradle.kts
sed -i 's/versionName =.*/versionName = "'$NEWVERNAME'"/' app/build.gradle.kts
sed -i 's/Dark_Shadow_Theme_v.*/Dark_Shadow_Theme_v'$NEWVERNAME'.apk"/' app/build.gradle.kts

#sed -i 's/Dark_Shadow_Theme_v.*/Dark_Shadow_Theme_v'$NEWVERNAME'.apk"/' app/build.gradle.kts
#sed -i 's/Versione.*/Versione '$NEWVERNAME'<\/item>/' app/src/main/res/values/theme_configurations.xml
#sed -i 's/Versione.*/Versione '$NEWVERNAME'<\/item>/' app/src/main/res/values-it/theme_configurations.xml