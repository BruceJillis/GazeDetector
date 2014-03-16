package brucejillis.gazedetector;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import brucejillis.gazedetector.blocks.BlockGazeDetector;
import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import brucejillis.gazedetector.items.ItemGazeDetector;
import brucejillis.gazedetector.net.GazeDetectorPacketHandler;
import brucejillis.gazedetector.proxies.CommonProxy;
import brucejillis.gazedetector.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid=GazeDetectorMod.ID, name=GazeDetectorMod.NAME, version=GazeDetectorMod.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=true , channels={GazeDetectorMod.CHANNEL}, packetHandler=GazeDetectorPacketHandler.class)
public class GazeDetectorMod {	
	public static final String NAME    = "Ender Eye Gaze Detector";
	public static final String ID      = "gaze-detector";
	public static final String VERSION = "v0.1";
	public static final String CHANNEL = "gaze-detector";
	
	// the instance of this mod that forge uses
    @Instance(value=GazeDetectorMod.ID)
    public static GazeDetectorMod instance;
    
    // proxies
	@SidedProxy(clientSide="brucejillis.gazedetector.proxies.ClientProxy", serverSide="brucejillis.gazedetector.proxies.CommonProxy")
	public static CommonProxy proxy;

	// blocks and items
	public static Block gazeDetectorBlock;
	public static Item  gazeDetectorItem;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		LogHelper.init();
		LogHelper.log(Level.INFO, "Initializing the gaze detector mod!");
		proxy.initRenderers();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		// add gaze detector block
		gazeDetectorBlock = new BlockGazeDetector(1500);
		GameRegistry.registerBlock(gazeDetectorBlock, ID + gazeDetectorBlock.getUnlocalizedName().substring(5));
		LanguageRegistry.addName(gazeDetectorBlock, "Gaze Detector");
		// register gaze detector tile entity
		GameRegistry.registerTileEntity(TileEntityGazeDetector.class, TileEntityGazeDetector.ID);
		// add gaze detector item
		gazeDetectorItem = new ItemGazeDetector(5000);
		LanguageRegistry.addName(gazeDetectorItem, "Gaze Detector");
	}
}
