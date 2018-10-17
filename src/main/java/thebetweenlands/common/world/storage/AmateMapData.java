package thebetweenlands.common.world.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.MathUtils;

import java.util.*;

public class AmateMapData extends MapData {
    public Set<BLMapDecoration> blDecorations = new TreeSet<>();
    private Int2ObjectArrayMap<BLMapDecoration.Location> usedSpaces = new Int2ObjectArrayMap<>();

    public AmateMapData(String mapname) {
        super(mapname);
        usedSpaces.defaultReturnValue(BLMapDecoration.Location.NONE);
    }

    public void addDecoration(BLMapDecoration deco) {
        int x = deco.getX();
        int y = deco.getY();
        Int2ObjectArrayMap<BLMapDecoration.Location> tmp = new Int2ObjectArrayMap<>();
        int area = 5;
        for (int i = -area; i <= area; i++) {
            for (int j = -area; j <= area; j++) {
                int index = (x + i) + (y + j) * 128;
                tmp.put(index, deco.location);
                if (usedSpaces.get(index) == deco.location) {
                    return;
                }
            }
        }
        usedSpaces.putAll(tmp);
        blDecorations.add(deco);
    }

    public void updateMapTexture() {
        MapItemRenderer mapItemRenderer = Minecraft.getMinecraft().entityRenderer.getMapItemRenderer();
        MapItemRenderer.Instance instance = mapItemRenderer.getMapRendererInstance(this);
        for (int i = 0; i < 16384; ++i) {
            int j = this.colors[i] & 255;

            if (j / 4 == 0) {
                instance.mapTextureData[i] = changeColor((i + i / 128 & 1) * 8 + 16 << 24);
            } else {
                instance.mapTextureData[i] = changeColor(MapColor.COLORS[j / 4].getMapColor(j & 3));
            }
        }
        instance.mapTexture.updateDynamicTexture();
    }

    private int changeColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int rs = MathHelper.clamp((int) ((0.293 * r + 0.269 * g + 0.089 * b) * 0.8D), 0, 255);
        int rg = MathHelper.clamp((int) ((0.049 * r + 0.386 * g + 0.128 * b) * 0.8D), 0, 255);
        int rb = MathHelper.clamp((int) ((0.072 * r + 0.034 * g + 0.111 * b) * 0.8D), 0, 255);
        return rs << 16 | rg << 8 | rb;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte[] locationStorage = nbt.getByteArray("locations");
        if (locationStorage.length > 0) {
            deserializeLocations(locationStorage);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (blDecorations.size() > 0) {
            compound.setByteArray("locations", serializeLocations());
        }

        return compound;
    }

    public void deserializeLocations(byte[] arr) {
        this.blDecorations.clear();
        this.usedSpaces.clear();

        for (int i = 0; i < arr.length / 3; ++i) {
            byte id = arr[i * 3];
            byte mapX = arr[i * 3 + 1];
            byte mapZ = arr[i * 3 + 2];
            byte mapRotation = 8;

            addDecoration(new BLMapDecoration(BLMapDecoration.Location.byId(id), mapX, mapZ, mapRotation));
        }
    }

    public byte[] serializeLocations() {
        byte[] storage = new byte[this.blDecorations.size() * 3];

        int i = 0;
        for (BLMapDecoration location : blDecorations) {
            storage[i * 3] = location.location.id;
            storage[i * 3 + 1] = location.getX();
            storage[i * 3 + 2] = location.getY();
            i++;
        }

        return storage;
    }

    public static class BLMapDecoration extends MapDecoration implements Comparable<BLMapDecoration> {

        private static final ResourceLocation MAP_ICONS = new ResourceLocation(ModInfo.ID, "textures/gui/map_icon_sheet.png");
        private final Location location;

        public BLMapDecoration(Location location, byte xIn, byte yIn, byte rotationIn) {
            super(Type.TARGET_X, xIn, yIn, rotationIn);
            this.location = location;
        }

        @Override
        public boolean render(int index) {
            Minecraft.getMinecraft().renderEngine.bindTexture(MAP_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F + getX() / 2.0F + 64.0F, 0.0F + getY() / 2.0F + 64.0F, -0.02F);
            GlStateManager.rotate((float) (getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            if (location == Location.FORTRESS) {
                GlStateManager.scale(5.0F, 5.0F, 4.0F);
            } else if (location == Location.CHECK) {
                GlStateManager.scale(2.5F, 2.5F, 2.5F);
                GlStateManager.translate(0.0F, -0.6F, 0.0F);
            } else {
                GlStateManager.scale(4.0F, 4.0F, 3.0F);
            }
            GlStateManager.translate(-0.125F, 0.125F, 0.0F);

            float f1 = location.x;
            float f2 = location.y;
            float f3 = location.x2;
            float f4 = location.y2;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-1.0D, 1.0D, (float) index * -0.001F).tex((double) f1, (double) f2).endVertex();
            bufferbuilder.pos(1.0D, 1.0D, (float) index * -0.001F).tex((double) f3, (double) f2).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, (float) index * -0.001F).tex((double) f3, (double) f4).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, (float) index * -0.001F).tex((double) f1, (double) f4).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof BLMapDecoration))
                return false;
            if (!super.equals(o))
                return false;
            BLMapDecoration that = (BLMapDecoration) o;
            return location == that.location;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), location);
        }

        @Override
        public int compareTo(BLMapDecoration o) {
            return this.location == Location.CHECK ? 1 : o.location != Location.CHECK ? Integer.compare(this.location.id, ((BLMapDecoration) o).location.id) : -1;
        }

        public enum Location {
            NONE(0, 0, 0, 0, 0),
            PORTAL(1, 0, 0, 16, 16),
            SPAWN(2, 16, 0, 16, 16),
            SHRINE(3, 32, 0, 16, 16),
            GIANT_TREE(4, 48, 0, 16, 16),
            RUINS(5, 64, 0, 16, 16),
            TOWER(6, 80, 0, 16, 16),
            IDOL(7, 96, 0, 16, 16),
            WAYSTONE(8, 112, 0, 16, 16),
            BURIAL_MOUND(9, 0, 16, 16, 16),
            SPIRIT_TREE(10, 16, 16, 16, 16),
            FORTRESS(11, 0, 104, 22, 24),

            CHECK(12, 0, 32, 16, 16);

            private float x;
            private float y;
            private float x2;
            private float y2;
            
            private final byte id;

            Location(int id, int x, int y, int width, int height) {
            	this.id = (byte) id;
                this.x = MathUtils.linearTransformf(x, 0, 128, 0, 1);
                this.y = MathUtils.linearTransformf(y, 0, 128, 0, 1);
                this.x2 = MathUtils.linearTransformf(width + x, 0, 128, 0, 1);
                this.y2 = MathUtils.linearTransformf(height + y, 0, 128, 0, 1);
            }
            
            public static Location byId(int id) {
            	for(Location location : values()) {
            		if(location.id == id) {
            			return location;
            		}
            	}
            	return NONE;
            }

            public static Location getLocation(LocationStorage storage) {
                if (storage instanceof LocationPortal) {
                    return PORTAL;
                }
                String name = storage.getName();
                switch (name) {
                    case "small_dungeon":
                        return SHRINE;
                    case "giant_tree":
                        return GIANT_TREE;
                    case "abandoned_shack":
                    case "ruins":
                        return RUINS;
                    case "cragrock_tower":
                        return TOWER;
                    case "idol_head":
                        return IDOL;
                    case "wight_tower":
                        return FORTRESS;
                    case "spirit_tree":
                        return SPIRIT_TREE;
                }
                return NONE;
            }
        }
    }
}