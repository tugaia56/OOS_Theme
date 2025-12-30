#!/bin/bash

NEWVERNAME=${GITHUB_REF_NAME/v/}

# sub changelog
EN_CHANGELOG="./app/src/main/res/values/changelog.xml"

#temp changelog
TMP_CHANGELOG="tmp_changelog_lines.md"
TMP_ADDED="tmp_added.md"
TMP_UPDATED="tmp_updated.md"
TMP_REMOVED="tmp_removed.md"
rm -f tmp_*

# reset the file - most likely not needed
rm -f changeLog.md
rm -f Tchangelog.htm
touch changeLog.md
touch $TMP_CHANGELOG
touch Tchangelog.htm

# copy the changelog xml into new md
echo "$(cat $EN_CHANGELOG)" >> changeLog.md
# let's work on custom strings
sed -i 's\        <item>ADDED:\## Added\g' changeLog.md
sed -i 's\        <item>UPDATED:\## Updated\g' changeLog.md
sed -i 's\        <item>REMOVED:\## Removed\g' changeLog.md
# remove <item> tag
sed -i 's\        <item>\### \g' changeLog.md
# remove ending </item> tag
sed -i 's\</item>\\g' changeLog.md
# remove first 4 lines (xml, resources, string-array and versioning)
sed -i 1,4d changeLog.md
# remove last 2 lines of useless xml tags
head -n -2 < changeLog.md | tee changeLog.md


echo "*Dark Shadow Theme v$NEWVERNAME released!*" > body.msg
echo "  " >> body.msg
echo "*Changelog:*  " >> body.msg
cat changeLog.md >> body.msg
echo 'Body<<EOF' >> $GITHUB_ENV
cat body.msg >> $GITHUB_ENV
echo 'EOF' >> $GITHUB_ENV

echo "[🌎 Site Dark Shadows Theme](https://mythemedarkandmore.altervista.org/)" > telegram.msg
echo " " >> telegram.msg
echo "⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️" >> telegram.msg
echo " " >> telegram.msg
echo "Dark Shadow Theme "$NEWVERNAME >> telegram.msg
echo " " >> telegram.msg
cat changeLog.md >> telegram.msg
echo " " >> telegram.msg
echo "If you find any bugs or incorrect colors, please report them to the group and I'll fix them as soon as possible. Thank you very much." >> telegram.msg
echo "For those of you using OxygenOS, I recommend using Oxygen Customizer; it has many extra features." >> telegram.msg
echo " " >> telegram.msg
echo "[🙇 Support the developer ❤️](https://www.paypal.com/donate/?business=UGACHZ2UGBEBJ&no_recurring=1&currency_code=EUR)" >> telegram.msg
echo " " >> telegram.msg
echo "Thank you all" >> telegram.msg
echo 'TMessage<<EOF' >> $GITHUB_ENV
cat telegram.msg >> $GITHUB_ENV
echo 'EOF' >> $GITHUB_ENV