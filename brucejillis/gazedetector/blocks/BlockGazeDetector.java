package brucejillis.gazedetector.blocks;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import brucejillis.gazedetector.GazeDetectorMod;
import brucejillis.gazedetector.entities.TileEntityGazeDetector;
import brucejillis.gazedetector.items.ItemGazeDetector;
import brucejillis.gazedetector.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGazeDetector extends BlockContainer {
	public BlockGazeDetector(int blockID) {
		super(blockID, Material.circuits);
		setUnlocalizedName("gazeDetectorBlock");
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
	}

	public int getRenderType() {
		return -1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		int l = blockAccess.getBlockMetadata(x, y, z) & 7;
		switch (l) {
			case 1:
			default:
				this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
				break;
			case 2:
				this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
				break;
			case 3:
				this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
				break;
			case 4:
				this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
				break;
			case 5:
				this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
		}
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
		int l = MathHelper.floor_double((double) (entityLivingBase.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@SideOnly(Side.CLIENT)
	public int idPicked(World world, int x, int y, int z) {
		return GazeDetectorMod.gazeDetectorItem.itemID;
	}

	public int getDamageValue(World world, int x, int y, int z) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		return tileentity != null && tileentity instanceof TileEntityGazeDetector ? ((TileEntityGazeDetector) tileentity).getSkullType() : super.getDamageValue(world, x, y, z);
	}

	public int damageDropped(int par1) {
		return par1;
	}

	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		metadata &= ~8;
		if (player.capabilities.isCreativeMode) {
			world.setBlockMetadataWithNotify(x, y, z, metadata, 4);
		}
		dropBlockAsItem(world, x, y, z, metadata, 0);
		super.onBlockHarvested(world, x, y, z, metadata, player);
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if ((metadata & 8) == 0) {
			ItemStack itemstack = new ItemStack(GazeDetectorMod.gazeDetectorItem.itemID, 1, this.getDamageValue(world, x, y, z));
			TileEntityGazeDetector tileentityskull = (TileEntityGazeDetector) world.getBlockTileEntity(x, y, z);

			if (tileentityskull == null) {
				return drops;
			}
			if (tileentityskull.getSkullType() == 3 && tileentityskull.getExtraType() != null && tileentityskull.getExtraType().length() > 0) {
				itemstack.setTagCompound(new NBTTagCompound());
				itemstack.getTagCompound().setString("SkullOwner", tileentityskull.getExtraType());
			}
			drops.add(itemstack);
		}
		return drops;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
	}

	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return Block.slowSand.getBlockTextureFromSide(par1);
	}

	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return String.format("%s:%s", GazeDetectorMod.ID, "skull");
	}

	@SideOnly(Side.CLIENT)
	public String getItemIconName() {
		return String.format("%s_%s", this.getTextureName(), ItemGazeDetector.iconNames[0]);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		return ((blockAccess.getBlockMetadata(x, y, z) & 8) == 8) ? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int par5) {
		return isProvidingWeakPower(blockAccess, x, y, z, par5);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGazeDetector();
	}

	public void makeWither(World world, int x, int y, int z, TileEntityGazeDetector skull) {
		if (skull.getSkullType() == 1 && y >= 2 && world.difficultySetting > 0 && !world.isRemote) {
			int l = Block.slowSand.blockID;
			int i1;
			EntityWither entitywither;
			int j1;

			for (i1 = -2; i1 <= 0; ++i1) {
				if (world.getBlockId(x, y - 1, z + i1) == l && world.getBlockId(x, y - 1, z + i1 + 1) == l && world.getBlockId(x, y - 2, z + i1 + 1) == l && world.getBlockId(x, y - 1, z + i1 + 2) == l && this.func_82528_d(world, x, y, z + i1, 1) && this.func_82528_d(world, x, y, z + i1 + 1, 1) && this.func_82528_d(world, x, y, z + i1 + 2, 1)) {
					world.setBlockMetadataWithNotify(x, y, z + i1, 8, 2);
					world.setBlockMetadataWithNotify(x, y, z + i1 + 1, 8, 2);
					world.setBlockMetadataWithNotify(x, y, z + i1 + 2, 8, 2);
					world.setBlock(x, y, z + i1, 0, 0, 2);
					world.setBlock(x, y, z + i1 + 1, 0, 0, 2);
					world.setBlock(x, y, z + i1 + 2, 0, 0, 2);
					world.setBlock(x, y - 1, z + i1, 0, 0, 2);
					world.setBlock(x, y - 1, z + i1 + 1, 0, 0, 2);
					world.setBlock(x, y - 1, z + i1 + 2, 0, 0, 2);
					world.setBlock(x, y - 2, z + i1 + 1, 0, 0, 2);

					if (!world.isRemote) {
						entitywither = new EntityWither(world);
						entitywither.setLocationAndAngles((double) x + 0.5D, (double) y - 1.45D, (double) (z + i1) + 1.5D, 90.0F, 0.0F);
						entitywither.renderYawOffset = 90.0F;
						entitywither.func_82206_m();
						world.spawnEntityInWorld(entitywither);
					}

					for (j1 = 0; j1 < 120; ++j1) {
						world.spawnParticle("snowballpoof", (double) x + world.rand.nextDouble(), (double) (y - 2) + world.rand.nextDouble() * 3.9D, (double) (z + i1 + 1) + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
					}

					world.notifyBlockChange(x, y, z + i1, 0);
					world.notifyBlockChange(x, y, z + i1 + 1, 0);
					world.notifyBlockChange(x, y, z + i1 + 2, 0);
					world.notifyBlockChange(x, y - 1, z + i1, 0);
					world.notifyBlockChange(x, y - 1, z + i1 + 1, 0);
					world.notifyBlockChange(x, y - 1, z + i1 + 2, 0);
					world.notifyBlockChange(x, y - 2, z + i1 + 1, 0);
					return;
				}
			}

			for (i1 = -2; i1 <= 0; ++i1) {
				if (world.getBlockId(x + i1, y - 1, z) == l && world.getBlockId(x + i1 + 1, y - 1, z) == l && world.getBlockId(x + i1 + 1, y - 2, z) == l && world.getBlockId(x + i1 + 2, y - 1, z) == l && this.func_82528_d(world, x + i1, y, z, 1) && this.func_82528_d(world, x + i1 + 1, y, z, 1) && this.func_82528_d(world, x + i1 + 2, y, z, 1)) {
					world.setBlockMetadataWithNotify(x + i1, y, z, 8, 2);
					world.setBlockMetadataWithNotify(x + i1 + 1, y, z, 8, 2);
					world.setBlockMetadataWithNotify(x + i1 + 2, y, z, 8, 2);
					world.setBlock(x + i1, y, z, 0, 0, 2);
					world.setBlock(x + i1 + 1, y, z, 0, 0, 2);
					world.setBlock(x + i1 + 2, y, z, 0, 0, 2);
					world.setBlock(x + i1, y - 1, z, 0, 0, 2);
					world.setBlock(x + i1 + 1, y - 1, z, 0, 0, 2);
					world.setBlock(x + i1 + 2, y - 1, z, 0, 0, 2);
					world.setBlock(x + i1 + 1, y - 2, z, 0, 0, 2);

					if (!world.isRemote) {
						entitywither = new EntityWither(world);
						entitywither.setLocationAndAngles((double) (x + i1) + 1.5D, (double) y - 1.45D, (double) z + 0.5D, 0.0F, 0.0F);
						entitywither.func_82206_m();
						world.spawnEntityInWorld(entitywither);
					}

					for (j1 = 0; j1 < 120; ++j1) {
						world.spawnParticle("snowballpoof", (double) (x + i1 + 1) + world.rand.nextDouble(), (double) (y - 2) + world.rand.nextDouble() * 3.9D, (double) z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
					}

					world.notifyBlockChange(x + i1, y, z, 0);
					world.notifyBlockChange(x + i1 + 1, y, z, 0);
					world.notifyBlockChange(x + i1 + 2, y, z, 0);
					world.notifyBlockChange(x + i1, y - 1, z, 0);
					world.notifyBlockChange(x + i1 + 1, y - 1, z, 0);
					world.notifyBlockChange(x + i1 + 2, y - 1, z, 0);
					world.notifyBlockChange(x + i1 + 1, y - 2, z, 0);
					return;
				}
			}
		}
	}

	private boolean func_82528_d(World world, int x, int y, int z, int par5) {
		if (world.getBlockId(x, y, z) != this.blockID) {
			return false;
		} else {
			TileEntity tileentity = world.getBlockTileEntity(x, y, z);
			return tileentity != null && tileentity instanceof TileEntityGazeDetector ? ((TileEntityGazeDetector) tileentity).getSkullType() == par5 : false;
		}
	}

}
