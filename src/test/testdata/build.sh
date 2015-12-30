#!/usr/bin/env bash

set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

pushd ../resources
rm *.dex
popd

for folder in `find "$DIR" -type d -mindepth 1 -maxdepth 1`; do
  name=`basename $folder`
  pushd "$folder"
  rm *.class
  javac *.java
  zip -r "$folder.jar" *.class
  rm *.class
  dx --dex --output="$folder.dex" "$folder.jar"
  rm "$folder.jar"
  cp "$folder.dex" "../../resources/"
  rm "$folder.dex"
  popd
done