package net.minecraft.littleblocks;

import net.minecraft.littleblocks.network.PacketHandler;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.Entity;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;

public class LittleWorld extends World {

	private final World realWorld;

	public LittleWorld(World world, WorldProvider worldprovider) {
		// Server super(world.getSaveHandler(), "", new
		// WorldSettings(world.getWorldInfo().getSeed(),
		// world.getWorldInfo().getGameType(),
		// world.getWorldInfo().isMapFeaturesEnabled(),
		// world.getWorldInfo().isHardcoreModeEnabled(),
		// world.getWorldInfo().getTerrainType()), null);
		super(world, worldprovider);
		realWorld = world;
		worldInfo = world.getWorldInfo();
	}

	// Eury START
	public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
		return realWorld.getLightBrightnessForSkyBlocks(i >> 3, j >> 3, k >> 3,
				l);
	}

	public float getBrightness(int i, int j, int k, int l) {
		return realWorld.getBrightness(i >> 3, j >> 3, k >> 3, l);
	}

	// Eury END

	@Override
	protected void generateSpawnPoint() {
	}

	public boolean isOutdated(World world) {
		return realWorld != world;
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return 0;
		}
		if (y < 0) {
			return 0;
		}
		if (y >= 128 << 3) {
			return 0;
		} else {
			int id = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7)
					.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getContent(x & 7, y & 7, z & 7);
			} else {
				return id;
			}
		}
	}

	@Override
	public int getBlockLightValue(int i, int j, int k) {
		return realWorld.getBlockLightValue(i >> 3, j >> 3, k >> 3);
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		if (realWorld.isRemote) {
			return false;
		}
		entity.setPosition(entity.posX / 8, entity.posY / 8, entity.posZ / 8);
		// entity.motionX/=8;
		// entity.motionY/=8;
		// entity.motionZ/=8; Server
		entity.worldObj = realWorld;
		return realWorld.spawnEntityInWorld(entity);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return 0;
		}
		if (y < 0) {
			return 0;
		}
		if (y >= 128 << 3) {
			return 0;
		} else {
			int id = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7)
					.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			int metadata = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7)
					.getBlockMetadata((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getMetadata(x & 7, y & 7, z & 7);
			} else {
				return metadata;
			}
		}
	}

	@Override
	public int getHeight() {
		return super.getHeight() * 8;
	}

	@Override
	public boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LittleBlocksCore.littleBlocksID) {
				realWorld.setBlockWithNotify((x) >> 3, y >> 3, (z) >> 3,
						LittleBlocksCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
			if (currentId != id || currentData != metadata) {
				tile.setContent(x & 7, y & 7, z & 7, id, metadata);
				flag = true;
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return flag;
		}
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LittleBlocksCore.littleBlocksID) {
				realWorld.setBlockWithNotify((x) >> 3, y >> 3, (z) >> 3,
						LittleBlocksCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			if (currentId != id) {
				tile.setContent(x & 7, y & 7, z & 7, id);
				flag = true;
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return flag;
		}
	}

	@Override
	public boolean setBlockMetadata(int x, int y, int z, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
				if (currentData != metadata) {
					tile.setMetadata(x & 7, y & 7, z & 7, metadata);
				}
			} else {
				chunk.setBlockMetadata((x & 0x7f) >> 3, y >> 3,
						(z & 0x7f) >> 3, metadata);
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return true;
		}
	}

	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int i,
			int j, int k) {
		return 15;
	}

	@Override
	public boolean checkChunksExist(int x, int y, int z, int x2, int y2, int z2) {
		int xDiff = (x2 - x) >> 1, yDiff = (y2 - y) >> 1, zDiff = (z2 - z) >> 1;
		int xMid = (x + x2) >> 1, yMid = (y + y2) >> 1, zMid = (z + z2) >> 1;

		boolean flag = realWorld.checkChunksExist((xMid >> 3) - xDiff,
				(yMid >> 3) - yDiff, (zMid >> 3) - zDiff, (xMid >> 3) + xDiff,
				(yMid >> 3) + yDiff, (zMid >> 3) + zDiff);
		return flag;
	}

	@Override
	public void notifyBlocksOfNeighborChange(int i, int j, int k, int l) {
		notifyBlockOfNeighborChange(i - 1, j, k, l);
		notifyBlockOfNeighborChange(i + 1, j, k, l);
		notifyBlockOfNeighborChange(i, j - 1, k, l);
		notifyBlockOfNeighborChange(i, j + 1, k, l);
		notifyBlockOfNeighborChange(i, j, k - 1, l);
		notifyBlockOfNeighborChange(i, j, k + 1, l);
	}

	private void notifyBlockOfNeighborChange(int i, int j, int k, int l) {
		World world;
		if (realWorld.getBlockId(i >> 3, j >> 3, k >> 3) == LittleBlocksCore.littleBlocksID) {
			world = this;
		} else {
			i >>= 3;
			j >>= 3;
			k >>= 3;
			world = realWorld;
		}
		if (realWorld.editingBlocks || realWorld.isRemote || this.editingBlocks
				|| this.isRemote) {
			return;
		}
		// if(realWorld.editingBlocks || this.editingBlocks) {
		// return;
		// } Server
		Block block = Block.blocksList[world.getBlockId(i, j, k)];
		if (block != null) {
			block.onNeighborBlockChange(world, i, j, k, l);

			if (world == this) {
				realWorld.markBlockNeedsUpdate(i >> 3, j >> 3, k >> 3);
			}
		}
	}

	public void idModified(int x, int y, int z, int lastId, int newId) {
		if (lastId != 0) {
			Block.blocksList[lastId].onBlockRemoval(this, x, y, z);
		}
		this.updateAllLightTypes(x, y, z);
		if (newId != 0) {
			if (!this.isRemote) {
				Block.blocksList[newId].onBlockAdded(this, x, y, z);
			}
		}

		PacketHandler.Output.idModified(x, y, z, lastId, newId, this);

		notifyBlockChange(x, y, z, newId);
	}

	public void metadataModified(int x, int y, int z, int lastMetadata,
			int newMetadata) {
		PacketHandler.Output.metadataModified(this, x, y, z, lastMetadata,
				newMetadata);
	}

	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return null;
		}
		if (y < 0) {
			return null;
		}
		if (y >= 256 << 3) {
			return null;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getTileEntity(x & 7, y & 7, z & 7);
			} else {
				return realWorld.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			}
		}
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileEntity) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= 256 << 3) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.setTileEntity(x & 7, y & 7, z & 7, tileEntity);
			} else {
				realWorld
						.setBlockTileEntity(x >> 3, y >> 3, z >> 3, tileEntity);
			}
			return;
		}
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
				|| z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= 256 << 3) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LittleBlocksCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.removeTileEntity(x & 7, y & 7, z & 7);
			}
		}
	}

	public void markBlockNeedsUpdate(int i, int j, int k) {
		realWorld.markBlockNeedsUpdate(i >> 3, j >> 3, k >> 3);
	}

	@Override
	public void playSoundEffect(double x, double y, double z, String s,
			float f, float f1) {
		realWorld.playSoundEffect(x / 8, y / 8, z / 8, s, f, f1);
	}

	@Override
	public void playRecord(String s, int x, int y, int z) {
		realWorld.playRecord(s, x, y, z);
	}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		realWorld.playAuxSFX(x / 8, y / 8, z / 8, l, i1);
	}

	@Override
	public void spawnParticle(String s, double x, double y, double z,
			double d3, double d4, double d5) {
		realWorld.spawnParticle(s, x / 8, y / 8, z / 8, d3, d4, d5);
	}

	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3D vec3d, Vec3D vec3d1,
			boolean flag, boolean flag1) {
		vec3d.xCoord *= 8;
		vec3d.yCoord *= 8;
		vec3d.zCoord *= 8;

		vec3d1.xCoord *= 8;
		vec3d1.yCoord *= 8;
		vec3d1.zCoord *= 8;
		return super.rayTraceBlocks_do_do(vec3d, vec3d1, flag, flag1);
	}

	public static int xx = 0, yy = 0, zz = 0;
}
