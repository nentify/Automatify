package uk.lukejs.automatify.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBlockBreaker extends Block {

    private boolean isPowered = false;

    public BlockBlockBreaker() {
        super(Material.ROCK);

        setUnlocalizedName("automatify.blockBreaker");
        setRegistryName(new ResourceLocation("automatify", "block_breaker"));
        setHardness(3.5F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean receivingPower = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());

        if (receivingPower && !isPowered) {
            worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
            isPowered = true;
        }
        else if (!receivingPower && isPowered) {
            isPowered = false;
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 4;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            BlockPos breakPos = pos.down();
            IBlockState brokenBlockState = worldIn.getBlockState(breakPos);
            Block brokenBlock = brokenBlockState.getBlock();

            if (worldIn.destroyBlock(breakPos, false)) {
                NonNullList<ItemStack> stacksToDrop = NonNullList.create();
                brokenBlock.getDrops(stacksToDrop, worldIn, breakPos, brokenBlockState, 0);

                for (ItemStack stack : stacksToDrop) {
                    Block.spawnAsEntity(worldIn, pos.up(), stack);
                }
            }
        }
    }
}
