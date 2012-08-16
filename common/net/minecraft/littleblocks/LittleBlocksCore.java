package net.minecraft.littleblocks;

import java.io.File;

import cpw.mods.fml.common.network.NetworkRegistry;

import net.minecraft.littleblocks.network.LittleBlocksConnection;
import net.minecraft.src.MLProp;
import net.minecraftforge.common.Configuration;

public class LittleBlocksCore {
	public static final String version = "1.2";
	public static final String modName = "LittleBlocks Mod";
	public static final String modChannel = "LITTLEBLOCKS";
	public static File configFile = new File(LittleBlocks.minecraftDir,
			"config/LittleBlocks.cfg");
	public static Configuration configuration = new Configuration(configFile);
	public static String metaDataModifiedCommand = "METADATA";
	public static String idModifiedCommand = "IDMOD";
	public static String blockClickCommand = "BLOCKCLICK";
	public static String blockActivateCommand = "BLOCKACTIVATE";

	public static int littleBlocksID = configurationProperties();
	public static int renderingMethod;

	public static boolean initialized = false;

	public static void initialize() {
		LittleBlocks.initialize();
		NetworkRegistry.instance().registerConnectionHandler(new LittleBlocksConnection());
		initialized = true;
	}

	public static void addItems() {
	}

	public static void addNames() {
	}

	public static void addRecipes() {
	}
	
	public static int configurationProperties() {
		configuration.load();
		littleBlocksID = Integer.parseInt(configuration
				.getOrCreateIntProperty("littleBlocksID",
						Configuration.CATEGORY_BLOCK,
						140).value);
		renderingMethod = Integer.parseInt(configuration
				.getOrCreateIntProperty("renderingMethod",
						Configuration.CATEGORY_GENERAL,
						0).value);
		configuration.save();
		return littleBlocksID;
	}
}
