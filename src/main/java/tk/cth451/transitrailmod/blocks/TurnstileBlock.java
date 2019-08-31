package tk.cth451.transitrailmod.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tk.cth451.transitrailmod.TransitRailMod;
import tk.cth451.transitrailmod.blocks.prototype.CustomDirectionBlock;
import tk.cth451.transitrailmod.enums.EnumPassingDirection;

public class TurnstileBlock extends CustomDirectionBlock{
	
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	// whether the turnstile has processed the ticket and open the gate
	public static final PropertyEnum PASSING = PropertyEnum.create("passing", EnumPassingDirection.class);
	
	public TurnstileBlock(Material materialIn) {
		super(Material.IRON);
		this.setUnlocalizedName("turnstile_block");
		this.setCreativeTab(TransitRailMod.tabTransitRail);
		this.setTickRandomly(true);
		this.setDefaultState(this.getDefaultState()
				.withProperty(ACTIVE, false)
				.withProperty(PASSING, EnumPassingDirection.INSIDE)
				.withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public boolean isTranslucent(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
	@Override
	public int tickRate(World worldIn) {
		return 20;
	}
	
	// Block State
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVE, PASSING, FACING);
	}
	
	// meta: 3211
	// 11: facing
	// 2: active
	// 3: passing
	@Override
	public int getMetaFromState(IBlockState state) {
		int mFacing = state.getValue(FACING).getHorizontalIndex();
		int mActive = state.getValue(ACTIVE) ? 1 : 0;
		int mPassing = ((EnumPassingDirection) state.getValue(PASSING)).toMeta();
		return (mPassing >> 3) + (mActive >> 2) + mFacing;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing pFacing = EnumFacing.getHorizontal(meta % 4);
		boolean pActive = (meta % 8) / 4 > 0;
		EnumPassingDirection pPassing = EnumPassingDirection.fromMeta(meta / 8);
		return getDefaultState()
				.withProperty(ACTIVE, pActive)
				.withProperty(PASSING, pPassing)
				.withProperty(FACING, pFacing);
	}
	
	// Interactions
	
	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (state.getValue(ACTIVE)){
			worldIn.setBlockState(pos, state.withProperty(ACTIVE, false));
		}
	}
	
	// Appearance
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH :
				return new AxisAlignedBB(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			case EAST :
				return new AxisAlignedBB(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
			case SOUTH :
				return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
			default : //WEST
				return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote) {
			// make a non-mutable copy
			pos = new BlockPos(pos);
			AxisAlignedBB spaceToCheck = getSpaceToCheck(worldIn, pos);
			List<EntityLivingBase> list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, spaceToCheck);
			
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
			if (list.isEmpty()){
				if (state.getValue(ACTIVE)){
					worldIn.setBlockState(pos, state.withProperty(ACTIVE, false));
				}
			}
		}
	}
	
	protected AxisAlignedBB getBarBoundsBasedOnState(IBlockState blockState, BlockPos pos) {
		AxisAlignedBB bbFromBounds = null;
		if (blockState.getValue(ACTIVE)) {
			return NULL_AABB;
		} else {
			switch (blockState.getValue(FACING)) {
				case NORTH :
				case SOUTH :
					bbFromBounds = new AxisAlignedBB(0.0F, 0.4375F, 0.4375F, 1.0F, 1.5F, 0.5625F);
					break;
				default : // EAST WEST
					bbFromBounds = new AxisAlignedBB(0.4375F, 0.4375F, 0.0F, 0.5625F, 1.5F, 1.0F);
			}
			return bbFromBounds;
		}
	}

	private AxisAlignedBB getBBFromBounds (BlockPos pos, double x1, double y1, double z1, double x2, double y2, double z2) {
		return new AxisAlignedBB(pos.getX() + x1, pos.getY() + y1, pos.getZ() + z1, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2);
	}
	
	protected AxisAlignedBB getSpaceToCheck(World worldIn, BlockPos pos){
		return new AxisAlignedBB(
				pos.getX(),
				pos.getY(),
				pos.getZ(),
				pos.getX() + 1.0,
				pos.getY() + 1.0,
				pos.getZ() + 1.0);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return this.getBarBoundsBasedOnState(state, pos);
	}

}
