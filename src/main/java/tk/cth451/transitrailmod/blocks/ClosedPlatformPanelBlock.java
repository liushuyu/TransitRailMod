package tk.cth451.transitrailmod.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClosedPlatformPanelBlock extends Block{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool UPPER = PropertyBool.create("upper");
	
	public ClosedPlatformPanelBlock(Material materialIn) {
		super(Material.glass);
		this.setUnlocalizedName("closed_platform_panel_block");
		this.setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(UPPER, false));
	}
	
	// Properties
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
		EnumFacing facing = (EnumFacing) worldIn.getBlockState(pos).getValue(FACING);
		if (facing == EnumFacing.NORTH) {
			this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.25F);
		} else if (facing == EnumFacing.EAST) {
			this.setBlockBounds(0.75F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
		} else if (facing == EnumFacing.SOUTH) {
			this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 0.875F);
		} else if (facing == EnumFacing.WEST) {
			this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
		}
    }
	
	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list,
			Entity collidingEntity) {
		this.setBlockBoundsBasedOnState(worldIn, pos);
		super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
	}
	
	@Override
	public boolean isTranslucent() {
		return true;
	}
	
	@Override
	public int getMobilityFlag()
    {
        return 1;
    }
	
	@Override
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public boolean isSolidFullCube()
    {
        return false;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }
	
	// Block State Methods
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] {FACING, UPPER});
	}
	

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(UPPER, isUpper(worldIn, pos));
	}
	
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}
	
	// Interactions
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		// set facing to the direction player is facing
		IBlockState state = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
		EnumFacing thisFacing = placer.getHorizontalFacing();
		// check if the block below is a platform panel
		return this.getActualState(state, worldIn, pos).withProperty(FACING, thisFacing);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (isUpper(world, pos)) {
			world.setBlockToAir(pos.up());
			world.setBlockToAir(pos.down());
		} else {
			world.setBlockToAir(pos.up());
			world.setBlockToAir(pos.up(2));
		}
		world.setBlockToAir(pos);
		return true;
	}
	
	public boolean isUpper(IBlockAccess worldIn, BlockPos pos){
		return worldIn.getBlockState(pos.down()).getBlock().equals(this);
	}
}
