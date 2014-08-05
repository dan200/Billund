#!/bin/bash

source ./settings.sh

# Install source
rm -rf $MCPDIR/src/minecraft/dan200
rm -rf $MCPDIR/src/minecraft/juniordev
$SVN export -q src/dan200 $MCPDIR/src/minecraft/dan200

# Update version number in source files
# BuildInfo:
sed "s/VERSION/$CCVERSION/g" $MCPDIR/src/minecraft/dan200/billund/shared/BuildInfo.java > temp
sed "s/42/$CCREVISION/g" temp > temp2
sed "s/MODNAME/$CCMODNAME/g" temp2 > temp3
cp -f temp3 $MCPDIR/src/minecraft/dan200/billund/shared/BuildInfo.java
rm -f temp temp2 temp3

# Compile
cd $MCPDIR
chmod +x ./recompile.sh
./recompile.sh
cd $CCDIR

# Move binaries to resources dir
rm -rf $MCPDIR/jars/mods/Billund
mkdir $MCPDIR/jars/mods/Billund
mv -f $MCPDIR/bin/minecraft/dan200 $MCPDIR/jars/mods/Billund/dan200

# Install resources
source ./pushres.sh

# Copy client binaries to server
rm -rf $MCPDIR/bin/minecraft_server
cp -rf $MCPDIR/bin/minecraft $MCPDIR/bin/minecraft_server
