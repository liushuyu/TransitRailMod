package tk.cth451.transitrailmod.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;

// []
// 1
// 1 = Ticket out
public class InventoryTicketMachineOutput implements IInventory {
	
	private final ItemStack[] invStack = new ItemStack[1];

	@Override
	public String getName() {
		return "TicketMachineOutput";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		// TODO: is this correct?
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return invStack[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (invStack[index] != null) {
			ItemStack ret = invStack[index];
			invStack[index] = null;
			return ret;
		} else {
			return null;
		}
	}

//	@Override
//	public ItemStack getStackInSlotOnClosing(int index) {
//		if (invStack[index] != null) {
//			ItemStack ret = invStack[index];
//			invStack[index] = null;
//			return ret;
//		} else {
//			return null;
//		}
//	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		invStack[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		Arrays.fill(invStack, null);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

}
