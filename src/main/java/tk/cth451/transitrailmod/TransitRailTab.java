package tk.cth451.transitrailmod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import tk.cth451.transitrailmod.init.ModBlocks;

public class TransitRailTab extends CreativeTabs {

	public TransitRailTab(String label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack getTabIconItem() {
		// TODO Auto-generated method stub
		return new ItemStack(Item.getItemFromBlock(ModBlocks.logo_block));
	}
	
}
