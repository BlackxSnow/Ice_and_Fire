package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class MyrmexAIAttackPlayers extends NearestAttackableTargetGoal {
    private EntityMyrmexBase myrmex;

    public MyrmexAIAttackPlayers(EntityMyrmexBase myrmex) {
        super(myrmex, Player.class, 10, true, true, new Predicate<Player>() {
            public boolean apply(@Nullable Player entity) {
                return entity != null && (myrmex.getHive() == null || myrmex.getHive().isPlayerReputationLowEnoughToFight(entity.getUUID()));
            }
        });
        this.myrmex = myrmex;
    }

    public boolean canUse() {
        return myrmex.shouldHaveNormalAI() && super.canUse();
    }
}
