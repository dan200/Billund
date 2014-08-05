/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
 
package dan200;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import dan200.billund.shared.BlockBillund;
import dan200.billund.shared.BuildInfo;
import dan200.billund.shared.IBillundProxy;
import dan200.billund.shared.ItemBrick;
import dan200.billund.shared.ItemOrderForm;
import dan200.billund.shared.PacketHandler;
import dan200.billund.shared.BillundPacket;
import dan200.billund.shared.EntityAirDrop;
import dan200.billund.shared.BillundSet;

///////////////
// UNIVERSAL //
///////////////

@Mod( modid = "Billund", name = BuildInfo.ModName, version = BuildInfo.Version )
@NetworkMod( channels = { "Billund" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class )
public class Billund
{
	// Block IDs
	public static int billundBlockID;
	
	// Item IDs
	public static int brickItemID;
	public static int orderFormItemID;
	
	// GUI IDs
	// None
	
	// Configuration options
	// None
	
	// Blocks and Items
	public static class Blocks
	{
		public static BlockBillund billund;
	}
	
	public static class Items
	{
		public static ItemBrick brick;
		public static ItemOrderForm orderForm;
	}
	
	// Other stuff	
	public static CreativeTabs creativeTab;
	
	public static CreativeTabs getCreativeTab()
	{
		return creativeTab;
	}
	
	// Implementation
	@Mod.Instance( value = "Billund" )
	public static Billund instance;
	
	@SidedProxy( clientSide = "dan200.billund.client.BillundProxyClient", serverSide = "dan200.billund.server.BillundProxyServer" )
	public static IBillundProxy proxy;
	
	public Billund()
	{
	}
	
	@Mod.EventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		// Load config
		Configuration config = new Configuration( event.getSuggestedConfigurationFile() );
		config.load();
		
		// Setup blocks
		Property prop = config.getBlock("billundBlockID", 2642);
		prop.comment = "The Block ID for Billund Blocks";
		billundBlockID = prop.getInt();

		// Setup items
		prop = config.getItem("brickItemID", 6242);
		prop.comment = "The Item ID for Billund Bricks";
		brickItemID = prop.getInt();
		
		prop = config.getItem("orderFormItemID", 6242);
		prop.comment = "The Item ID for Billund order forms";
		orderFormItemID = prop.getInt();
		
		// Setup general
		// None
		
		// Save config
		config.save();
		
		proxy.preLoad();
	}
	
	@Mod.EventHandler
	public void init( FMLInitializationEvent event )
	{	
		proxy.load();
	}
	
	public static boolean isClient()
	{
		return proxy.isClient();
	}
		
	public static boolean isServer()
	{
		return !proxy.isClient();
	}
	
	public static String getVersion()
	{
		return BuildInfo.Version;
	}
	
	private static boolean removeEmeralds( EntityPlayer player, int cost )
	{
		// Find enough emeralds
		int emeralds = 0;
		for( int i=0; i<player.inventory.getSizeInventory(); ++i )
		{
			ItemStack stack = player.inventory.getStackInSlot( i );
			if( stack != null && stack.getItem() == Item.emerald )
			{
				emeralds += stack.stackSize;
				if( emeralds >= cost )
				{
					break;
				}
			}
		}
		
		if( emeralds >= cost )
		{
			// Then expend them
			emeralds = cost;
			for( int i=0; i<player.inventory.getSizeInventory(); ++i )
			{
				ItemStack stack = player.inventory.getStackInSlot( i );
				if( stack != null && stack.getItem() == Item.emerald )
				{
					if( stack.stackSize <= emeralds )
					{
						player.inventory.setInventorySlotContents( i, null );
						emeralds -= stack.stackSize;
					}
					else
					{
						stack.stackSize -= emeralds;
						emeralds = 0;
					}
					if( emeralds == 0 )
					{
						break;
					}
				}
			}
			player.inventory.onInventoryChanged();
			return true;
		}
		return false;
	}
	
	public static void handlePacket( BillundPacket packet, Player player )
	{
		switch( packet.packetType )
		{
			case BillundPacket.OrderSet:
			{
				EntityPlayer entity = (EntityPlayer)player;
				World world = entity.worldObj;
				int set = packet.dataInt[0];
				int cost = BillundSet.get( set ).getCost();
				if( removeEmeralds( entity, cost ) )
				{
					Random r = new Random();
					world.spawnEntityInWorld( new EntityAirDrop(
						world,
						Math.floor( entity.posX - 8 + r.nextInt(16) ) + 0.5f,
						Math.min( world.getHeight(), 255 ) - r.nextInt(32) - 0.5f,
						Math.floor( entity.posZ - 8 + r.nextInt(16) ) + 0.5f,
						set
					) );
				}
				break;
			}
		}
	}
	
	public static void openOrderFormGUI( EntityPlayer player )
	{
		proxy.openOrderFormGUI( player );
    }
}
