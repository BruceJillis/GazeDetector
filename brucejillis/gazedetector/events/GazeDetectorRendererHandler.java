package brucejillis.gazedetector.events;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.entities.renderers.TileEntityGazeDetectorRenderer;
import brucejillis.gazedetector.util.LogHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class GazeDetectorRendererHandler {
//	@ForgeSubscribe
//	public void renderPlayer(RenderPlayerEvent.Specials.Pre evt) {
//		if (evt.entityLiving instanceof AbstractClientPlayer) {
//			AbstractClientPlayer player = (AbstractClientPlayer) evt.entityLiving;
//			ItemStack itemstack = player.inventory.armorItemInSlot(3);
//			if (itemstack != null && itemstack.getItem().itemID == GazeDetectorMod.gazeDetectorItem.itemID) {
//				//evt.renderHelmet = false;
//			}
//		}
//	}
	
	@ForgeSubscribe
	public void renderPlayer(RenderPlayerEvent.Specials.Pre evt) {
		if (evt.entityLiving instanceof AbstractClientPlayer) {
			RenderPlayer renderer = evt.renderer;
			AbstractClientPlayer player = (AbstractClientPlayer) evt.entityLiving;
			float partialRenderTick = (float) evt.partialRenderTick; 

			ItemStack itemstack = player.inventory.armorItemInSlot(3);
			if (itemstack != null && itemstack.getItem().itemID == GazeDetectorMod.gazeDetectorItem.itemID) {
	            GL11.glPushMatrix();
	            ModelBiped modelBipedMain = ReflectionHelper.getPrivateValue(RenderPlayer.class, renderer, "modelBipedMain");
	            modelBipedMain.bipedHead.postRender(0.0625F);
	            // do our own rendering for skulls	            
                float f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                String s = "";
                if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("SkullOwner")) {
                    s = itemstack.getTagCompound().getString("SkullOwner");
                }
                TileEntityGazeDetectorRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemDamage(), s);
                // done
                GL11.glPopMatrix();
                evt.renderHelmet = false;
			}
		}
	}
}
