package com.kokolihapihvi.orepings.client.model;

import com.kokolihapihvi.orepings.client.PingTexture;
import com.kokolihapihvi.orepings.item.SingleUsePingItem;
import com.kokolihapihvi.orepings.registry.ItemRegistry;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.util.LogHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public class ModelBakeHandler {

    public static HashMap<String, TextureAtlasSprite> textures = new HashMap<String, TextureAtlasSprite>();

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.map;

        for (String oreDictName : PingableOreRegistry.getList()) {
            String name = PingTexture.getTextureName(ItemRegistry.singleUsePing.getItemName(), oreDictName);

            TextureAtlasSprite texture = map.getTextureExtry(name);
            if (texture == null) {
                texture = new PingTexture(name);
                map.setTextureEntry(name, texture);
            }

            textures.put(name, map.getTextureExtry(name));
        }
    }

    @SubscribeEvent
    public void onBake(ModelBakeEvent event) {
        Object obj = event.modelRegistry.getObject(SingleUsePingItem.modelResourceLocation);
        if(obj instanceof IBakedModel) {
            IBakedModel model = (IBakedModel)obj;
            event.modelRegistry.putObject(SingleUsePingItem.modelResourceLocation, new PingModel(model));
        }
    }
}
