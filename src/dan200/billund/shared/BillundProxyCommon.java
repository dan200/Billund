/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import dan200.Billund;
import dan200.billund.shared.BlockBillund;
import dan200.billund.shared.EntityAirDrop;

public abstract class BillundProxyCommon implements IBillundProxy
{
	public BillundProxyCommon()
	{
	}
	
	// IBillundProxy implementation

	@Override
	public void preLoad()
	{
		registerItems();
	}
	
	@Override		
	public void load()
	{
		System.out.println( "Loading Billund v"+BuildInfo.Version+" (rev "+BuildInfo.Revision+")" );
		registerEntities();
		registerTileEntities();
		registerForgeHandlers();
	}
	
	@Override
	public abstract boolean isClient();

	@Override
	public abstract void openOrderFormGUI( EntityPlayer player );
		
	private void registerItems()
	{		
		// Register our own creative tab
		Billund.creativeTab = new CreativeTabBillund( CreativeTabs.getNextID(), "Billund" );
		
		// Billund block
		Billund.Blocks.billund = new BlockBillund( Billund.billundBlockID );
		GameRegistry.registerBlock( Billund.Blocks.billund, "Billund" );
		LanguageRegistry.instance().addNameForObject( Billund.Blocks.billund, "en_US", "Billund" );
		
		// Brick item
		Billund.Items.brick = new ItemBrick( Billund.brickItemID );
		LanguageRegistry.instance().addNameForObject( Billund.Items.brick, "en_US", "Billund Brick" );
		
		// Order form item
		Billund.Items.orderForm = new ItemOrderForm( Billund.orderFormItemID );
		LanguageRegistry.instance().addNameForObject( Billund.Items.orderForm, "en_US", "Billund Order Form" );		
	}
	
	private void registerEntities()
	{
		// airdrop entity
		EntityRegistry.registerModEntity( EntityAirDrop.class, "AirDrop", 1, Billund.instance, 80, 3, true );
		LanguageRegistry.instance().addStringLocalization( "entity.Billund_AirDrop.name", "Air Drop" );
	}
		
	private void registerTileEntities()
	{
		// Tile Entities
		GameRegistry.registerTileEntity( TileEntityBillund.class, "billund" );
	}
		
	private void registerForgeHandlers()
	{
		ForgeHandlers handlers = new ForgeHandlers();
		MinecraftForge.EVENT_BUS.register( handlers );
	}
			
	public class ForgeHandlers
	{
		private Random r = new Random();
		
		private ForgeHandlers()
		{
		}

		// Forge event responses 
		
		@ForgeSubscribe
		public void onEntityLivingDeath( LivingDeathEvent event )
		{
			if( event.entity.worldObj.isRemote )
			{
				return;
			}
			
			if( event.entity instanceof EntityZombie )
			{
				EntityLivingBase living = (EntityLivingBase)event.entity;
				if( (living.isChild() && r.nextInt(20) == 0) ||
				    (!living.isChild() && r.nextInt(100) == 0) )
				{
					event.entity.entityDropItem( new ItemStack( Billund.Items.orderForm, 1 ), 0.0f );
				}
			}
		}
	}
}
