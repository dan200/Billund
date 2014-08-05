#!/bin/bash

source ./settings.sh

# Install resources
rm -rf $MCPDIR/jars/mods/Billund/assets
$SVN export -q resource/assets $MCPDIR/jars/mods/Billund/assets
rm -rf $MCPDIR/jars/mods/Billund/mcmod.info
$SVN export -q resource/mcmod.info $MCPDIR/jars/mods/Billund/mcmod.info
rm -rf $MCPDIR/jars/mods/Billund/pack.png
$SVN export -q resource/pack.png $MCPDIR/jars/mods/Billund/pack.png
rm -rf $MCPDIR/jars/mods/Billund/pack.mcmeta
$SVN export -q resource/pack.mcmeta $MCPDIR/jars/mods/Billund/pack.mcmeta

# Update version number in resources
sed "s/CCMODNAME/$CCMODNAME/g" $MCPDIR/jars/mods/Billund/mcmod.info > temp
sed "s/CCVERSION/$CCVERSION/g" temp > temp2
mv -f temp2 $MCPDIR/jars/mods/Billund/mcmod.info
rm -f temp temp2
