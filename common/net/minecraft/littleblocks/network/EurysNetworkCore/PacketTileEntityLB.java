package net.minecraft.littleblocks.network.EurysNetworkCore;

import net.minecraft.littleblocks.LittleBlocksCore;
import net.minecraft.littleblocks.TileEntityLittleBlocks;

public class PacketTileEntityLB extends PacketTileEntity {

	public PacketTileEntityLB() {
		super();
		this.channel = LittleBlocksCore.modChannel;
	}

	public PacketTileEntityLB(TileEntityLittleBlocks tileentity) {
		this();
		this.setPosition(tileentity.xCoord, tileentity.yCoord,
				tileentity.zCoord);
		this.payload = tileentity.getTileEntityPayload();
	}
}
