package com.kokolihapihvi.orepings.item;

import com.kokolihapihvi.orepings.OrePingsMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlankPingItem extends ItemOrePings {

    public static IIcon overlay;

    public BlankPingItem() {
        super("blankPing");
        setMaxStackSize(64);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);

        overlay = register.registerIcon(String.format("%s:%s", OrePingsMod.MODID.toLowerCase(), "pingOverlay"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int damage) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass ) {
        if(pass == 0)
            return super.getIcon(stack, pass);

        return overlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIcon(stack, pass);
    }
}
