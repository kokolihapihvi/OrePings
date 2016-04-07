package com.kokolihapihvi.orepings.client;

import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
    Heavily based on RWTemas Dense Ores
    https://github.com/rwtema/DenseOres/blob/1.7/src/main/java/com/rwtema/denseores/TextureOre.java
 */
@SideOnly(Side.CLIENT)
public class PingTexture extends TextureAtlasSprite {
    private String name;

    public PingTexture(String name) {
        super(name);
        this.name = name;
    }

    private ResourceLocation getLocation() {
        String domain = "minecraft";
        String texname = "oreCoal";

        int ido = name.indexOf('-');
        if(ido > 0) {
            texname = name.substring(ido+1);
        }

        int metadata = PingableOreRegistry.getOre(texname).getDamage();

        ItemStack oreItemStack = OreDictionary.getOres(texname).get(0);
        oreItemStack.setItemDamage(metadata);

        texname = oreItemStack.getItem().delegate.getResourceName().toString();

        ido = texname.indexOf(':');
        if(ido > 0) {
            domain = texname.substring(0, ido).toLowerCase();
            texname = texname.substring(ido+1);

            //IC2 uranium comes out as "blockOreUran:0", so we look out for that!
            ido = texname.indexOf(':');
            if(ido > 0) {
                texname = texname.substring(0, ido);
            }
        }

        LogHelper.info(domain+":"+texname);

        return new ResourceLocation(domain, "textures/blocks/" + texname + ".png");
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        BufferedImage ore_image;
        BufferedImage base_image;

        ResourceLocation oreLocation = getLocation();

        AnimationMetadataSection animation;

        int mips = Minecraft.getMinecraft().gameSettings.mipmapLevels;

        try {
            //load the base image
            IResource baseRes = manager.getResource(new ResourceLocation("orepings","textures/items/singleUsePing.png"));
            base_image = ImageIO.read(baseRes.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();

            return super.load(manager, location);
        }

        try {
            IResource oreRes = manager.getResource(oreLocation);
            ore_image = ImageIO.read(oreRes.getInputStream());

            animation = oreRes.getMetadata("animation");

            LogHelper.info((animation == null ? "no" : animation.getFrameCount())+" animation frames");

            //If the ore icon is higher resolution, upscale the base image
            if(ore_image.getWidth() > base_image.getWidth()) {
                base_image = upscaleImage(base_image, ore_image.getWidth()/base_image.getWidth());
            }
        } catch (Exception e) {
            LogHelper.info("Failed to generate icon " + name + " using " + oreLocation.getResourcePath());

            e.printStackTrace();

            BufferedImage[] output = new BufferedImage[1 + mips];

            output[0] = base_image;

            try {
                loadSprite(output, null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return false;
        }

        BufferedImage[] output = new BufferedImage[1 + mips];

        output[0] = mergeImages(base_image, ore_image);

        LogHelper.info("Output image size: "+output[0].getWidth()+" by "+output[0].getHeight());

        try {
            loadSprite(output, animation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LogHelper.info("Generated icon " + name + " using " + oreLocation.getResourcePath());

        return false;
    }

    private BufferedImage mergeImages(BufferedImage baseImage, BufferedImage oreImage) {
        int w = baseImage.getWidth();
        int h = oreImage.getHeight();

        LogHelper.info("Merging baseImage ("+baseImage.getWidth()+" by "+baseImage.getHeight()+") with oreImage ("+oreImage.getWidth()+" by "+oreImage.getHeight()+")");

        int[] out_data = new int[w * h];
        for (int y = 0; y < h; y += w) {
            baseImage.getRGB(0, 0, w, w, out_data, y * w, w);
        }

        int[] ore_data = new int[w * h];
        oreImage.getRGB(0, 0, w, h, ore_data, 0, w);

        for (int i = 0; i < out_data.length; i++) {
            if(out_data[i] == 0xffffffff) {
                out_data[i] = ore_data[i];
            }
        }

        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        output.setRGB(0, 0, w, h, out_data, 0, w );

        return output;
    }

    private BufferedImage upscaleImage(BufferedImage input, int times) {
        int w = input.getWidth();
        int h = input.getHeight();

        int ow = w*times;
        int oh = h*times;

        LogHelper.info("Upscaling from "+w+" by "+h+" to "+ow+" by "+oh);

        int[] out_data = new int[ow*oh];

        int[] in_data = new int[w*h];
        input.getRGB(0, 0, w, h, in_data, 0, w);

        //Upscale by duplicating pixels
        for(int i = 0; i < in_data.length; ++i) {
            int y = i/w;

            //Make the 1 pixel times by times pixels
            for(int j = 0; j < times; j++) {
                for(int k = 0; k < times; k++) {
                    out_data[i * times + j + (y+k) * ow] = in_data[i];
                }
            }
        }

        BufferedImage output = new BufferedImage(ow, oh, BufferedImage.TYPE_INT_ARGB);
        output.setRGB(0, 0, ow, oh, out_data, 0, ow);

        return output;
    }

    public static String getTextureName(String base, String oreDict) {
        return "orepings:"+base+"-"+oreDict;
    }
}
