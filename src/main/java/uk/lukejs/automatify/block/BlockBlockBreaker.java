package uk.lukejs.automatify.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.lukejs.automatify.tileentity.TileEntityBlockBreaker;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlockBlockBreaker extends Block implements ITileEntityProvider {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    private Item.ToolMaterial toolMaterial;

    public BlockBlockBreaker(Item.ToolMaterial toolMaterial) {
        super(Material.ROCK);

        this.toolMaterial = toolMaterial;

        setHardness(3.5F);
        setSoundType(SoundType.STONE);

        setDefaultState(
                getBlockState()
                        .getBaseState()
                        .withProperty(FACING, EnumFacing.NORTH)
                        .withProperty(POWERED, false)
        );
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean receivingPower = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());

        if (receivingPower && !state.getValue(POWERED)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true));
        } else if (!receivingPower && state.getValue(POWERED)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, false));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
                .withProperty(POWERED, false);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState()
                // extract first 3 bits (0111)
                // & makes the 4th bit disappear (0 & anything = 0)
                // and preserves the 3 bits we care about
                .withProperty(FACING, EnumFacing.values()[meta & 7])
                // 8 (1000) makes the first 3 bits always 0
                // so we will get 8 or 0, depends if the 4th
                // bit is 1 or 0 (left most)
                .withProperty(POWERED, (meta & 8) != 0); // extract 4th (last) bit (1000)
    }

    public int getMetaFromState(IBlockState state)
    {
        // 0000
        int i = 0;

        // indexes 0-5
        // possible values for each of the 6 sides:
        // 0000, 0001, 0010, 0011, 0100, 0101 (0-5)
        // index 5 (6th side)
        // 0101 -- 2 bits wasted as we cant have 0111 (7)
        // ^ 4th bit unused (1000 = 8)
        i |= state.getValue(FACING).getIndex();

        // so we use the 8th bit for it's powered status
        // if we need something else, this can always be
        // calculated when the blcok loads
        // i |= 8 = 1XXX
        // otherwise, 0XXX
        if (state.getValue(POWERED))
        {
            i |= 8;
        }

        return i;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBlockBreaker(toolMaterial);
    }

    private Optional<TileEntityBlockBreaker> getTileEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity == null || !(tileEntity instanceof TileEntityBlockBreaker)) {
            return Optional.empty();
        }

        return Optional.of((TileEntityBlockBreaker) tileEntity);
    }
}
