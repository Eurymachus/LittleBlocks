package net.minecraft.littleblocks;

import java.net.URISyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.World;
import net.minecraft.src.mod_LittleBlocks;

public class LittleBlocks {

	public static String mode = "Server";
	public static World realWorld;
	public static String minecraftDir = getMinecraftDir();
	public static String getMinecraftDir() {
		try {
			String s = (net.minecraft.src.ModLoader.class)
					.getProtectionDomain().getCodeSource().getLocation()
					.toURI().getPath();
			return s.substring(0, s.lastIndexOf('/'));
		} catch (URISyntaxException urisyntaxexception) {
			return null;
		}
	}
	
	public static boolean blockActivated(World world, int x, int y, int z,
			EntityPlayer entityplayer, BlockLittleBlocks block) {
		if (block.xSelected == -10) {
			return true;
		}
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;

		ItemInWorldManager itemManager = player.itemInWorldManager;
		if (itemManager.activeBlockOrUseItem(entityplayer,
				tile.getLittleWorld(), entityplayer.getCurrentEquippedItem(),
				(x << 3) + block.xSelected, (y << 3) + block.ySelected,
				(z << 3) + block.zSelected, block.side)) {
			return true;
		} else if (entityplayer.getCurrentEquippedItem() != null
				&& entityplayer.getCurrentEquippedItem().getItem() instanceof ItemBucket) {
			itemManager.itemUsed(entityplayer, tile.getLittleWorld(),
					entityplayer.getCurrentEquippedItem());
			return true;
		}
		return false;
	}

	public static void blockClicked(World world, int x, int y, int z,
			EntityPlayer entityplayer, BlockLittleBlocks block) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;

		int content = tile.getContent(block.xSelected, block.ySelected,
				block.zSelected);
		if (content > 0 && Block.blocksList[content] != null) {
			if (player.itemInWorldManager.getGameType() == 0) {
				int idDropped = Block.blocksList[content].idDropped(tile
						.getMetadata(block.xSelected, block.ySelected,
								block.zSelected), world.rand, 0);
				int quantityDropped = Block.blocksList[content]
						.quantityDropped(world.rand);
				if (idDropped > 0 && quantityDropped > 0) {
					block.dropLittleBlockAsItem_do(
							world,
							x,
							y,
							z,
							new ItemStack(idDropped, quantityDropped, tile
									.getMetadata(block.xSelected,
											block.ySelected, block.zSelected)));
				}
			}
		}

		tile.setContent(block.xSelected, block.ySelected, block.zSelected, 0);
	}

	public static void initialize() {
		ModLoader.setInGameHook(mod_LittleBlocks.instance, true, true);
		LittleBlocksCore.addItems();
		LittleBlocksCore.addNames();
		LittleBlocksCore.addRecipes();
		ModLoader.registerTileEntity(TileEntityLittleBlocks.class,
				"littleBlocks");
	}

	public static boolean onTickInGame(MinecraftServer minecraft) {
		if (minecraft.getWorldManager(0) != null) {
			TileEntityLittleBlocks.getLittleWorld(minecraft.getWorldManager(0))
					.tickUpdates(false);
		}
		return true;
	}

	public static World getWorld(NetworkManager network) {
		NetServerHandler net = (NetServerHandler) network.getNetHandler();
		return net.getPlayerEntity().worldObj;
	}

	public static EntityPlayer getPlayer(NetworkManager network) {
		NetServerHandler net = (NetServerHandler) network.getNetHandler();
		return net.getPlayerEntity();
	}

	public static World getWorld() {
		if (realWorld != null)
			return realWorld;
		return null;
	}
}
