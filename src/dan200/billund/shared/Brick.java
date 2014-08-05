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
	
public class Brick
{	
	public int Colour;
	public int XOrigin;
	public int YOrigin;
	public int ZOrigin;
	public int Width;
	public int Height;
	public int Depth;
	
	public Brick( int colour, int xOrigin, int yOrigin, int zOrigin, int width, int height, int depth )
	{
		Colour = colour;
		XOrigin = xOrigin;
		YOrigin = yOrigin;
		ZOrigin = zOrigin;
		Width = width;
		Height = height;
		Depth = depth;
	}
}
