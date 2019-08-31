package tk.cth451.transitrailmod.items;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tk.cth451.transitrailmod.ModOptions;
import tk.cth451.transitrailmod.TransitRailMod;
import tk.cth451.transitrailmod.blocks.TurnstileBlock;
import tk.cth451.transitrailmod.enums.EnumPassingDirection;
import tk.cth451.transitrailmod.init.ModBlocks;

public class TrainTicket extends Item {
	
	public TrainTicket(){
		super();
		setUnlocalizedName("train_ticket");
		setCreativeTab(TransitRailMod.tabTransitRail);
		this.maxStackSize = 1;
		this.setMaxDamage(ModOptions.TICKET_MAX_USES);
	}
	
	// Appearance
	// transitrailmod.ticket.in_use
	// transitrailmod.ticket.not_in_use
	// transitrailmod.ticket.remaining_rides
	// transitrailmod.ticket.insufficient_balance
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		String usageLangKey = isTicketInUse(stack) ? "transitrailmod.ticket.in_use" : "transitrailmod.ticket.not_in_use";
		String usageToolTip = I18n.format(usageLangKey);
		tooltip.add(usageToolTip);
		
		int rides = getRidesRemaining(stack);
		String ridesToolTip = I18n.format("transitrailmod.ticket.remaining_rides");
		tooltip.add(ridesToolTip + ": " + rides);
		
		if (rides <= 0) {
			tooltip.add(I18n.format("transitrailmod.ticket.insufficient_balance"));
		}
	}
	
	// Interactions
	@Override
	public EnumActionResult onItemUse(ItemStack stack,
                                  EntityPlayer playerIn,
                                  World worldIn,
                                  BlockPos pos,
                                  EnumHand hand,
                                  EnumFacing facing,
                                  float hitX,
                                  float hitY,
                                  float hitZ)
	{
		IBlockState state = worldIn.getBlockState(pos);
		if (!(isTicketInUse(stack)) && !(getRidesRemaining(stack) > 0)) {
			return EnumActionResult.PASS;
		} else {
			if (state.getBlock() == ModBlocks.turnstile_block) {
				return this.processTicket(stack, playerIn, worldIn, pos, facing);
			} else {
				return EnumActionResult.PASS;
			}
		}
	}
	
	private EnumActionResult processTicket(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing facing) {
		IBlockState state = worldIn.getBlockState(pos);
		boolean usage = isTicketInUse(stack);
		boolean onTheRightSide = state.getValue(TurnstileBlock.FACING) == facing.getOpposite();
		EnumPassingDirection direction = (EnumPassingDirection) state.getValue(TurnstileBlock.PASSING);
		
		if (onTheRightSide) {
			if (usage == !direction.isInside()) {
				stack.damageItem(1, playerIn);
			}
			worldIn.setBlockState(pos, state.cycleProperty(TurnstileBlock.ACTIVE));
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.PASS;
		}
	}
	
	// damage to rides conversion
	// n rides = 2 n uses = 2 n - 1 max damage
	// On entry, the damage should be added 1 if and only if the damage is an even number.
	// On exit, the damage should be added 1 if and only if the damage is an odd number.
	public static boolean isTicketInUse(ItemStack stack) {
		return stack.getItemDamage() % 2 == 1;
	}
	
	public static int getRidesRemaining(ItemStack stack) {
		int dmg = stack.getItemDamage();
		return (ModOptions.TICKET_MAX_USES - dmg) / 2;
	}
	
	public static ItemStack setRidesRemaining(ItemStack stack, int ridesRemaining, boolean inUse) {
		int dmgAfter = (ModOptions.TICKET_MAX_USES - ridesRemaining * 2) - (inUse ? 1 : 0);
		stack.setItemDamage(dmgAfter);
		return stack;
	}
}
