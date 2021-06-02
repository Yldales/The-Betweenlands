package thebetweenlands.common.block.farming;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockBarnacle_3_4 extends BasicBlock implements ICustomItemBlock {
	public static final PropertyEnum<EnumBarnacleTypeLate> BARNACLE_TYPE_LATE = PropertyEnum.<EnumBarnacleTypeLate>create("barnacle_type_late", EnumBarnacleTypeLate.class);

	public BlockBarnacle_3_4() {
		super(Material.ROCK);
		setTickRandomly(true);
		setHardness(0.2F);
		setDefaultState(blockState.getBaseState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.NORTH_THREE));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return canPlaceBlock(world, pos, side);
    }

	@Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values())
            if (canPlaceBlock(world, pos, enumfacing))
                return true;
        return false;
    }

    protected static boolean canPlaceBlock(World world, BlockPos pos, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction.getOpposite());
        IBlockState iblockstate = world.getBlockState(blockpos);
        boolean flag = iblockstate.getBlockFaceShape(world, blockpos, direction) == BlockFaceShape.SOLID;
        Block block = iblockstate.getBlock();

        return world.isBlockNormalCube(blockpos, true) && block.isOpaqueCube(iblockstate) && flag;
    }

	@SuppressWarnings("incomplete-switch")
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

		EnumBarnacleTypeLate newFacing = EnumBarnacleTypeLate.NORTH_THREE;
		if (facing == EnumFacing.UP)
			newFacing = EnumBarnacleTypeLate.DOWN_THREE;

		else if (facing == EnumFacing.DOWN)
				newFacing = EnumBarnacleTypeLate.DOWN_THREE;
		else {
			switch (facing) {
			case SOUTH:
				newFacing = EnumBarnacleTypeLate.NORTH_THREE;
				break;
			case EAST:
				newFacing = EnumBarnacleTypeLate.WEST_THREE;
				break;
			case NORTH:
				newFacing = EnumBarnacleTypeLate.SOUTH_THREE;
				break;
			case WEST:
				newFacing = EnumBarnacleTypeLate.EAST_THREE;
				break;
			}
		}

		return getDefaultState().withProperty(BARNACLE_TYPE_LATE, newFacing);
    }

	@Override
	public int damageDropped(IBlockState state) {
		return EnumBarnacleTypeLate.NORTH_THREE.ordinal();
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BARNACLE_TYPE_LATE });
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab == BLCreativeTabs.BLOCKS)
			list.add(new ItemStack(this, 1, EnumBarnacleTypeLate.NORTH_THREE.ordinal()));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.values()[meta]);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote)
			return;

		EnumBarnacleTypeLate stage = state.getValue(BARNACLE_TYPE_LATE);

		switch (stage) {
		case DOWN_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.DOWN_FOUR), 2);
			break;
		case UP_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.UP_FOUR), 2);
			break;
		case EAST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.EAST_FOUR), 2);
			break;
		case NORTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.NORTH_FOUR), 2);
			break;
		case SOUTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.SOUTH_FOUR), 2);
			break;
		case WEST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.WEST_FOUR), 2);
			break;
		case DOWN_FOUR:
		case UP_FOUR:
		case EAST_FOUR:
		case NORTH_FOUR:
		case SOUTH_FOUR:
		case WEST_FOUR:
			break;
		default:
			break;
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumBarnacleTypeLate type = state.getValue(BARNACLE_TYPE_LATE);
		return type.ordinal();
	}
	
	public static enum EnumBarnacleTypeLate implements IStringSerializable {
		
		DOWN_THREE,
		UP_THREE,
		NORTH_THREE,
		SOUTH_THREE,
		WEST_THREE,
		EAST_THREE,
		DOWN_FOUR,
		UP_FOUR,
		NORTH_FOUR,
		SOUTH_FOUR,
		WEST_FOUR,
		EAST_FOUR;

		private final String name;

		private EnumBarnacleTypeLate() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumBarnacleTypeLate byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
