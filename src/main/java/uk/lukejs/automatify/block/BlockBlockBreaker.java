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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.lukejs.automatify.block.properties.PropertyToolMaterial;
import uk.lukejs.automatify.tileentity.TileEntityBlockBreaker;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlockBlockBreaker extends Block implements ITileEntityProvider {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyToolMaterial TOOL_MATERIAL = PropertyToolMaterial.create("tool_material");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    private Item.ToolMaterial material;

    public BlockBlockBreaker(Item.ToolMaterial material) {
        super(Material.ROCK);

        this.material = material;

        setUnlocalizedName("automatify.blockBreaker");
        setRegistryName(new ResourceLocation("automatify", "block_breaker"));
        setHardness(3.5F);
        setSoundType(SoundType.STONE);

        setDefaultState(
                getBlockState()
                        .getBaseState()
                        .withProperty(FACING, EnumFacing.NORTH)
                        .withProperty(TOOL_MATERIAL, Item.ToolMaterial.IRON)
                        .withProperty(POWERED, false)
        );
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean receivingPower = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());

        if (receivingPower && !state.getValue(POWERED)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true));
            getTileEntity(worldIn, pos).ifPresent(t -> t.setPowered(true));
        } else if (!receivingPower && state.getValue(POWERED)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, false));
            getTileEntity(worldIn, pos).ifPresent(t -> t.setPowered(false));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TOOL_MATERIAL, POWERED);
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
                .withProperty(POWERED, meta == 1);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

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
        return new TileEntityBlockBreaker();
    }

    private Optional<TileEntityBlockBreaker> getTileEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity == null || !(tileEntity instanceof TileEntityBlockBreaker)) {
            return Optional.empty();
        }

        return Optional.of((TileEntityBlockBreaker) tileEntity);
    }
}
