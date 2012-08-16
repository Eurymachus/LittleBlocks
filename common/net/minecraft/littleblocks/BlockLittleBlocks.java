package net.minecraft.littleblocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class BlockLittleBlocks extends BlockContainer {

	// Eury START
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x,
			int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == LittleBlocksCore.littleBlocksID) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile.isEmpty()) {
				return super.removeBlockByPlayer(world, player, x, y, z);
			} else {
				if (world.getWorldInfo().getGameType() == EnumGameType.CREATIVE) {
					this.onBlockClicked(world, x, y, z, player);
					return false;
				}
				int[][][] content = tile.getContent();
				for (int x1 = 0; x1 < content.length; x1++) {
					for (int y1 = 0; y1 < content[x1].length; y1++) {
						for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
							if (content[x1][y1][z1] > 0
									&& Block.blocksList[content[x1][y1][z1]] != null) {
								int idDropped = Block.blocksList[content[x1][y1][z1]]
										.idDropped(tile.getMetadata(
												this.xSelected, this.ySelected,
												this.zSelected), world.rand, 0);
								int quantityDropped = Block.blocksList[content[x1][y1][z1]]
										.quantityDropped(world.rand);
								if (idDropped > 0 && quantityDropped > 0) {
									this.dropLittleBlockAsItem_do(
											world,
											x,
											y,
											z,
											new ItemStack(idDropped,
													quantityDropped,
													tile.getMetadata(
															this.xSelected,
															this.ySelected,
															this.zSelected)));
								}
							}
						}
					}
				}
			}
		}
		return world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z,
			EntityPlayer entityplayer) {
		LittleBlocks.blockClicked(world, x, y, z, entityplayer, this);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer entityplayer, int i, float j, float k, float l) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			int itemID = entityplayer.getCurrentEquippedItem().itemID;
			Block[] blocks = Block.blocksList;
			for (int blockID = 0; blockID < blocks.length; i++) {
				if (blocks[blockID] != null && blocks[blockID].blockID == itemID) {
					Block theBlock = blocks[blockID];
					if (theBlock.hasTileEntity(0)) {
						return false;
					}
				}
			}
		}
		return LittleBlocks.blockActivated(world, x, y, z, entityplayer, this, j, k, l);
	}

	public void dropLittleBlockAsItem_do(World world, int x, int y, int z,
			ItemStack itemStack) {
		this.dropBlockAsItem_do(world, x, y, z, itemStack);
	}

	// Eury END

	public BlockLittleBlocks(int id, BaseMod mod) {
		super(id, Material.wood);
		setHardness(2F);
		setRequiresSelfNotify();
		renderType = ModLoader.getUniqueBlockModelID(mod, false);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLittleBlocks(world);
	}

	@Override
	public int getRenderType() {
		return renderType;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i,
			int j, int k) {
		return null;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
	}

	public void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
		float m = TileEntityLittleBlocks.size;
		if (xSelected == -10) {
			setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile.getContent(xSelected, ySelected, zSelected) <= 0) {
				setBlockBounds(xSelected / m, ySelected / m, zSelected / m,
						(xSelected + 1) / m, (ySelected + 1) / m,
						(zSelected + 1) / m);
			} else {
				Block block = Block.blocksList[tile.getContent(xSelected,
						ySelected, zSelected)];
				block.setBlockBoundsBasedOnState(tile.getLittleWorld(),
						(x << 3) + xSelected, (y << 3) + ySelected, (z << 3)
								+ zSelected);
				AxisAlignedBB bound = AxisAlignedBB.getBoundingBox(
						(xSelected + block.minX) / m, (ySelected + block.minY)
								/ m, (zSelected + block.minZ) / m,
						(xSelected + block.maxX) / m, (ySelected + block.maxY)
								/ m, (zSelected + block.maxZ) / m);
				setBlockBounds((float) bound.minX, (float) bound.minY,
						(float) bound.minZ, (float) bound.maxX,
						(float) bound.maxY, (float) bound.maxZ);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addCollidingBlockToList(World world, int x, int y, int z,
			AxisAlignedBB axisalignedbb, List list, Entity entity) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		int[][][] content = tile.getContent();
		float m = TileEntityLittleBlocks.size;

		for (int x1 = 0; x1 < content.length; x1++) {
			for (int y1 = 0; y1 < content[x1].length; y1++) {
				for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
					if (content[x1][y1][z1] > 0) {
						Block block = Block.blocksList[content[x1][y1][z1]];
						setBlockBounds((float) (x + block.minX) / m,
								(float) (y1 + block.minY) / m,
								(float) (z1 + block.minZ) / m,
								(float) (x1 + block.maxX) / m,
								(float) (y1 + block.maxY) / m,
								(float) (z1 + block.maxZ) / m);
						super.addCollidingBlockToList(world, x, y, z, axisalignedbb, list, entity);
					}
				}
			}
		}
		setBlockBoundsBasedOnSelection(world, x, y, z);
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j,
			int k, Vec3 player, Vec3 view) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(i, j, k);

		player = player.addVector(-i, -j, -k);
		view = view.addVector(-i, -j, -k);
		if (tile == null) {
			return null;
		}

		int[][][] content = tile.getContent();
		float m = TileEntityLittleBlocks.size;

		List<Object[]> returns = new ArrayList<Object[]>();

		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						Block block = Block.blocksList[content[x][y][z]];
						block.collisionRayTrace(tile.getLittleWorld(), (i << 3)
								+ x, (j << 3) + y, (k << 3) + z, player, view);
						Object[] ret = rayTraceBound(
								AxisAlignedBB.getBoundingBox((x + block.minX)
										/ m, (y + block.minY) / m,
										(z + block.minZ) / m, (x + block.maxX)
												/ m, (y + block.maxY) / m,
										(z + block.maxZ) / m), i, j, k, player,
								view);
						if (ret != null) {
							returns.add(new Object[] { ret[0], ret[1], ret[2],
									x, y, z });
						}
					}
				}
			}
		}

		int max = TileEntityLittleBlocks.size;

		int block = world.getBlockId(i, j - 1, k); // DOWN
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int x = 0; x < max; x++) {
				for (int z = 0; z < max; z++) {
					int y = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		block = world.getBlockId(i, j + 1, k); // UP
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int x = 0; x < max; x++) {
				for (int z = 0; z < max; z++) {
					int y = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		block = world.getBlockId(i - 1, j, k); // -X
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					int x = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		block = world.getBlockId(i + 1, j, k); // +X
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					int x = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		block = world.getBlockId(i, j, k - 1); // -Z
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int x = 0; x < max; x++) {
					int z = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		block = world.getBlockId(i, j, k + 1); // +Z
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int x = 0; x < max; x++) {
					int z = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(x
							/ m, y / m, z / m, (x + 1) / m, (y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] { ret[0], ret[1], ret[2], x,
								y, z });
					}
				}
			}
		}

		if (!returns.isEmpty()) {
			Object[] min = null;
			double distMin = 0;
			for (Object[] ret : returns) {
				double dist = (Double) ret[2];
				if (min == null || dist < distMin) {
					distMin = dist;
					min = ret;
				}
			}
			if (min != null) {
				side = (Byte) min[1];
				xSelected = (Integer) min[3];
				ySelected = (Integer) min[4];
				zSelected = (Integer) min[5];
				if (tile.getContent(xSelected, ySelected, zSelected) > 0) {
					Block.blocksList[tile.getContent(xSelected, ySelected,
							zSelected)].collisionRayTrace(
							tile.getLittleWorld(), (i << 3) + xSelected,
							(j << 3) + ySelected, (k << 3) + zSelected, player,
							view);
				}
				setBlockBoundsBasedOnSelection(world, i, j, k);

				return new MovingObjectPosition(i, j, k, (Byte) min[1],
						((Vec3) min[0]).addVector(i, j, k));
			}
		}
		xSelected = -10;
		ySelected = -10;
		zSelected = -10;
		side = -1;
		setBlockBoundsBasedOnSelection(world, i, j, k);

		return null;
	}

	private Object[] rayTraceBound(AxisAlignedBB bound, int i, int j, int k,
			Vec3 player, Vec3 view) {
		Vec3 vec3d2 = player.getIntermediateWithXValue(view, bound.minX);
		Vec3 vec3d3 = player.getIntermediateWithXValue(view, bound.maxX);
		Vec3 vec3d4 = player.getIntermediateWithYValue(view, bound.minY);
		Vec3 vec3d5 = player.getIntermediateWithYValue(view, bound.maxY);
		Vec3 vec3d6 = player.getIntermediateWithZValue(view, bound.minZ);
		Vec3 vec3d7 = player.getIntermediateWithZValue(view, bound.maxZ);
		if (!isVecInsideYZBounds(bound, vec3d2)) {
			vec3d2 = null;
		}
		if (!isVecInsideYZBounds(bound, vec3d3)) {
			vec3d3 = null;
		}
		if (!isVecInsideXZBounds(bound, vec3d4)) {
			vec3d4 = null;
		}
		if (!isVecInsideXZBounds(bound, vec3d5)) {
			vec3d5 = null;
		}
		if (!isVecInsideXYBounds(bound, vec3d6)) {
			vec3d6 = null;
		}
		if (!isVecInsideXYBounds(bound, vec3d7)) {
			vec3d7 = null;
		}
		Vec3 vec3d8 = null;
		if (vec3d2 != null
				&& (vec3d8 == null || player.distanceTo(vec3d2) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d2;
		}
		if (vec3d3 != null
				&& (vec3d8 == null || player.distanceTo(vec3d3) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d3;
		}
		if (vec3d4 != null
				&& (vec3d8 == null || player.distanceTo(vec3d4) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d4;
		}
		if (vec3d5 != null
				&& (vec3d8 == null || player.distanceTo(vec3d5) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d5;
		}
		if (vec3d6 != null
				&& (vec3d8 == null || player.distanceTo(vec3d6) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d6;
		}
		if (vec3d7 != null
				&& (vec3d8 == null || player.distanceTo(vec3d7) < player
						.distanceTo(vec3d8))) {
			vec3d8 = vec3d7;
		}
		if (vec3d8 == null) {
			return null;
		}
		byte side = -1;
		if (vec3d8 == vec3d2) {
			side = 4;
		}
		if (vec3d8 == vec3d3) {
			side = 5;
		}
		if (vec3d8 == vec3d4) {
			side = 0;
		}
		if (vec3d8 == vec3d5) {
			side = 1;
		}
		if (vec3d8 == vec3d6) {
			side = 2;
		}
		if (vec3d8 == vec3d7) {
			side = 3;
		}
		return new Object[] { vec3d8, side, player.distanceTo(vec3d8) };
	}

	private boolean isVecInsideYZBounds(AxisAlignedBB bound, Vec3 vec3d) {
		if (vec3d == null) {
			return false;
		} else {
			return vec3d.yCoord >= bound.minY && vec3d.yCoord <= bound.maxY
					&& vec3d.zCoord >= bound.minZ && vec3d.zCoord <= bound.maxZ;
		}
	}

	private boolean isVecInsideXZBounds(AxisAlignedBB bound, Vec3 vec3d) {
		if (vec3d == null) {
			return false;
		} else {
			return vec3d.xCoord >= bound.minX && vec3d.xCoord <= bound.maxX
					&& vec3d.zCoord >= bound.minZ && vec3d.zCoord <= bound.maxZ;
		}
	}

	private boolean isVecInsideXYBounds(AxisAlignedBB bound, Vec3 vec3d) {
		if (vec3d == null) {
			return false;
		} else {
			return vec3d.xCoord >= bound.minX && vec3d.xCoord <= bound.maxX
					&& vec3d.yCoord >= bound.minY && vec3d.yCoord <= bound.maxY;
		}
	}

	@Override
	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k,
			int l) {
		if (super.isPoweringTo(iblockaccess, i, j, k, l)) {
			return true;
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) iblockaccess
					.getBlockTileEntity(i, j, k);

			int[][][] content = tile.getContent();
			int maX = 8, maY = 8, maZ = 8;
			int startX = 0, startY = 0, startZ = 0;

			switch (l) {
			case 1:
				maY = 1;
				break;

			case 0:
				startY = 7;
				break;

			case 3:
				maZ = 1;
				break;

			case 2:
				startZ = 7;
				break;

			case 5:
				maX = 1;
				break;

			case 4:
				startX = 7;
				break;
			}

			for (int x = startX; x < maX; x++) {
				for (int y = startY; y < maY; y++) {
					for (int z = startZ; z < maZ; z++) {
						if (content[x][y][z] > 0) {
							if (Block.blocksList[content[x][y][z]]
									.isPoweringTo(tile.getLittleWorld(),
											(i << 3) + x, (j << 3) + y,
											(k << 3) + z, l)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		super.onNeighborBlockChange(world, i, j, k, l);
		if (updateEveryone) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(i, j, k);
			int[][][] content = tile.getContent();
			int maX = 8, maY = 8, maZ = 8;
			int startX = 0, startY = 0, startZ = 0;
			for (int side = 0; side < 6; side++) {
				switch (side) {
				case 0:
					maY = 1;
					break;

				case 1:
					startY = 7;
					break;

				case 2:
					maZ = 1;
					break;

				case 3:
					startZ = 7;
					break;

				case 4:
					maX = 1;
					break;

				case 5:
					startX = 7;
					break;
				}

				for (int x = startX; x < maX; x++) {
					for (int y = startY; y < maY; y++) {
						for (int z = startZ; z < maZ; z++) {
							if (content[x][y][z] > 0) {
								Block.blocksList[content[x][y][z]]
										.onNeighborBlockChange(
												tile.getLittleWorld(), (i << 3)
														+ x, (j << 3) + y,
												(k << 3) + z, l);
							}
						}
					}
				}
			}
		}
	}

	public int xSelected = -10, ySelected = -10, zSelected = -10, side = -1;

	public boolean updateEveryone = true;

	private final int renderType;
}
