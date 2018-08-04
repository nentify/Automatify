package uk.lukejs.automatify.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class AutomatifyFakePlayer extends FakePlayer {

    private static GameProfile NAME = new GameProfile(UUID.fromString("949f9a21-00be-4f77-b85f-4f3897b1a17e"), "[Automatify]");

    public AutomatifyFakePlayer(WorldServer world) {
        super(world, NAME);
    }
}
