package brucejillis.gazedetector.entities.renderers;

import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntityGazeDetectorRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation skeleton = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	private static final ResourceLocation witherSkeleton = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	private static final ResourceLocation zombie = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation creeper = new ResourceLocation("textures/entity/creeper/creeper.png");
	private static final ResourceLocation enderman = new ResourceLocation(String.format("%s:%s", GazeDetectorMod.ID, "textures/entity/enderman/enderman.png"));

	public static TileEntityGazeDetectorRenderer skullRenderer;

	private ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
	private ModelSkeletonHead skeletonHeadZombie = new ModelSkeletonHead(0, 0, 64, 64);

	public void renderTileEntitySkullAt(TileEntityGazeDetector par1TileEntitySkull, double par2, double par4, double par6, float par8) {
		this.func_82393_a((float) par2, (float) par4, (float) par6, par1TileEntitySkull.getBlockMetadata() & 7, (float) (par1TileEntitySkull.getSkullRotation() * 360) / 16.0F, par1TileEntitySkull.getSkullType(), par1TileEntitySkull.getExtraType());
	}

	public void setTileEntityRenderer(TileEntityRenderer par1TileEntityRenderer) {
		super.setTileEntityRenderer(par1TileEntityRenderer);
		skullRenderer = this;
	}

	public void func_82393_a(float par1, float par2, float par3, int par4, float par5, int par6, String par7Str) {
		ModelSkeletonHead modelskeletonhead = this.skeletonHead;

		switch (par6) {
			case 0:
			default:
				this.bindTexture(skeleton);
				break;
			case 1:
				this.bindTexture(witherSkeleton);
				break;
			case 2:
				this.bindTexture(zombie);
				modelskeletonhead = this.skeletonHeadZombie;
				break;
			case 3:
				ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
				if (par7Str != null && par7Str.length() > 0) {
					resourcelocation = AbstractClientPlayer.getLocationSkull(par7Str);
					AbstractClientPlayer.getDownloadImageSkin(resourcelocation, par7Str);
				}
				this.bindTexture(resourcelocation);
				break;
			case 4:
				this.bindTexture(creeper);
				break;
			case 5:
				this.bindTexture(enderman);
				break;
		}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		if (par4 != 1) {
			switch (par4) {
				case 2:
					GL11.glTranslatef(par1 + 0.5F, par2 + 0.25F, par3 + 0.74F);
					break;
				case 3:
					GL11.glTranslatef(par1 + 0.5F, par2 + 0.25F, par3 + 0.26F);
					par5 = 180.0F;
					break;
				case 4:
					GL11.glTranslatef(par1 + 0.74F, par2 + 0.25F, par3 + 0.5F);
					par5 = 270.0F;
					break;
				case 5:
				default:
					GL11.glTranslatef(par1 + 0.26F, par2 + 0.25F, par3 + 0.5F);
					par5 = 90.0F;
			}
		} else {
			GL11.glTranslatef(par1 + 0.5F, par2, par3 + 0.5F);
		}

		float f4 = 0.0625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		modelskeletonhead.render((Entity) null, 0.0F, 0.0F, 0.0F, par5, 0.0F, f4);
		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
		this.renderTileEntitySkullAt((TileEntityGazeDetector) par1TileEntity, par2, par4, par6, par8);
	}
}