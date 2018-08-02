package uk.lukejs.automatify;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import uk.lukejs.automatify.block.BlockBlockBreaker;

@Mod(modid = "automatify")
public class Automatify {

    private Logger logger;

    @GameRegistry.ObjectHolder("automatify:block_breaker")
    public static final Block blockBreaker = null;

    public Automatify() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockBlockBreaker().setCreativeTab(CreativeTabs.REDSTONE)
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(blockBreaker).setRegistryName(blockBreaker.getRegistryName())
        );
    }
}
