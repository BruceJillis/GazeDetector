package brucejillis.gazedetector.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import brucejillis.gazedetector.util.LogHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class GazeDetectorPacketHandler implements IPacketHandler  {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals(GazeDetectorMod.CHANNEL)) {
			handleGazeDetectorPacket(packet, player);
		}
	}

	private void handleGazeDetectorPacket(Packet250CustomPayload packet, Player player) {
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			// read data
			int x = is.readInt();
			int y = is.readInt();
			int z = is.readInt();
			boolean powered = is.readBoolean();
			// process it
			EntityPlayerMP playerMp = (EntityPlayerMP) player;
			TileEntityGazeDetector te = (TileEntityGazeDetector) playerMp.worldObj.getBlockTileEntity(x, y, z);
			if (te != null) {
				// update state to keep it in sync
				te.setPowering(powered); 
				// change metadata to reflect correct powered state
				int metadata = playerMp.worldObj.getBlockMetadata(x, y, z);
				if (powered) {
					metadata = metadata | 8; 
				} else {
					metadata = metadata & ~8;
				}
				playerMp.worldObj.setBlockMetadataWithNotify(x, y, z, metadata, 3);						
			}
		} catch (Exception e) {
            e.printStackTrace();
            return;			
		}
	}
}
