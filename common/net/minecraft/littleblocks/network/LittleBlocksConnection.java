package net.minecraft.littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;

import net.minecraft.littleblocks.LittleBlocks;
import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.littleblocks.network.EurysNetworkCore.INetworkConnections;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketIds;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketLittleBlocks;
import net.minecraft.littleblocks.network.EurysNetworkCore.PacketTileEntityLB;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

public class LittleBlocksConnection implements INetworkConnections {
	private static String modName = LittleBlocksCore.modName;
	private static String modVersion = LittleBlocksCore.version;
	private static String modChannel = LittleBlocksCore.modChannel;

	@Override
	public void onPacketData(NetworkManager network, Packet250CustomPayload payload,
			Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(
				payload.data));
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
	public void playerLoggedIn(Player player, NetHandler netHandler,
			NetworkManager manager) {
		NetworkRegistry.instance().registerChannel(this,
				LittleBlocksConnection.modChannel);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			NetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(NetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			NetworkManager manager, Packet1Login login) {
		NetworkRegistry.instance().registerChannel(this,
				LittleBlocksConnection.modChannel);
	}
}
