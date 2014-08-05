/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import dan200.Billund;

public class BlockBillund extends BlockContainer
{
	public int blockRenderID;
	private static Icon s_transparentIcon;
	private static Icon[] s_icons;

	private static Brick s_hoverBrick = null;
	public static void setHoverBrick( Brick brick )
	{
		s_hoverBrick = brick;
	}	
	
    public static Icon getIcon( int studColour )
    {
    	if( studColour >= 0 && studColour < StudColour.Count )
    	{
	    	return s_icons[ studColour ];
	    }
	    return s_transparentIcon;
    }
    
    public BlockBillund(int i)
    {
        super( i, Material.wood );
		setHardness( 0.25f );
		setUnlocalizedName( "billund" );

        // These get replaced on the client
		blockRenderID = -1;
    }
    
    	
    @Override
    public void addCreativeItems( ArrayList list )
    {
    }
    
    @Override
    public int getRenderType()
	{
		return blockRenderID;
	}

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
	public boolean isOpaqueCube()
    {
        return false;
    }
	
    @Override
    public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
    {
    	return false;
    }
	
    @Override
    public boolean isBlockSolidOnSide( World world, int i, int j, int k, ForgeDirection side )
    {
		return false;
	}
	
	@Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

	@Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
    	if( !world.isRemote )
    	{
			TileEntity tileEntity = world.getBlockTileEntity( x, y, z );
			if( tileEntity != null && tileEntity instanceof TileEntityBillund )
			{
				// Find a brick to destroy
				TileEntityBillund billund = (TileEntityBillund)tileEntity;
				Brick brick = ItemBrick.getExistingBrick( world, player, 1.0f );
				if( brick != null )
				{					
					// Remove the brick
					TileEntityBillund.removeBrick( world, brick );

					// Spawn an item for the destroyed brick
					if( !player.capabilities.isCreativeMode )
					{						
						float brickX = ((float)brick.XOrigin + (float)brick.Width * 0.5f) / (float)TileEntityBillund.ROWS_PER_BLOCK;
						float brickY = ((float)brick.YOrigin + (float)brick.Height) /  (float)TileEntityBillund.LAYERS_PER_BLOCK;
						float brickZ = ((float)brick.ZOrigin + (float)brick.Depth * 0.5f) /  (float)TileEntityBillund.ROWS_PER_BLOCK;
						ItemStack stack = ItemBrick.create( brick.Colour, Math.min( brick.Width, brick.Depth ), Math.max( brick.Width, brick.Depth ), 1 );
			            EntityItem entityitem = new EntityItem( world, brickX, brickY + 0.05f, brickZ, stack );
						entityitem.motionX = 0.0f;
						entityitem.motionY = 0.0f;
						entityitem.motionZ = 0.0f;
						entityitem.delayBeforeCanPickup = 30;
						world.spawnEntityInWorld( entityitem );
					}
					
					// Clear the block
					if( billund.isEmpty() )
					{
						world.setBlockToAir(x, y, z);
						return true;
					}					
				}
			}  
	    }
	    return false;
    }
    
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int l )
    {
		TileEntity tileEntity = world.getBlockTileEntity( i, j, k );
		if( tileEntity != null && tileEntity instanceof TileEntityBillund )
		{
			TileEntityBillund billund = (TileEntityBillund)tileEntity;
			billund.cullOrphans();
			if( billund.isEmpty() )
			{
				world.setBlockToAir(i, j, k);
			}
		}
    }
   
	@Override
    public void setBlockBoundsBasedOnState( IBlockAccess world, int i, int j, int k )
    {
	    if( s_hoverBrick != null )
	    {	
	    	// See if the hovered brick is in the start bit
	    	int sx = s_hoverBrick.XOrigin;
	    	int sy = s_hoverBrick.YOrigin;
	    	int sz = s_hoverBrick.ZOrigin;
	    	{
				int localX = (sx % TileEntityBillund.ROWS_PER_BLOCK + TileEntityBillund.ROWS_PER_BLOCK) % TileEntityBillund.ROWS_PER_BLOCK;
				int localY = (sy % TileEntityBillund.LAYERS_PER_BLOCK + TileEntityBillund.LAYERS_PER_BLOCK) % TileEntityBillund.LAYERS_PER_BLOCK;
				int localZ = (sz % TileEntityBillund.ROWS_PER_BLOCK + TileEntityBillund.ROWS_PER_BLOCK) % TileEntityBillund.ROWS_PER_BLOCK;
				int blockX = (sx - localX) / TileEntityBillund.ROWS_PER_BLOCK;
				int blockY = (sy - localY) / TileEntityBillund.LAYERS_PER_BLOCK;
				int blockZ = (sz - localZ) / TileEntityBillund.ROWS_PER_BLOCK;
						
				if( (i == blockX || i == blockX + 1) &&
					(j == blockY || j == blockY + 1) &&
					(k == blockZ || k == blockZ + 1) )
				{
					float xScale = 1.0f / (float)TileEntityBillund.ROWS_PER_BLOCK;
					float yScale = 1.0f / (float)TileEntityBillund.LAYERS_PER_BLOCK;
					float zScale = 1.0f / (float)TileEntityBillund.ROWS_PER_BLOCK;
					
					float startX = (float)(sx - (i*TileEntityBillund.ROWS_PER_BLOCK)) * xScale;
					float startY = (float)(sy - (j*TileEntityBillund.LAYERS_PER_BLOCK)) * yScale;
					float startZ = (float)(sz - (k*TileEntityBillund.ROWS_PER_BLOCK)) * zScale;
					this.setBlockBounds(
						startX, startY, startZ,
						startX + (float)s_hoverBrick.Width * xScale,
						startY + (float)s_hoverBrick.Height * yScale,
						startZ + (float)s_hoverBrick.Depth * zScale
					);
					return;
				}
			}
		}

		// Set bounds to something that should hopefully never be hit
		this.setBlockBounds( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f );
	}

	@Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }

	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }
    
	@Override
    public void addCollisionBoxesToList( World world, int i, int j, int k, AxisAlignedBB bigBox, List list, Entity entity )
    {
		TileEntity tileEntity = world.getBlockTileEntity( i, j, k );
		if( tileEntity != null && tileEntity instanceof TileEntityBillund )
		{
			double originX = (double)i;
			double originY = (double)j;
			double originZ = (double)k;
			double stepX = 1.0 / (double)TileEntityBillund.STUDS_PER_ROW;
			double stepY = 1.0 / (double)TileEntityBillund.STUDS_PER_COLUMN;
			double stepZ = 1.0 / (double)TileEntityBillund.STUDS_PER_ROW;
			
			int minsx = i * TileEntityBillund.STUDS_PER_ROW;
			int minsy = j * TileEntityBillund.STUDS_PER_COLUMN;
			int minsz = k * TileEntityBillund.STUDS_PER_ROW;
			
			TileEntityBillund billund = (TileEntityBillund)tileEntity;
			for( int x=0; x<TileEntityBillund.STUDS_PER_ROW; ++x )
			{
				for( int y=0; y<TileEntityBillund.STUDS_PER_COLUMN; ++y )
				{
					for( int z=0; z<TileEntityBillund.STUDS_PER_ROW; ++z )
					{
						Stud stud = billund.getStudLocal( x, y, z );
						if( stud != null )
						{
							double startX = originX + (double)x * stepX;
							double startY = originY + (double)y * stepY;
							double startZ = originZ + (double)z * stepZ;
							if( stud.XOrigin < minsx || stud.YOrigin < minsy || stud.ZOrigin < minsz )
							{
								// If the origin of this brick is in a different block, add our own aabbs for each stud
								AxisAlignedBB littleBox = AxisAlignedBB.getAABBPool().getAABB(
									startX, startY, startZ,
									startX + stepX,
									startY + stepY,
									startZ + stepZ
								);
								if( littleBox.intersectsWith( bigBox ) )
								{
									list.add( littleBox );
								}					        
							}
							else
							{
								// Else, if this stud *is* the origin, add an aabb for the whole thing
								int sx = x + minsx;
								int sy = y + minsy;
								int sz = z + minsz;
								if( sx == stud.XOrigin && sy == stud.YOrigin && sz == stud.ZOrigin )
								{
									AxisAlignedBB littleBox = AxisAlignedBB.getAABBPool().getAABB(
										startX, startY, startZ,
										startX + (double)stud.BrickWidth * stepX,
										startY + (double)stud.BrickHeight * stepY,
										startZ + (double)stud.BrickDepth * stepZ										
									);
									if( littleBox.intersectsWith( bigBox ) )
									{
										list.add( littleBox );
									}					        
								}
							}
						}
					}
				}
			}
		}    	
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded(world, i, j, k);
	}
    
	@Override
    public void onBlockPlacedBy( World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack )
    {
    	super.onBlockPlacedBy( world, i, j, k, entityliving, itemstack );
    	if (world.isRemote)
    	{
    		return;
    	}
    }
    
	@Override
    public Icon getBlockTexture( IBlockAccess world, int i, int j, int k, int side )
    {
    	if( s_hoverBrick != null )
    	{
	    	int sx = s_hoverBrick.XOrigin;
	    	int sy = s_hoverBrick.YOrigin;
	    	int sz = s_hoverBrick.ZOrigin;
	    	{
				int localX = (sx % TileEntityBillund.ROWS_PER_BLOCK + TileEntityBillund.ROWS_PER_BLOCK) % TileEntityBillund.ROWS_PER_BLOCK;
				int localY = (sy % TileEntityBillund.LAYERS_PER_BLOCK + TileEntityBillund.LAYERS_PER_BLOCK) % TileEntityBillund.LAYERS_PER_BLOCK;
				int localZ = (sz % TileEntityBillund.ROWS_PER_BLOCK + TileEntityBillund.ROWS_PER_BLOCK) % TileEntityBillund.ROWS_PER_BLOCK;
				int blockX = (sx - localX) / TileEntityBillund.ROWS_PER_BLOCK;
				int blockY = (sy - localY) / TileEntityBillund.LAYERS_PER_BLOCK;
				int blockZ = (sz - localZ) / TileEntityBillund.ROWS_PER_BLOCK;
				if( blockX == i && blockY == j && blockZ == k )
				{
					Stud stud = TileEntityBillund.getStud( world, s_hoverBrick.XOrigin, s_hoverBrick.YOrigin, s_hoverBrick.ZOrigin );
					if( stud != null )
					{
						return getIcon( stud.Colour );
					}
				}
			}
    	}
    	return s_transparentIcon;
    }

	@Override
    public Icon getIcon( int side, int damage )
    {
    	return s_transparentIcon;
	}
	
    @Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer entityplayer, int l, float m, float n, float o )
    { 	
    	return false;
    }
    
	@Override
	public TileEntity createNewTileEntity( World world )
	{
		return null;
	}
    
    @Override
    public TileEntity createTileEntity( World world, int metadata )
    {
    	return new TileEntityBillund();
    }

	@Override
	public void registerIcons( IconRegister iconRegister )
	{
		s_icons = new Icon[ StudColour.Count ];
		s_transparentIcon = iconRegister.registerIcon( "billund:transparent" );
		s_icons[ StudColour.Red ] = iconRegister.registerIcon( "billund:red" );
		s_icons[ StudColour.Green ] = iconRegister.registerIcon( "billund:green" );
		s_icons[ StudColour.Blue ] = iconRegister.registerIcon( "billund:blue" );
		s_icons[ StudColour.Orange ] = iconRegister.registerIcon( "billund:orange" );
		s_icons[ StudColour.Yellow ] = iconRegister.registerIcon( "billund:yellow" );
		s_icons[ StudColour.Brown ] = iconRegister.registerIcon( "billund:brown" );
		s_icons[ StudColour.LightGreen ] = iconRegister.registerIcon( "billund:lightGreen" );
		s_icons[ StudColour.Pink ] = iconRegister.registerIcon( "billund:pink" );
		s_icons[ StudColour.Purple ] = iconRegister.registerIcon( "billund:purple" );
		s_icons[ StudColour.White ] = iconRegister.registerIcon( "billund:white" );
		s_icons[ StudColour.LightGrey ] = iconRegister.registerIcon( "billund:lightGrey" );
		s_icons[ StudColour.DarkGrey ] = iconRegister.registerIcon( "billund:darkGrey" );
		s_icons[ StudColour.Black ] = iconRegister.registerIcon( "billund:black" );
	}
}
