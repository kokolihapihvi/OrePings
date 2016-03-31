package com.kokolihapihvi.orepings.item;

import com.kokolihapihvi.orepings.client.PingTexture;
import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.networking.PacketDispatcher;
import com.kokolihapihvi.orepings.networking.PingMessage;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.util.PingableOre;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class SingleUsePingItem extends ItemOrePings {

    private HashMap<String, TextureAtlasSprite> textures = new HashMap<String, TextureAtlasSprite>();

    public SingleUsePingItem() {
        super("singleUsePing");

        setMaxStackSize(16);
    }

    private String getType(ItemStack stack) {
        String type = "UNKNOWN";

        if(stack.getTagCompound() != null) {
            type = stack.getTagCompound().getCompoundTag("OrePing").getString("ore");
        }

        return type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (String oreDictName : PingableOreRegistry.getList()) {
            ItemStack itemStack = new ItemStack(this, 1);

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ore", oreDictName);

            NBTTagCompound tags = new NBTTagCompound();
            tags.setTag("OrePing", tag);

            itemStack.setTagCompound(tags);

            list.add(itemStack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        String type = getType(stack);

        return super.getItemStackDisplayName(stack).replace("%ore%", PingableOreRegistry.getOre(type).getName());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idklol) {
        PingableOre po = PingableOreRegistry.getOre(getType(stack));
        if(po.enabled) {
            list.add("Range: " + po.range + " blocks");
            list.add("Type: " + getType(stack));
        } else {
            list.add("DISABLED");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        if(register instanceof TextureMap) {
            TextureMap map = (TextureMap)register;

            for (String oreDictName : PingableOreRegistry.getList()) {
                String name = PingTexture.getTextureName(this.getItemName(), oreDictName);

                TextureAtlasSprite texture = map.getTextureExtry(name);
                if (texture == null) {
                    texture = new PingTexture(name);
                    map.setTextureEntry(name, texture);
                }

                textures.put(name, map.getTextureExtry(name));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int damage) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass ) {
        String type = getType(stack);

        if(type == null) {
            return super.getIcon(stack, pass);
        }

        if(pass == 0)
            return textures.get(PingTexture.getTextureName(this.getItemName(), type));

        return BlankPingItem.overlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIcon(stack, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return getIconFromDamage(damage);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote) pingOres(stack, (EntityPlayerMP)player, world);

        if(player.capabilities.isCreativeMode) {
            return stack;
        }

        stack.stackSize--;

        return stack;
    }

    private void pingOres(ItemStack pingStack, EntityPlayerMP player, World world) {
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        PingMessage packet = new PingMessage(getType(pingStack), world.provider.dimensionId, (float)x, (float)y, (float)z);

        int range = ConfigurationHandler.pingRadius;
        List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(x-range, y-range, z-range, x+range, y+range, z+range));

        //Make sure the pinger sees the ping
        if(players.size() == 0) {
            players.add(player);
        }

        for (EntityPlayerMP ply : players) {
            PacketDispatcher.sendTo(ply, packet);
        }
    }
}
