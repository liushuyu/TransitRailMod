package tk.cth451.transitrailmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tk.cth451.transitrailmod.TransitRailMod;
import tk.cth451.transitrailmod.blocks.prototype.CustomDirectionBlock;
import tk.cth451.transitrailmod.init.ModBlocks;
import tk.cth451.transitrailmod.init.ModItems;

public class WirePanel extends CustomDirectionBlock {
	
	public static final PropertyBool LAMP = PropertyBool.create("lamp");
	public static final PropertyBool SHUT = PropertyBool.create("shut");
	
	public WirePanel(Material materialIn) {
		super(Material.iron);
		this.setUnlocalizedName("wire_panel");
		this.setCreativeTab(TransitRailMod.tabTransitRail);
		this.setDefaultState(this.getDefaultState()
				.withProperty(LAMP, false)
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(SHUT, false));
	}
	
	// Properties
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube()
    {
        return false;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		EnumFacing facing = (EnumFacing) worldIn.getBlockState(pos).getValue(FACING); 
		if (facing == EnumFacing.NORTH) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
		} else if (facing == EnumFacing.EAST) {
			this.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else if (facing == EnumFacing.SOUTH) {
			this.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
		} else { // WEST
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
		}
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	// Block States
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] {FACING, LAMP, SHUT});
	}
	
	// meta: 3211
	// 11: facing
	// 2: lamp
	// 3: shut
	@Override
	public int getMetaFromState(IBlockState state) {
		int mFacing = ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();
		int mLamp = (Boolean) state.getValue(LAMP) ? 1 : 0;
		int mShut = (Boolean) state.getValue(SHUT) ? 1 : 0;
		return mShut * 8 + mLamp * 4 + mFacing;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing pFacing = EnumFacing.getHorizontal(meta % 4);
		boolean pLamp = ((meta % 8) / 4) > 0;
		boolean pShut = meta / 8 > 0;
		return this.getDefaultState()
				.withProperty(FACING, pFacing)
				.withProperty(LAMP, pLamp)
				.withProperty(SHUT, pShut);
	}
	
	// Interactions
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		// set facing to the direction player is facing
		IBlockState state = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
		return this.getFacingState(state, placer)
				.withProperty(LAMP, this.checkLampPresent(worldIn, pos))
				.withProperty(SHUT, this.checkIsExtendingAbove(worldIn,pos));
	}
	
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		EnumFacing sideToProvide = ((EnumFacing) state.getValue(FACING)).getOpposite();
		state = state.withProperty(LAMP, this.checkLampPresent(worldIn, pos));
		worldIn.setBlockState(pos, state);
		worldIn.notifyBlockOfStateChange(pos.offset(sideToProvide), this);
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		if (this.checkLampPresent(worldIn, pos) && !((Boolean) state.getValue(SHUT))) {
			EnumFacing sideToProvide = ((EnumFacing) state.getValue(FACING));
			return sideToProvide == side ? 15 : 0;
		} else {
			return 0;
		}
	}
	
	protected boolean checkLampPresent(IBlockAccess worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.fluorescent_lamp ? true : worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.noise_barrier_with_lamp;
	}
	
	protected boolean checkIsExtendingAbove(IBlockAccess worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.wire_panel;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (playerIn.getHeldItem() != null) {
			if (playerIn.getHeldItem().getItem() == ModItems.style_changer){
				worldIn.setBlockState(pos, state.cycleProperty(SHUT));
				return true;
			}
		}
		return false;
	}
}
