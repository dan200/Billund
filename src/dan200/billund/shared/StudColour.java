/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import dan200.Billund;
	
public class StudColour
{
	public static final int TranslucentWall = -2;
	public static final int Wall = -1;
	
	public static final int Red = 0;
	public static final int Green = 1;
	public static final int Blue = 2;
	public static final int Orange = 3;
	
	public static final int Yellow = 4;
	public static final int Brown = 5;
	public static final int LightGreen = 6;
	public static final int Pink = 7;
	
	public static final int Purple = 8;
	
	public static final int White = 9;
	public static final int LightGrey = 10;
	public static final int DarkGrey = 11;
	public static final int Black = 12;
	
	public static final int Count = 13;
}
