package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.TileEntityPossessedBlock;

public class BlockPossessedBlock extends BlockContainer {

	public BlockPossessedBlock() {
		super(Material.rock);
		setStepSound(Block.soundTypeStone);
		setHardness(1.5F);
		setResistance(10.0F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockTextureName("thebetweenlands:betweenstoneBricks");
		setBlockName("thebetweenlands.possessedBlock");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPossessedBlock();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int meta = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, meta == 0 ? 2 : meta == 1 ? 5 : meta == 2 ? 3 : 4, 2);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
}