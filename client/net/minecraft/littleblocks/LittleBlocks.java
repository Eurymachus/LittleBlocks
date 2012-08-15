package net.minecraft.littleblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.littleblocks.network.PacketHandler;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.PlayerController;
import net.minecraft.src.PlayerControllerCreative;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import net.minecraft.src.mod_LittleBlocks;

import org.lwjgl.opengl.GL11;

public class LittleBlocks {
	public static boolean blockActivated(World world, int x, int y, int z,
			EntityPlayer entityplayer, BlockLittleBlocks block) {
		PacketHandler.Output.blockUpdate(world, entityplayer, x, y, z, block,
				LittleBlocksCore.blockActivateCommand);

		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		PlayerController playerControler = ModLoader.getMinecraftInstance().playerController;
		if (playerControler.onPlayerRightClick(entityplayer,
				tile.getLittleWorld(), entityplayer.getCurrentEquippedItem(),
				(x << 3) + block.xSelected, (y << 3) + block.ySelected,
				(z << 3) + block.zSelected, block.side)) {
			return true;
		} else if (entityplayer.getCurrentEquippedItem() != null
				&& entityplayer.getCurrentEquippedItem().getItem() instanceof ItemBucket) {
			playerControler.sendUseItem(entityplayer, tile.getLittleWorld(),
					entityplayer.getCurrentEquippedItem());
			return true;
		}
		return false;
	}

	public static void blockClicked(World world, int x, int y, int z,
			EntityPlayer entityplayer, BlockLittleBlocks block) {
		PacketHandler.Output.blockUpdate(world, entityplayer, x, y, z, block,
				LittleBlocksCore.blockClickCommand);
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		int content = tile.getContent(block.xSelected, block.ySelected,
				block.zSelected);
		if (content > 0 && Block.blocksList[content] != null) {
			if (!(ModLoader.getMinecraftInstance().playerController instanceof PlayerControllerCreative)) {
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

	public static int getLightBrightnessForSkyBlocks(World realWorld, int i,
			int j, int k, int l) {
		return realWorld.getLightBrightnessForSkyBlocks(i >> 3, j >> 3, k >> 3,
				l);
	}

	public static float getBrightness(World realWorld, int i, int j, int k,
			int l) {
		return realWorld.getBrightness(i, j, k, l);
	}

	public static boolean optifine;
	public static String mode = "Client";
	public static String minecraftDir = Minecraft.getMinecraftDir().toString();;

	public static void initialize() {
		ModLoader.setInGameHook(mod_LittleBlocks.instance, true, true);
		LittleBlocksCore.addItems();
		LittleBlocksCore.addNames();
		LittleBlocksCore.addRecipes();
		ModLoader.registerTileEntity(TileEntityLittleBlocks.class,
				"littleBlocks", new LittleBlocksRenderer());

		try {
			mod_LittleBlocks.instance.getClass().getClassLoader()
					.loadClass("TextureHDCompassFX");
			optifine = true;
		} catch (ClassNotFoundException e) {
			optifine = false;
		}
	}

	public static boolean renderWorldBlock(RenderBlocks renderblocks,
			IBlockAccess iblockaccess, int x, int y, int z, Block block, int l) {
		if (block.blockID == LittleBlocksCore.littleBlocksID && !optifine) {
			Tessellator tessellator = Tessellator.instance;

			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) iblockaccess
					.getBlockTileEntity(x, y, z);
			if (tile == null) {
				return false;
			}

			tessellator.draw();

			GL11.glPushMatrix();

			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);

			GL11.glTranslated(xS, yS, zS);

			GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);

			float scale = 1 / 8F;
			GL11.glScalef(scale, scale, scale);

			GL11.glTranslated(-xS, -yS, -zS);

			int[][][] content = tile.getContent();

			RenderBlocks renderBlocks = new RenderBlocks(tile.getLittleWorld());

			tessellator.startDrawingQuads();
			for (int x1 = 0; x1 < content.length; x1++) {
				for (int y1 = 0; y1 < content[x1].length; y1++) {
					for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
						if (content[x1][y1][z1] > 0) {
							renderBlocks
									.renderBlockByRenderType(
											Block.blocksList[content[x1][y1][z1]],
											(x << 3) + x1, (y << 3) + y1,
											(z << 3) + z1);
						}
					}
				}
			}
			tessellator.draw();

			GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);

			GL11.glPopMatrix();

			tessellator.startDrawingQuads();

