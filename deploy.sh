#!/bin/bash

source settings.sh 

# Cleanup previously deployed files
rm -rf deploy
mkdir deploy

# Recompile
./recompile.sh

# Copy files from /jars into /bin for reobfuscation
mv -f $MCPDIR/jars/mods/Billund/dan200 $MCPDIR/bin/minecraft/dan200

# Reobfuscate
cd $MCPDIR
chmod +x ./reobfuscate.sh
./reobfuscate_srg.sh
cd $CCDIR

# Copy files back from /bin to /jars
mv -f $MCPDIR/bin/minecraft/dan200 $MCPDIR/jars/mods/Billund/dan200

# Start making the deployment
rm -rf deploy/universal
mkdir deploy/universal

# Install class files
cp -r $MCPDIR/reobf/minecraft/dan200 deploy/universal/

# Install resources
cp -r $MCPDIR/jars/mods/Billund/mcmod.info deploy/universal/mcmod.info
cp -r $MCPDIR/jars/mods/Billund/pack.mcmeta deploy/universal/pack.mcmeta
cp -r $MCPDIR/jars/mods/Billund/pack.png deploy/universal/pack.png
mkdir deploy/universal/assets
cp -r $MCPDIR/jars/mods/Billund/assets/billund deploy/universal/assets/billund

# Create ZIP
cd deploy/universal
echo "Zipping..."
zip -r ../$CCMODNAME$CCVERSION.zip * > /dev/null
cd ../..

# Copy Universal ZIP to real minecraft directory for testing
#rm -rf "$MCDIR/mods/$CCMODNAME.zip"
#cp deploy/$CCMODNAME$CCVERSION.zip "$MCDIR/mods/$CCMODNAME.zip"

# Copy Universal ZIP to real minecraft server directory for testing
#rm -rf "$MCSERVERDIR/mods/$CCMODNAME.zip"
#cp deploy/$CCMODNAME$CCVERSION.zip "$MCSERVERDIR/mods/$CCMODNAME.zip"

# Delete the reobfuscated files, because it confuses MCP
rm -rf $MCPDIR/reobf/minecraft/dan200
