package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageSetMyrmexHiveNull {

    public MessageSetMyrmexHiveNull() {
    }

    public static MessageSetMyrmexHiveNull read(FriendlyByteBuf buf) {
        return new MessageSetMyrmexHiveNull();
    }

    public static void write(MessageSetMyrmexHiveNull message, FriendlyByteBuf buf) {
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSetMyrmexHiveNull message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if (player != null) {
                IceAndFire.PROXY.setReferencedHive(null);
            }
        }
    }
}