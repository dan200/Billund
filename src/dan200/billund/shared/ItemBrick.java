/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.minecraft.util.MathHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dan200.Billund;

public class ItemBrick extends Item
{
	public ItemBrick(int i)
    {
        super(i);
        setMaxStackSize( 64 );
		setHasSubtypes( true );
		setUnlocalizedName( "billbrick" );
		setCreativeTab( Billund.getCreativeTab() );
    }

	public static ItemStack create( int colour, int width, int depth, int quantity )
	{
		int damage = ((width - 1) & 0x1) + (((depth - 1) & 0x7) << 1) + ((colour & 0xf) << 4);
		return new ItemStack( Billund.Items.brick.itemID, quantity, damage );
	}

	@Override
    public void getSubItems( int itemID, CreativeTabs tabs, List list )
    {
    	for( int colour=0; colour<StudColour.Count; ++colour )
    	{
	    	list.add( create( colour, 1, 1, 1 ) );
	    	list.add( create( colour, 1, 2, 1 ) );
	    	list.add( create( colour, 1, 3, 1 ) );
	    	list.add( create( colour, 1, 4, 1 ) );
	    	list.add( create( colour, 1, 6, 1 ) );
	    	list.add( create( colour, 2, 2, 1 ) );
	    	list.add( create( colour, 2, 3, 1 ) );
	    	list.add( create( colour, 2, 4, 1 ) );
	    	list.add( create( colour, 2, 6, 1 ) );
	    }
    }
    
    public static TileEntityBillund.StudRaycastResult raycastFromPlayer( World world, EntityPlayer player, float f )
    {    	    	
    	// Calculate the raycast origin and direction
    	double yOffset2 = ( !world.isRemote && player.isSneaking() ) ? -0.08 : 0.0; // TODO: Improve
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double x = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double y = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62 - player.yOffset + yOffset2; // TODO: Improve
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 position = world.getWorldVec3Pool().getVecFromPool( x, y, z );
        
        float f3 = MathHelper.cos( -yaw * 0.017453292F - (float)Math.PI );
        float f4 = MathHelper.sin( -yaw * 0.017453292F - (float)Math.PI );
        float f5 = -MathHelper.cos( -pitch * 0.017453292F );
        float f6 = MathHelper.sin( -pitch * 0.017453292F );
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        
        float distance = 5.0f;
        if( player instanceof EntityPlayerMP )
        {
            distance = (float)((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 direction = world.getWorldVec3Pool().getVecFromPool( (double)f7, (double)f6, (double)f8 );        
        
        // Do the raycast
        return TileEntityBillund.raycastStuds( world, position, direction, distance );
    }
    
    public static Brick getPotentialBrick( ItemStack stack, World world, EntityPlayer player, float f )
    {
        // Do the raycast
    	TileEntityBillund.StudRaycastResult result = raycastFromPlayer( world, player, f );
    	if( result != null )
    	{
    		// Calculate where to place the brick
			int width = getWidth( stack );
			int depth = getDepth( stack );
			int height = 1;
			if( player.isSneaking() )
			{
				int temp = depth;
				depth = width;
				width = temp;
			}			
		
			int placeX = result.hitX;
			int placeY = result.hitY;
			int placeZ = result.hitZ;
			switch( result.hitSide )
			{
				case 0: placeY -= height; break;
				case 1: placeY++; break;
				case 2: placeZ -= depth; break;
				case 3: placeZ++; break;
				case 4: placeX -= width; break;
				case 5: placeX++; break;
			}
			
			// Try a few positions nearby
			Brick brick = new Brick( getColour( stack ), placeX, placeY, placeZ, width, height, depth );
			for( int x=0; x<width; ++x )
			{
				for( int z=0; z<depth; ++z )
				{
					for( int y=0; y<height; ++y )
					{
						brick.XOrigin = placeX - x;
						brick.YOrigin = placeY - y;
						brick.ZOrigin = placeZ - z;
						if( TileEntityBillund.canAddBrick( world, brick ) )
						{
							return brick;
						}
					}
				}
			}
    	}
    	return null;
    }
    
    public static Brick getExistingBrick( World world, EntityPlayer player, float f )
    {
        // Do the raycast
    	TileEntityBillund.StudRaycastResult result = raycastFromPlayer( world, player, f );
		if( result != null )
		{
			Stud stud = TileEntityBillund.getStud( world, result.hitX, result.hitY, result.hitZ );
			if( stud != null && stud.Colour != StudColour.Wall)
			{
				return new Brick( stud.Colour, stud.XOrigin, stud.YOrigin, stud.ZOrigin, stud.BrickWidth, stud.BrickHeight, stud.BrickDepth );
			}
		}
		return null;
    }

	@Override
    public boolean onItemUseFirst( ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
    {
//    	Brick brick = getPotentialBrick( stack, world, player, 1.0f );
//    	if( brick != null )
//		{
//			return true;
//		}
		return false;
    }
    
	@Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
//    	Brick brick = getPotentialBrick( stack, world, player, 1.0f );
//    	if( brick != null )
//		{
//			return true;
//		}
		return false;
	}
	    
	@Override
    public ItemStack onItemRightClick( ItemStack stack, World world, EntityPlayer player )
    {
    	Brick brick = getPotentialBrick( stack, world, player, 1.0f );
    	if( brick != null )
		{
			if( !world.isRemote )
			{
				// Place the brick
				TileEntityBillund.addBrick( world, brick );

				if( !player.capabilities.isCreativeMode )
				{
					// Decrement stackSize
					stack.stackSize--;
				}
			}
		}
		return stack;
	}
    
    public static int getWidth( ItemStack stack )
    {
		int damage = stack.getItemDamage();
		return (damage & 0x1) + 1;
    }
    
    public static int getHeight( ItemStack stack )
    {
    	return 1;
    }
    
    public static int getDepth( ItemStack stack )
    {
		int damage = stack.getItemDamage();
		return ((damage >> 1) & 0x7) + 1;
    }
    
    public static int getColour( ItemStack stack )
    {
		int damage = stack.getItemDamage();
		return ((damage >> 4) & 0xf);
    }
}
