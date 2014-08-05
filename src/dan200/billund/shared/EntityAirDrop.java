package dan200.billund.shared;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import dan200.billund.shared.BillundSet;

public class EntityAirDrop extends Entity
{
    public int blockID;
    public int metadata;
    public int setType;
    public boolean deployed;

    public EntityAirDrop( World world )
    {
        super( world );
        this.blockID = Block.chest.blockID;
        this.metadata = 0;
    }

    public EntityAirDrop( World world, double x, double y, double z, int set )
    {
        this( world );
        this.preventEntitySpawning = true;
        this.setSize( 0.98f, 0.98f );
        this.yOffset = this.height / 2.0f;
        this.setPosition( x, y, z );
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setType = set;
    }

	@Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
    protected void entityInit()
    {
    }

	@Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

	@Override
    public void onUpdate()
    {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
        
        if( !this.deployed )
        {
			float deployHeight = (float)(this.worldObj.getTopSolidOrLiquidBlock( 
				MathHelper.floor_double( this.posX ),
				MathHelper.floor_double( this.posZ )
			)) + 11.0f;
        	if( this.posY <= deployHeight )
			{
				this.deployed = true;
			}
		}
        
        if( !this.deployed )
        {
			this.motionY -= 0.003;
		}
		else
		{
			this.motionY -= 0.02;
		}
		
		this.moveEntity( this.motionX, this.motionY, this.motionZ );
		this.motionX *= 0.98;
		this.motionY *= 0.98;
		this.motionZ *= 0.98;

		if( !this.worldObj.isRemote )
		{
			int blockX = MathHelper.floor_double( this.posX );
			int blockY = MathHelper.floor_double( this.posY );
			int blockZ = MathHelper.floor_double( this.posZ );

			if( this.onGround )
			{
				this.motionX *= 0.7;
				this.motionZ *= 0.7;
				this.motionY *= -0.5;

				if( this.worldObj.getBlockId( blockX, blockY, blockZ ) != Block.pistonMoving.blockID )
				{
					this.setDead();
					
					// Set the block
					this.worldObj.setBlock( blockX, blockY, blockZ, this.blockID, this.metadata, 3 );
					
					// Populate the block
					TileEntity entity = this.worldObj.getBlockTileEntity( blockX, blockY, blockZ );
					if( entity != null && entity instanceof IInventory )
					{
						IInventory inv = (IInventory)entity;
						BillundSet.get( this.setType ).populateChest( inv );
						inv.onInventoryChanged();
					}					
				}
			}
			else if( blockY < 0 )
			{
				this.setDead();
			}
		}
    }

	@Override
    protected void fall(float par1)
    {
    }

	@Override
    protected void writeEntityToNBT( NBTTagCompound nbtTagCompound )
    {
        nbtTagCompound.setInteger( "BlockID", this.blockID );
        nbtTagCompound.setByte( "Data", (byte)this.metadata );
        nbtTagCompound.setInteger( "Set", this.setType );
        nbtTagCompound.setBoolean( "Deployed", this.deployed );
    }

	@Override
    protected void readEntityFromNBT( NBTTagCompound nbtTagCompound )
    {
		this.blockID = nbtTagCompound.getInteger( "BlockID" );
        this.metadata = nbtTagCompound.getByte( "Data" ) & 255;
        this.setType = nbtTagCompound.getInteger( "Set" );
        this.deployed = nbtTagCompound.getBoolean( "Deployed" );
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0f;
    }

    @SideOnly(Side.CLIENT)
    public World getWorld()
    {
        return this.worldObj;
    }

    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }
}
