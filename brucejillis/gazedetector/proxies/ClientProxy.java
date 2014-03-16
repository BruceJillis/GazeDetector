package brucejillis.gazedetector.proxies;

import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import brucejillis.gazedetector.entities.renderers.TileEntityGazeDetectorRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(brucejillis.gazedetector.entities.TileEntityGazeDetector.class, new TileEntityGazeDetectorRenderer());
	}
}
