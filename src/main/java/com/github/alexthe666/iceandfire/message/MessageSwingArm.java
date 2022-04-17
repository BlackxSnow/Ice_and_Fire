package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.event.ServerEvents;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

public class MessageSwingArm {

    public MessageSwingArm() {

    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSwingArm message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            Player player = context.get().getSender();
            if(player != null) {
                ServerEvents.onLeftClick(player, player.getItemInHand(InteractionHand.MAIN_HAND));
            }
        }
    }


    public static MessageSwingArm read(FriendlyByteBuf buf) {
        return new MessageSwingArm();
    }

    public static void write(MessageSwingArm message, FriendlyByteBuf buf) {
    }

}
