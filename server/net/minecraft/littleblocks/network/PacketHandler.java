package net.minecraft.littleblocks.network;

import net.minecraft.littleblocks.BlockLittleBlocks;
import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.littleblocks.LittleWorld;
import net.minecraft.littleblocks.TileEntityLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntity;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntityLB;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketUpdate;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_LittleBlocks;
import net.minecraftforge.common.DimensionManager;

public class PacketHandler {
	public static class Output {

		public static void blockUpdate(World world, EntityPlayer entityplayer,
				int x, int y, int z, BlockLittleBlocks block, String command) {
			if (world.isRemote) {
				/*
				 * Packet230ModLoader packet = new Packet230ModLoader();
				 * packet.dataInt = new int[]{0, x, y, z, xSelected, ySelected,
				 * zSelected, side};
				 * ModLoaderMp.sendPacket(mod_LittleBlocks.instance, packet);
				 * 
				 * Packet230ModLoader packet = new Packet230ModLoader();
				 * packet.dataInt = new int[]{1, x, y, z, xSelected, ySelected,
				 * zSelected, side};
				 * ModLoaderMp.sendPacket(mod_LittleBlocks.instance, packet);
				 */
				PacketLittleBlocks packetLB = new PacketLittleBlocks(command,
						x, y, z, block.xSelected, block.ySelected,
						block.zSelected, block.blockID, block.side);
				ModLoader.sendPacket(packetLB.getPacket());
			}
		}
		
		public static void metadataModified(LittleWorld littleWorld, int x,
				int y, int z, int lastMetadata, int newMetadata) {
			/*
			 * Packet230ModLoader packet = new Packet230ModLoader();
			 * packet.dataInt = new int[]{0, x, y, z, getBlockId(x, y, z),
			 * newMetadata}; packet.isChunkDataPacket = true;
			 * ModLoaderMp.sendPacketToAll(mod_LittleBlocks.instance, packet);
			 */
			PacketLittleBlocks packetLB = new PacketLittleBlocks(
					LittleBlocksCore.metaDataModifiedCommand, x, y, z,
					littleWorld.getBlockId(x, y, z), newMetadata);
			sendToAll(packetLB);
		}

		public static void idModified(int x, int y, int z, int lastId,
				int newId, LittleWorld littleWorld) {
			/*
			 * Packet230ModLoader packet = new Packet230ModLoader();
			 * packet.dataInt = new int[]{0, x, y, z, newId, getBlockMetadata(x,
			 * y, z)}; packet.isChunkDataPacket = true;
			 * ModLoaderMp.sendPacketToAll(mod_LittleBlocks.instance, packet);
			 */
			PacketLittleBlocks packetLB = new PacketLittleBlocks(
					LittleBlocksCore.idModifiedCommand, x, y, z, newId,
					littleWorld.getBlockMetadata(x, y, z));
			sendToAll(packetLB);
		}

	}

	public static class Received {
		public static void handlePacket(PacketUpdate packet,
				EntityPlayer entityplayer, World world) {
			if (packet instanceof PacketLittleBlocks) {
				PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
				if (packetLB.getCommand().equals(
						LittleBlocksCore.blockActivateCommand)) {
					if (world.getBlockId(packetLB.xPosition,
							packetLB.yPosition, packetLB.zPosition) == LittleBlocksCore.littleBlocksID) {
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.xSelected = packetLB
								.getSelectedX();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.ySelected = packetLB
								.getSelectedY();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.zSelected = packetLB
								.getSelectedZ();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.side = packetLB
								.getMetadata();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks
								.onBlockActivated(world, packet.xPosition,
										packet.yPosition, packet.zPosition,
										entityplayer, 0, 0, 0, 0);
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.xSelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.ySelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.zSelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.side = -1;
					}
				}
				if (packetLB.getCommand().equals(
						LittleBlocksCore.blockClickCommand)) {
					if (world.getBlockId(packetLB.xPosition,
							packetLB.yPosition, packetLB.zPosition) == LittleBlocksCore.littleBlocksID) {
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.xSelected = packetLB
								.getSelectedX();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.ySelected = packetLB
								.getSelectedY();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.zSelected = packetLB
								.getSelectedZ();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.side = packetLB
								.getMetadata();
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks
								.onBlockClicked(world, packet.xPosition,
										packet.yPosition, packet.zPosition,
										entityplayer);
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.xSelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.ySelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.zSelected = -10;
						((mod_LittleBlocks) mod_LittleBlocks.instance).littleBlocks.side = -1;
					}
				}
				if (packetLB.getCommand().equals(LittleBlocksCore.idModifiedCommand) || packetLB.getCommand().equals(LittleBlocksCore.metaDataModifiedCommand)) {
					if (packetLB.targetExists(world)) {
						TileEntity tileentity = packetLB.getTileEntity(world);
						// world.setBlock(packet.dataInt[1] >> 3, packet.dataInt[2]
						// >> 3, packet.dataInt[3] >> 3, littleBlocksID);
						// tileent = world.getBlockTileEntity(packet.dataInt[1] >>
						// 3, packet.dataInt[2] >> 3, packet.dataInt[3] >> 3);
						if (tileentity != null
								&& tileentity instanceof TileEntityLittleBlocks) {
							TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
							switch (packet.payload.getIntPayload(0)) {
							case 0:
								tileentitylb.getLittleWorld().setBlockAndMetadata(
										packetLB.xPosition, packetLB.yPosition,
										packetLB.zPosition, packetLB.getBlockID(),
										packetLB.getMetadata());
								world.markBlockAsNeedsUpdate(
										packetLB.getSelectedX(),
										packetLB.getSelectedY(),
										packetLB.getSelectedZ());
								break;
							}
						}
					}
				}
			}
		}

