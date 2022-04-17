package com.github.alexthe666.iceandfire.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class IafDamageRegistry {
    public static final DamageSource GORGON_DMG = new DamageCustomDeathMessage("gorgon");
    public static final DamageSource DRAGON_FIRE = new DamageCustomDeathMessage("dragon_fire");
    public static final DamageSource DRAGON_ICE = new DamageCustomDeathMessage("dragon_ice");
    public static final DamageSource DRAGON_LIGHTNING = new DamageCustomDeathMessage("dragon_lightning");

    static class DamageCustomDeathMessage extends DamageSource{

        public DamageCustomDeathMessage(String damageTypeIn) {
            super(damageTypeIn);
        }

        public Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.msgId;
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? new TranslatableComponent(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : new TranslatableComponent(s1, entityLivingBaseIn.getDisplayName());
        }

    }
}
