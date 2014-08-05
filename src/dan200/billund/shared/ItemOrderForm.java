/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
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
import net.minecraft.util.Icon;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dan200.Billund;

public class ItemOrderForm extends Item
{
	private static Icon s_icon;
	
	public ItemOrderForm(int i)
    {
        super(i);
        setMaxStackSize( 1 );
		setHasSubtypes( true );
		setUnlocalizedName( "billform" );
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
		list.add( new ItemStack( Billund.Items.orderForm.itemID, 1, 0 ) );
    }
    
	@Override
    public ItemStack onItemRightClick( ItemStack stack, World world, EntityPlayer player )
    {
    	if( Billund.isClient() && world.isRemote )
    	{
    		Billund.openOrderFormGUI( player );
    	}
		return stack;
	}

	@Override
	public void registerIcons( IconRegister iconRegister )
	{
		s_icon = iconRegister.registerIcon( "billund:orderform" );
	}

    @Override
    public Icon getIconFromDamage( int damage )
    {
    	return s_icon;
    }
}
