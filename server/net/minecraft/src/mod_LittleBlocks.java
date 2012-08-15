package net.minecraft.src;

import net.minecraft.littleblocks.BlockLittleBlocks;
import net.minecraft.littleblocks.LittleBlocks;
import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.server.MinecraftServer;

public class mod_LittleBlocks extends BaseMod {
	public static BaseMod instance;
	public final BlockLittleBlocks littleBlocks;

	@Override
	public void modsLoaded() {
		if (ModLoader.isModLoaded("mod_MinecraftForge")) {
			if (!LittleBlocksCore.initialized) {
				LittleBlocksCore.initialize();
			}
		}
	}

	public mod_LittleBlocks() {
		instance = this;
		littleBlocks = (BlockLittleBlocks) new BlockLittleBlocks(
				LittleBlocksCore.littleBlocksID, instance)
				.setBlockName("littleBlocks");
		ModLoader.registerBlock(littleBlocks);
		ModLoader.addName(littleBlocks, "Little Blocks Block");
		ModLoader.addRecipe(new ItemStack(littleBlocks), new Object[] { // to be
																		// removed
																		// TODO
				"#", Character.valueOf('#'), Block.dirt });
	}

	@Override
	public boolean onTickInGame(MinecraftServer minecraft) {
		return LittleBlocks.onTickInGame(minecraft);
	}

	@Override
	public String getVersion() {
		return LittleBlocksCore.version;
	}

	@Override
	public void load() {
	}
}