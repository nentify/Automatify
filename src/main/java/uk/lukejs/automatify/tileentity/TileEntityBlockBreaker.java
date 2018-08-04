package uk.lukejs.automatify.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import uk.lukejs.automatify.block.BlockBlockBreaker;
import uk.lukejs.automatify.fakeplayer.AutomatifyFakePlayer;

import java.util.function.Function;

public class TileEntityBlockBreaker extends TileEntity implements ITickable {

    private AutomatifyFakePlayer fakePlayer;

    private BlockPos breakingPos;
    private IBlockState breakingState;
    private float progress;

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            fakePlayer = new AutomatifyFakePlayer((WorldServer) world);
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        Item.ToolMaterial toolMaterial = state.getValue(BlockBlockBreaker.TOOL_MATERIAL);

        if (!state.getValue(BlockBlockBreaker.POWERED)) {
            return;
        }

        EnumFacing facing = state.getValue(BlockBlockBreaker.FACING);
        BlockPos newBreakingPos = pos.offset(facing);
        IBlockState newBreakingState = world.getBlockState(newBreakingPos);

        if (!newBreakingPos.equals(breakingPos) || newBreakingState != breakingState) {
            breakingPos = newBreakingPos;
            breakingState = newBreakingState;

            resetBreakProgress();
        }

        if (newBreakingState.getBlock() == Blocks.AIR) {
            return;
        }

        boolean canHarvest = toolMaterial.getHarvestLevel() >= breakingState.getBlock().getHarvestLevel(breakingState);
        System.out.println(toolMaterial.getHarvestLevel() + " " + breakingState.getBlock().getHarvestLevel(breakingState));
        float harvestSpeedDivisor = canHarvest ? 30F : 100F;

        updateBreakProgress(p -> p + (toolMaterial.getEfficiency() / breakingState.getBlockHardness(world, breakingPos) / harvestSpeedDivisor));

        if (progress >= 1F) {
            FakePlayer fakePlayer = new AutomatifyFakePlayer((WorldServer) world);

            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, breakingPos, breakingState, fakePlayer);

            if (!MinecraftForge.EVENT_BUS.post(event) && world.destroyBlock(breakingPos, false))
            {
                if (canHarvest) {
                    NonNullList<ItemStack> stacksToDrop = NonNullList.create();
                    breakingState.getBlock().getDrops(stacksToDrop, world, breakingPos, breakingState, 0);

                    for (ItemStack stack : stacksToDrop) {
                        Block.spawnAsEntity(world, pos.offset(facing.getOpposite()), stack);
                    }
                }
            }

            resetBreakProgress();
        }
    }

    private void resetBreakProgress() {
        progress = 0;
        sendBreakProgress(-1);
    }

    private void updateBreakProgress(Function<Float, Float> update) {
        progress = update.apply(progress);
        sendBreakProgress((int) (progress * 10F));
    }

    private void sendBreakProgress(int progress) {
        System.out.println(this.progress + " " + progress);
        world.sendBlockBreakProgress(
                fakePlayer.getEntityId(),
                breakingPos,
                progress
        );
    }
}
