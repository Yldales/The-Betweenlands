package thebetweenlands.world.biomes;

import net.minecraft.init.Blocks;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;

public class TestBiome extends BiomeGenBaseBetweenlands {
	public TestBiome(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((short)10, (short)30, (short)12);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 10);
		this.setBiomeName("TestBiome");
		this.setBlocks(BLBlockRegistry.betweenstone, Blocks.dirt, Blocks.grass, Blocks.sand);
		this.setFillerBlockHeight((byte)5);
	}
}
