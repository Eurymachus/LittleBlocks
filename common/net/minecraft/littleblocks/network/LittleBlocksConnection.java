package net.minecraft.littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.littleblocks.LittleBlocks;
import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.littleblocks.network.EurysNetworkCore.INetworkConnections;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketIds;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntityLB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.World;
import net.minecraft.src.forge.MessageManager;

public class LittleBlocksConnection implements INetworkConnections {
	private static String modName = LittleBlocksCore.modName;
	private static String modVersion = LittleBlocksCore.version;
	private static String modChannel = LittleBlocksCore.modChannel;

	@Override
	public void onPacketData(NetworkManager network, String channel,
			byte[] bytes) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(
				bytes));
		try {
			World world = LittleBlocks.getWorld(network);
			EntityPlayer entityplayer = LittleBlocks.getPlayer(network);
			int packetID = data.read();
			switch (packetID) {
			case PacketIds.TILE:
				PacketTileEntityLB packetTileLB = new PacketTileEntityLB();
				packetTileLB.readData(data);
				PacketHandler.Received.handleTileEntityPacket(packetTileLB,
						entityplayer, world);
				break;
			case PacketIds.UPDATE:
				PacketLittleBlocks packetLB = new PacketLittleBlocks();
				packetLB.readData(data);
				PacketHandler.Received.handlePacket(packetLB, entityplayer,
						world);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onConnect(NetworkManager network) {
	}

	@Override
	public void onLogin(NetworkManager network, Packet1Login login) {
		MessageManager.getInstance().registerChannel(network, this,
				LittleBlocksConnection.modChannel);
	}

	@Override
	public void onDisconnect(NetworkManager network, String message,
			Object[] args) {
	}

}
