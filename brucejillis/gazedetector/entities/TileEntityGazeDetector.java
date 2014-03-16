package brucejillis.gazedetector.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.items.ItemGazeDetector;
import brucejillis.gazedetector.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

public class TileEntityGazeDetector extends TileEntity {
	public static final String ID = "gaze-detector-tile-entity";
	private static final int DISTANCE = 32;
	private static final int ENDERMAN_SKULL = 5;

	// current powering state
	private boolean isPowering;

	// properties for emulating vanilla skulls
	private int skullType;
	private int skullRotation;
	private String extraType = "";
	private int shutOffDelay = -1;

	public TileEntityGazeDetector() {
		this.isPowering = false;
	}

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote && this.skullType == ENDERMAN_SKULL) {
			// now determine if the block should emit power
			boolean isPowering = false;
			if (this.anyPlayerInRange()) {
				AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(xCoord - DISTANCE, yCoord - DISTANCE, zCoord - DISTANCE, xCoord + DISTANCE, yCoord + DISTANCE, zCoord + DISTANCE);
				List players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
				for (Object o : players) {
					if (isLookingAtUs((EntityPlayer) o)) {
						isPowering = true;
					}
				}
			}
			// toggle powered state on change
			if (isPowering && !this.isPowering) {
				this.isPowering = true;
				sendChangeToServer();
			} else if (!isPowering && this.isPowering && this.shutOffDelay == -1) {
				this.shutOffDelay = 35;
			}
			if ((this.shutOffDelay > 0) && this.isPowering) {
				this.shutOffDelay--;
			}
			if ((this.shutOffDelay == 0) && this.isPowering) {
				this.isPowering = false;
				this.shutOffDelay = -1;
				sendChangeToServer();
			}			
		}
	}

	// returns true if there is any player within DISTANCE blocks
	private boolean anyPlayerInRange() {
		EntityPlayer player = this.worldObj.getClosestPlayer(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, TileEntityGazeDetector.DISTANCE);
		return (player != null);
	}

	// returns true of a player is looking at the block associated with this
	// tile entity
	private boolean isLookingAtUs(EntityPlayer player) {
		MovingObjectPosition p = player.rayTrace(DISTANCE, 1.0f);
		if (p == null)
			return false;
		return (p.blockX == xCoord) && (p.blockY == yCoord) && (p.blockZ == zCoord);
	}

	public void setSkullType(int skullType, String extra) {
		this.skullType = skullType;
		this.extraType = extra;
	}

	public int getSkullType() {
		return this.skullType;
	}

	public String getExtraType() {
		return this.extraType;
	}
	
	public void setSkullRotation(int rotation) {
		this.skullRotation = rotation;
	}

	@SideOnly(Side.CLIENT)
	public int getSkullRotation() {
		return this.skullRotation;
	}

	public boolean isPowering() {
		return isPowering;
	}

	public void setPowering(boolean isPowering) {
		this.isPowering = isPowering;
	}

	public void sendChangeToServer() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeInt(xCoord);
			os.writeInt(yCoord);
			os.writeInt(zCoord);
			// custom
			os.writeBoolean(isPowering);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// now create the packet and send it
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.channel = GazeDetectorMod.CHANNEL;
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("isPowering", isPowering);
		tag.setByte("SkullType", (byte) (this.skullType & 255));
		tag.setByte("Rot", (byte) (this.skullRotation & 255));
		tag.setString("ExtraType", this.extraType);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		isPowering = tag.getBoolean("isPowering");
		this.skullType = tag.getByte("SkullType");
		this.skullRotation = tag.getByte("Rot");
		if (tag.hasKey("ExtraType")) {
			this.extraType = tag.getString("ExtraType");
		}
		super.readFromNBT(tag);
	}
}
