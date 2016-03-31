package com.kokolihapihvi.orepings.networking;

import com.kokolihapihvi.orepings.client.PingBlock;
import com.kokolihapihvi.orepings.client.PingRenderer;
import com.kokolihapihvi.orepings.config.ConfigurationHandler;
import com.kokolihapihvi.orepings.registry.PingableOreRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class PingMessage implements IMessage {

    public String ore;
    public int dimension, range;
    public float x, y, z;
    private int duration;

    public PingMessage() {
        ore = "";
        dimension = Integer.MIN_VALUE;
        range = 0;
        x = 0;
        y = 0;
        z = 0;
        duration = 0;
    }

    public PingMessage(String oreName, int dimensionID, float x, float y, float z) {
        this();

        this.ore = oreName;
        this.dimension = dimensionID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ore = ByteBufUtils.readUTF8String(buf);
        range = buf.readInt();
        dimension = buf.readInt();
        x = buf.readFloat();
        y = buf.readFloat();
        z = buf.readFloat();
        duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ore);
        buf.writeInt(PingableOreRegistry.getOre(ore).range);
        buf.writeInt(dimension);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeInt(ConfigurationHandler.pingDuration);
    }

    public static class HANDLER implements IMessageHandler<PingMessage, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(final PingMessage message, MessageContext ctx) {
            //Only receive pings from the same dimension as the player
            if(Minecraft.getMinecraft().thePlayer.dimension != message.dimension) return null;

            int range = message.range;
            String pingOreName = message.ore;
            World world = Minecraft.getMinecraft().theWorld;

            int pingX = (int)message.x;
            int pingY = (int)message.y;
            int pingZ = (int)message.z;

            for(int x = -range; x < range+1; x++) {
                for(int y = -range; y < range; y++) {
                    for(int z = -range-1; z < range; z ++) {
                        int fx = x + pingX;
                        int fy = y + pingY;
                        int fz = z + pingZ;

                        Block b = world.getBlock(fx, fy, fz);

                        if(b.equals(Blocks.air)) {
                            continue;
                        }

                        ItemStack stack = new ItemStack(b, 1, world.getBlockMetadata(fx, fy, fz));

                        int[] oreIds = OreDictionary.getOreIDs(stack);

                        if(oreIds.length == 0) continue;

                        String oreName = OreDictionary.getOreName(oreIds[0]);

                        if(oreName.equals(pingOreName)) {
                            PingBlock pb = new PingBlock(message.duration*(world.isRemote ? 2 : 1), fx, fy, fz, b, world);
                            PingRenderer.AddPing(pb);
                        }
                    }
                }
            }

            return null;
        }
    }
}