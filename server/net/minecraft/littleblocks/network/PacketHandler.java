package net.minecraft.littleblocks.network;

import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.littleblocks.LittleWorld;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketUpdate;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet;
import net.minecraft.src.World;
import net.minecraft.src.mod_LittleBlocks;
import net.minecraft.src.forge.DimensionManager;

public class PacketHandler {
	public static class Output {

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
								.blockActivated(world, packet.xPosition,
										packet.yPosition, packet.zPosition,
										entityplayer);
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

		public static void handleTileEntityPacket(PacketUpdate packet,
				EntityPlayer entityplayer, World world) {
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
					entityplayermp.playerNetServerHandler.netManager
							.addToSendQueue(packet);
			}
		}
	}
}
