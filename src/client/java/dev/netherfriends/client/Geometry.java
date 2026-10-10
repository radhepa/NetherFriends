package dev.netherfriends.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.netherfriends.NetherFriends;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Builds model layers from {@code assets/netherfriends/geometry/<id>.json}, written by tools/mobkit.py.
 * Parts come parent-first; each has a pivot, a rotation (radians), an optional scale and box-UV cubes.
 *
 * <p>A cube may borrow a vanilla texture ({@code "texture": "entity/wither/wither"}: the Withren's head is the
 * Wither's own). Those cubes are left out of the main layer and go in a second, "borrowed" layer with the same
 * parts, drawn with the game's texture. No vanilla pixels are copied into this mod.
 */
public final class Geometry {
    private Geometry() {}

    /** Texture sizes of the vanilla textures the models borrow. */
    static final Map<String, int[]> BORROWED_SIZES = Map.of("entity/wither/wither", new int[]{64, 64});

    public static ModelLayerLocation layer(String id) {
        return new ModelLayerLocation(NetherFriends.id(id), "main");
    }

    public static ModelLayerLocation borrowedLayer(String id) {
        return new ModelLayerLocation(NetherFriends.id(id), "borrowed");
    }

    private static final Map<String, JsonObject> CACHE = new HashMap<>();

    static JsonObject read(String id) {
        return CACHE.computeIfAbsent(id, k -> {
            String path = "/assets/netherfriends/geometry/" + k + ".json";
            try (InputStream in = Geometry.class.getResourceAsStream(path)) {
                if (in == null) throw new IllegalStateException("missing " + path);
                return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (IOException e) {
                throw new IllegalStateException("can't read " + path, e);
            }
        });
    }

    /** The vanilla texture a model's borrowed cubes use, or null when it borrows nothing. */
    public static @Nullable String borrowed(String id) {
        for (JsonElement el : read(id).getAsJsonArray("parts")) {
            for (JsonElement ce : el.getAsJsonObject().getAsJsonArray("cubes")) {
                JsonObject c = ce.getAsJsonObject();
                if (c.has("texture")) return c.get("texture").getAsString();
            }
        }
        return null;
    }

    /** The model's own cubes (everything not borrowed). */
    public static LayerDefinition load(String id) {
        JsonArray tex = read(id).getAsJsonArray("texture");
        return LayerDefinition.create(build(read(id), null), tex.get(0).getAsInt(), tex.get(1).getAsInt());
    }

    /** Only the cubes drawn with the borrowed vanilla texture, on the same parts. */
    public static LayerDefinition loadBorrowed(String id) {
        String texture = borrowed(id);
        if (texture == null) throw new IllegalArgumentException(id + " borrows no texture");
        int[] size = BORROWED_SIZES.getOrDefault(texture, new int[]{64, 64});
        return LayerDefinition.create(build(read(id), texture), size[0], size[1]);
    }

    /** {@code onlyTexture} null: the model's own cubes; otherwise only the cubes borrowing that texture. */
    static MeshDefinition build(JsonObject json, @Nullable String onlyTexture) {
        MeshDefinition mesh = new MeshDefinition();
        Map<String, PartDefinition> parts = new HashMap<>();
        parts.put("", mesh.getRoot());
        for (JsonElement el : json.getAsJsonArray("parts")) {
            JsonObject p = el.getAsJsonObject();
            CubeListBuilder cubes = CubeListBuilder.create();
            for (JsonElement ce : p.getAsJsonArray("cubes")) {
                JsonObject c = ce.getAsJsonObject();
                String texture = c.has("texture") ? c.get("texture").getAsString() : null;
                if (onlyTexture == null ? texture != null : !onlyTexture.equals(texture)) continue;
                float[] uv = floats(c.getAsJsonArray("uv")), from = floats(c.getAsJsonArray("from")), size = floats(c.getAsJsonArray("size"));
                cubes.texOffs((int) uv[0], (int) uv[1]).mirror(c.get("mirror").getAsBoolean())
                        .addBox(from[0], from[1], from[2], size[0], size[1], size[2], new CubeDeformation(c.get("inflate").getAsFloat()));
            }
            float[] pivot = floats(p.getAsJsonArray("pivot")), rot = floats(p.getAsJsonArray("rotation"));
            float[] scale = p.has("scale") ? floats(p.getAsJsonArray("scale")) : new float[]{1, 1, 1};
            PartDefinition parent = parts.get(p.get("parent").getAsString());
            if (parent == null) throw new IllegalStateException("part " + p.get("name") + " comes before its parent");
            PartPose pose = new PartPose(pivot[0], pivot[1], pivot[2], rot[0], rot[1], rot[2], scale[0], scale[1], scale[2]);
            parts.put(p.get("name").getAsString(), parent.addOrReplaceChild(p.get("name").getAsString(), cubes, pose));
        }
        return mesh;
    }

    private static float[] floats(JsonArray a) {
        float[] out = new float[a.size()];
        for (int i = 0; i < out.length; i++) out[i] = a.get(i).getAsFloat();
        return out;
    }

    static Identifier texture(String id) {
        return NetherFriends.id("textures/entity/" + id + ".png");
    }

    static boolean hasGlow(String textureId) {
        return Geometry.class.getResource("/assets/netherfriends/textures/entity/" + textureId + "_glow.png") != null;
    }
}
