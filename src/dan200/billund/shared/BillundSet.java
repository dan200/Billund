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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import dan200.Billund;

public class BillundSet
{
	public static BillundSet get( int index )
	{
		return new BillundSet( index );
	}
	
    private static String[] s_setNames = new String[] {
		"Starter Pack",
		"Colour Pack A",
		"Colour Pack B",
		"Colour Pack C",
		"Colour Pack D",
	};
        
    private static int[] s_setCosts = new int[] {
    	7,
    	10,
    	10,
    	10,
    	10
    };	
    
    private int m_index;
	
	public BillundSet( int index )
	{
		m_index = index;
	}
	
	public int getCost()
	{
		return s_setCosts[ m_index ];
	}
	
	public String getDescription()
	{
		return s_setNames[ m_index ];
	}
	
	private IInventory s_addInventory = null;
	private int s_addIndex = 0;
	
	public void populateChest( IInventory inv )
	{
		s_addIndex = 0;
		s_addInventory = inv;
		
		switch( m_index )
		{
			case 0:
			{
				// Starter set
				// Basic pieces in 6 colours
				addBasic( StudColour.Red );
				addBasic( StudColour.Green );
				add( null );		
				
				addBasic( StudColour.Blue );
				addBasic( StudColour.Yellow );
				add( null );		
				
				addBasic( StudColour.White );
				addBasic( StudColour.Black );
				add( null );
				break;
			}
			case 1:
			{
				// Colour Pack
				// pieces in 3 colours
				addAll( StudColour.Red );
				addAll( StudColour.Green );
				addAll( StudColour.Blue );
				break;
			}
			case 2:
			{
				// Colour Pack
				// pieces in 3 colours
				addAll( StudColour.Orange );
				addAll( StudColour.Yellow );
				addAll( StudColour.LightGreen );
				break;
			}
			case 3:
			{
				// Colour Pack
				// pieces in 3 colours
				addAll( StudColour.Pink );
				addAll( StudColour.Purple );
				addAll( StudColour.White );
				break;
			}
			case 4:
			{
				// Colour Pack
				// pieces in 3 colours
				addAll( StudColour.LightGrey );
				addAll( StudColour.DarkGrey );
				addAll( StudColour.Black );
				break;
			}
		}
	}
	
	private void add( ItemStack stack )
	{
		int slot = s_addIndex++;
		if( slot < s_addInventory.getSizeInventory() )
		{
			s_addInventory.setInventorySlotContents( slot, stack );
		}
	}

	private void addBasic( int colour )
	{
		add( ItemBrick.create( colour, 1, 2, 24 ) );
		add( ItemBrick.create( colour, 1, 4, 24 ) );
		add( ItemBrick.create( colour, 2, 2, 24 ) );
		add( ItemBrick.create( colour, 2, 4, 24 ) );
	}

	private void addAll( int colour )
	{
		add( ItemBrick.create( colour, 1, 1, 24 ) );
		add( ItemBrick.create( colour, 1, 2, 24 ) );
		add( ItemBrick.create( colour, 1, 3, 24 ) );
		add( ItemBrick.create( colour, 1, 4, 24 ) );
		add( ItemBrick.create( colour, 1, 6, 24 ) );
		add( ItemBrick.create( colour, 2, 2, 24 ) );
		add( ItemBrick.create( colour, 2, 3, 24 ) );
		add( ItemBrick.create( colour, 2, 4, 24 ) );
		add( ItemBrick.create( colour, 2, 6, 24 ) );
	}
}
