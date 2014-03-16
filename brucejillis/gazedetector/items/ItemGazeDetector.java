package brucejillis.gazedetector.items;

import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.blocks.BlockGazeDetector;
import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import brucejillis.gazedetector.events.GazeDetectorRendererHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ItemGazeDetector extends Item {
	public static final String[] skullTypes = new String[] { "skeleton", "wither", "zombie", "char", "creeper", "enderman" };
	public static final String[] iconNames = new String[] { "skeleton", "wither", "zombie", "steve", "creeper", "enderman" };

	@SideOnly(Side.CLIENT)
	private Icon[] skullIcons;

	public ItemGazeDetector(int id) {
		super(id);
		this.setCreativeTab(CreativeTabs.tabBlock);
		setUnlocalizedName("gazeDetectorItem");
		setMaxDamage(0);
		setHasSubtypes(true);
		setTextureName("skull");
		MinecraftForge.EVENT_BUS.register(new GazeDetectorRendererHandler());
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (par7 == 0) {
			return false;
		} else if (!world.getBlockMaterial(x, y, z).isSolid()) {
			return false;
		} else {
			if (par7 == 1) {
				++y;
			}

			if (par7 == 2) {
				--z;
			}

			if (par7 == 3) {
				++z;
			}

			if (par7 == 4) {
				--x;
			}

			if (par7 == 5) {
				++x;
			}

			if (!player.canPlayerEdit(x, y, z, par7, itemStack)) {
				return false;
			} else if (!GazeDetectorMod.gazeDetectorBlock.canPlaceBlockAt(world, x, y, z)) {
				return false;
			} else {
				world.setBlock(x, y, z, GazeDetectorMod.gazeDetectorBlock.blockID, par7, 2);
				int i1 = 0;

				if (par7 == 1) {
					i1 = MathHelper.floor_double(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
				}
				TileEntity tileentity = world.getBlockTileEntity(x, y, z);
				if (tileentity != null && tileentity instanceof TileEntityGazeDetector) {
					String s = "";
					if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("SkullOwner")) {
						s = itemStack.getTagCompound().getString("SkullOwner");
					}
					((TileEntityGazeDetector) tileentity).setSkullType(itemStack.getItemDamage(), s);
					((TileEntityGazeDetector) tileentity).setSkullRotation(i1);
					((BlockGazeDetector) GazeDetectorMod.gazeDetectorBlock).makeWither(world, x, y, z, (TileEntityGazeDetector) tileentity);
				}

				--itemStack.stackSize;
				return true;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int j = 0; j < skullTypes.length; ++j) {
			par3List.add(new ItemStack(par1, 1, j));
		}
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		if (par1 < 0 || par1 >= skullTypes.length) {
			par1 = 0;
		}
		return this.skullIcons[par1];
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int i = par1ItemStack.getItemDamage();
		if (i < 0 || i >= skullTypes.length) {
			i = 0;
		}
		return super.getUnlocalizedName() + "." + skullTypes[i];
	}

	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return par1ItemStack.getItemDamage() == 3 && par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("SkullOwner") ? StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] { par1ItemStack.getTagCompound().getString("SkullOwner") }) : super.getItemDisplayName(par1ItemStack);
	}

	@Override
	protected String getIconString() {
		return String.format("%s:%s", GazeDetectorMod.ID, "skull");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistery) {
		this.skullIcons = new Icon[iconNames.length];
		for (int i = 0; i < iconNames.length; ++i) {
			this.skullIcons[i] = iconRegistery.registerIcon(this.getIconString() + "_" + iconNames[i]);
		}
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		if (armorType == 0 && itemID == GazeDetectorMod.gazeDetectorItem.itemID) {
			return true;
		}
		return super.isValidArmor(stack, armorType, entity);
	}
}
