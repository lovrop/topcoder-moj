#!/bin/sh

cd $(dirname $0)

VERSION=$(egrep -o 'Powered by moj [0-9.]+' src/moj/moj.java | egrep -o '[0-9.]+')
echo "Version is $VERSION."

echo "Creating jar..."
rm -f deploy/moj.jar
pushd bin >/dev/null
jar cf ../deploy/moj.jar moj/*.class
popd >/dev/null

sed -i -e "1s/.*/moj $VERSION/g" deploy/moj_instructions.txt

echo "Creating zip..."
rm -f deploy/moj.zip
zip -j deploy/moj.zip deploy/moj.jar deps/CodeProcessor.jar deps/FileEdit.jar deploy/moj_instructions.txt deploy/template.cpp deploy/template.java

echo "Done."
