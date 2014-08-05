#!/bin/bash

SVN="/opt/subversion/bin/svn"

CCDIR=`pwd`
FORGEDIR="../forge947"
MCPDIR="$FORGEDIR/mcp"

MCDIR="../minecraft151"
MCSERVERDIR="../minecraft_server151"

CCMODNAME="Billund"
CCVERSION="1.01"
CCREVISION=`$SVN info | grep "Last Changed Rev: [0-9]*" | cut -d ' ' -f 4`
