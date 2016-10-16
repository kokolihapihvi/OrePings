package com.kokolihapihvi.orepings.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.networking.PacketDispatcher;
import com.kokolihapihvi.orepings.networking.PingMessage;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import com.kokolihapihvi.orepings.util.LogHelper;
import com.kokolihapihvi.orepings.util.PingableOre;

public class SingleUsePingItem extends ItemOrePings {
    private HashMap<String, TextureAtlasSprite> textures = new HashMap<String, TextureAtlasSprite>();

    public SingleUsePingItem() {
        super("singleUsePing");

        setHasSubtypes(true);
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
        
        if(type.equals("UNKNOWN")) {
        	return super.getItemStackDisplayName(stack).replace("%ore%", type);
        } else {        	
        	return super.getItemStackDisplayName(stack).replace("%ore%", PingableOreRegistry.getOre(type).getName());
        }
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(!world.isRemote) pingOres(stack, (EntityPlayerMP)player, world);

        if(player.capabilities.isCreativeMode) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }

        stack.stackSize--;

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    private void pingOres(ItemStack pingStack, EntityPlayerMP player, World world) {
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        PingMessage packet = new PingMessage(getType(pingStack), world.provider.getDimension(), (float)x, (float)y, (float)z);

        int range = ConfigurationHandler.pingRadius;
        AxisAlignedBB aabb = new AxisAlignedBB(x-range, y-range, z-range, x+range, y+range, z+range);
        List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, aabb);

        //Make sure the pinger sees the ping
        if(players.size() == 0) {
            players.add(player);
        }

        for (EntityPlayerMP ply : players) {
            PacketDispatcher.sendTo(ply, packet);
        }
    }
}