		/*
		 * @Override public void handlePacket(Packet230ModLoader packet,
		 * EntityPlayerMP player) { World world =
		 * ModLoader.getMinecraftServerInstance().getWorldManager(0);
		 * switch(packet.dataInt[0]) { case 0: int x = packet.dataInt[1], y =
		 * packet.dataInt[2], z = packet.dataInt[3]; if(world.getBlockId(x, y,
		 * z) == mod_LittleBlocks.littleBlocksID) { littleBlocks.xSelected =
		 * packet.dataInt[4]; littleBlocks.ySelected = packet.dataInt[5];
		 * littleBlocks.zSelected = packet.dataInt[6]; littleBlocks.side =
		 * packet.dataInt[7]; littleBlocks.blockActivated(world, x, y, z,
		 * player); littleBlocks.xSelected = -10; littleBlocks.ySelected = -10;
		 * littleBlocks.zSelected = -10; littleBlocks.side = -1; } break;
		 * 
		 * case 1: x = packet.dataInt[1]; y = packet.dataInt[2]; z =
		 * packet.dataInt[3]; if(world.getBlockId(x, y, z) ==
		 * mod_LittleBlocks.littleBlocksID) { littleBlocks.xSelected =
		 * packet.dataInt[4]; littleBlocks.ySelected = packet.dataInt[5];
		 * littleBlocks.zSelected = packet.dataInt[6]; littleBlocks.side =
		 * packet.dataInt[7]; littleBlocks.onBlockClicked(world, x, y, z,
		 * player); littleBlocks.xSelected = -10; littleBlocks.ySelected = -10;
		 * littleBlocks.zSelected = -10; littleBlocks.side = -1; } break; } }
		 */
		
		public static void handleTileEntityPacket(PacketTileEntity packet,
				EntityPlayer entityplayer, World world) {
			TileEntity tileentity = world.getBlockTileEntity(packet.xPosition,
					packet.yPosition, packet.zPosition);
			if (packet instanceof PacketTileEntityLB) {
				PacketTileEntityLB packetLB = (PacketTileEntityLB) packet;
				if (tileentity != null
						&& tileentity instanceof TileEntityLittleBlocks) {
					TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
					switch (packetLB.payload.getIntPayload(0)) {
					case 0:
						int[][][] content = tileentitylb.getContent();
						switch (packetLB.payload.getIntPayload(1)) {
						case 0:
							break;
						case 1:
							for (int xx = 0; xx < 8; xx++) {
								for (int yy = 0; yy < 8; yy++) {
									for (int zz = 0; zz < 8; zz++) {
										tileentitylb.setContent(xx, yy, zz,
												packetLB.payload
														.getIntPayload(1 + xx
																+ yy * 8 + zz
																* 8 * 8),
												packetLB.payload
														.getIntPayload(1 + 8
																* 8 * 8 + xx
																+ yy * 8 + zz
																* 8 * 8), entityplayer);
									}
								}
							}
							break;
						case 2:
							for (int i = 0; i < (packetLB.payload.getIntSize() - 2) / 5; i++) {
								int xx = packetLB.payload
										.getIntPayload(2 + i * 5 + 0);
								int yy = packetLB.payload
										.getIntPayload(2 + i * 5 + 1);
								int zz = packetLB.payload
										.getIntPayload(2 + i * 5 + 2);
								int id = packetLB.payload
										.getIntPayload(2 + i * 5 + 3);
								int data = packetLB.payload
										.getIntPayload(2 + i * 5 + 4);
								tileentitylb.setContent(xx, yy, zz, id, data, entityplayer);
							}
							break;
						}
						// world.setBlockTileEntity(packet.xPosition,
						// packet.yPosition, packet.zPosition, tileentitylb);
						tileentitylb.onInventoryChanged();
						world.markBlockNeedsUpdate(packet.xPosition,
								packet.yPosition, packet.zPosition);
						break;
					}
				}
			}
		}
	}

	public static void sendToAll(PacketUpdate packet) {
		sendToAllWorlds(null, packet.getPacket(), packet.xPosition,
				packet.yPosition, packet.zPosition, true);
	}

	public static void sendToAllWorlds(EntityPlayer entityplayer,
			Packet packet, int x, int y, int z, boolean sendToPlayer) {
		World[] worlds = DimensionManager.getWorlds();
		for (int i = 0; i < worlds.length; i++) {
			sendToAllPlayers(worlds[i], entityplayer, packet, x, y, z,
					sendToPlayer);
		}
	}

	public static void sendToAllPlayers(World world, EntityPlayer entityplayer,
			Packet packet, int x, int y, int z, boolean sendToPlayer) {
		for (int j = 0; j < world.playerEntities.size(); j++) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) world.playerEntities
					.get(j);
			boolean shouldSendToPlayer = true;
			if (entityplayer != null) {
				if (entityplayer.username.equals(entityplayermp.username)
						&& !sendToPlayer)
					shouldSendToPlayer = false;
			}
			if (shouldSendToPlayer) {
				if (Math.abs(entityplayermp.posX - x) <= 16
						&& Math.abs(entityplayermp.posY - y) <= 16
						&& Math.abs(entityplayermp.posZ - z) <= 16)
					entityplayermp.serverForThisPlayer.theNetworkManager
							.addToSendQueue(packet);
			}
		}
	}
}
