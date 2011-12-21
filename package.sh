#!/bin/sh

cd $(dirname $0)

VERSION=$(egrep -o 'Powered by moj [0-9][0-9a-zA-Z.]+' src/moj/moj.java | egrep -o '[0-9][0-9a-zA-Z.]+')
echo "Version is $VERSION."

echo "Creating jar..."
rm -f deploy/moj.jar
cd bin
jar cf ../deploy/moj.jar moj/*.class
cd ..

rm -f deploy/moj_instructions.txt
echo "moj $VERSION" >> deploy/moj_instructions.txt
echo "moj $VERSION" | sed -e 's/./=/g' >> deploy/moj_instructions.txt
echo >> deploy/moj_instructions.txt
cat README >> deploy/moj_instructions.txt

echo "Creating zip..."
target=deploy/moj_$VERSION.zip
rm -f $target
zip -j $target deploy/moj.jar deps/CodeProcessor.jar deps/FileEdit.jar deploy/moj_instructions.txt deploy/template.cpp deploy/template.java

echo "Done."