			return true;
		}
		return false;
	}

	private void renderLittleBlock(int x, int y, int z, int id,
			int littleMetadata, TileEntityLittleBlocks tile) {
		if (id == 0) {
			return;
		}

		Tessellator tessellator = Tessellator.instance;

		Block block = Block.blocksList[id];

		double minX = x + block.minX, maxX = x + block.maxX;
		double minY = y + block.minY, maxY = y + block.maxY;
		double minZ = z + block.minZ, maxZ = z + block.maxZ;

		int texture, u, v;
		double minU, minV, maxU, maxV;

		if (tile.getContent(x, y + 1, z) <= 0
				|| !Block.blocksList[tile.getContent(x, y + 1, z)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					1, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;
			minU = ((texture % 16) + minX / 8) / 16F;
			maxU = ((texture % 16) + maxX / 8) / 16F;
			minV = ((texture / 16) + minZ / 8) / 16F;
			maxV = ((texture / 16) + maxZ / 8) / 16F;
			tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV); // TOP
																		// FACE
			tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, maxV);
			tessellator.addVertexWithUV(minX, maxY, minZ, minU, maxV);
			tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
		}

		if (tile.getContent(x + 1, y, z) <= 0
				|| !Block.blocksList[tile.getContent(x + 1, y, z)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					2, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;

			minU = ((texture % 16) + minX / 8) / 16F;
			maxU = ((texture % 16) + maxX / 8) / 16F;
			minV = ((texture / 16) + minZ / 8) / 16F;
			maxV = ((texture / 16) + maxZ / 8) / 16F;

			tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV); // SOUTH
			tessellator.addVertexWithUV(maxX, minY, minZ, minU, maxV);
			tessellator.addVertexWithUV(maxX, maxY, minZ, minU, minV);
			tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
		}

		if (tile.getContent(x - 1, y, z) <= 0
				|| !Block.blocksList[tile.getContent(x - 1, y, z)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					3, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;

			minU = ((texture % 16) + minZ / 8) / 16F;
			maxU = ((texture % 16) + maxZ / 8) / 16F;
			minV = ((texture / 16) + minY / 8) / 16F;
			maxV = ((texture / 16) + maxY / 8) / 16F;

			tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV); // NORTH
			tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
			tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
			tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
		}

		if (tile.getContent(x, y, z + 1) <= 0
				|| !Block.blocksList[tile.getContent(x, y, z + 1)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					4, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;

			minU = ((texture % 16) + minX / 8) / 16F;
			maxU = ((texture % 16) + maxX / 8) / 16F;
			minV = ((texture / 16) + minY / 8) / 16F;
			maxV = ((texture / 16) + maxY / 8) / 16F;

			tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV); // EAST
			tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
			tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
			tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
		}

		if (tile.getContent(x, y, z - 1) <= 0
				|| !Block.blocksList[tile.getContent(x, y, z - 1)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					5, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;

			minU = ((texture % 16) + minX / 8) / 16F;
			maxU = ((texture % 16) + maxX / 8) / 16F;
			minV = ((texture / 16) + minY / 8) / 16F;
			maxV = ((texture / 16) + maxY / 8) / 16F;

			tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV); // WEST
			tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
			tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
			tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
		}

		if (tile.getContent(x, y - 1, z) <= 0
				|| !Block.blocksList[tile.getContent(x, y - 1, z)]
						.isOpaqueCube()) {
			texture = Block.blocksList[id].getBlockTextureFromSideAndMetadata(
					0, littleMetadata);
			u = (texture & 0xF) << 4;
			v = texture & 0xF0;

			minU = ((texture % 16) + minZ / 8) / 16F;
			maxU = ((texture % 16) + maxZ / 8) / 16F;
			minV = ((texture / 16) + minY / 8) / 16F;
			maxV = ((texture / 16) + maxY / 8) / 16F;

			tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);// BOTTOM
			tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
			tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, minV);
			tessellator.addVertexWithUV(minX, minY, maxZ, minU, minV);
		}
	}

	/**
	 * Retrieves the world object without parameters
	 * 
	 * @return the world
	 */
	public static World getWorld() {
		return ModLoader.getMinecraftInstance().theWorld;
	}

	/**
	 * Retrieves the world object
	 * 
	 * @param network
	 *            the Network manager (used for Server)
	 * @return the world
	 */
	public static World getWorld(NetworkManager network) {
		return getWorld();
	}

	/**
	 * Retrieves the player object
	 * 
	 * @return the player
	 */
	public static EntityPlayer getPlayer() {
		return ModLoader.getMinecraftInstance().thePlayer;
	}

	/**
	 * Retrieves the player object
	 * 
	 * @param network
	 *            the Network manager (used for Server)
	 * @return the player
	 */
	public static EntityPlayer getPlayer(NetworkManager network) {
		return getPlayer();
	}
}
