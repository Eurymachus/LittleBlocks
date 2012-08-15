package net.minecraft.littleblocks.network;

import net.minecraft.littleblocks.BlockLittleBlocks;
import net.minecraft.littleblocks.LittleWorld;
import net.minecraft.littleblocks.TileEntityLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntity;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntityLB;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketUpdate;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

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
		}

		public static void idModified(int x, int y, int z, int lastId,
				int newId, LittleWorld littleWorld) {
		}

	}

	public static class Received {
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
																* 8 * 8));
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
								tileentitylb.setContent(xx, yy, zz, id, data);
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

		public static void handlePacket(PacketUpdate packet,
				EntityPlayer entityplayer, World world) {
			if (packet instanceof PacketLittleBlocks) {
				PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
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
}
