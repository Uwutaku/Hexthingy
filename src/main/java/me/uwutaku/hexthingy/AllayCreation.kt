package me.uwutaku.hexthingy

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.AllayEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text

class AllayCreation : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0)
        if(!(target.type.equals(EntityType.VEX))){
            throw MishapBadEntity(target, Text.of("Vex"))
        }

        env.assertEntityInRange(target)
        val cost = MediaConstants.CRYSTAL_UNIT * 15
        return SpellAction.Result(
            Spell(target),
            cost,
            listOf(ParticleSpray.cloud(target.pos.add(0.0, target.eyeY / 2.0, 0.0), 1.0))
        )
    }

    private class Spell(val entity: LivingEntity) :
        RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val serverWorld = entity.world as? ServerWorld ?: return
            val pos = entity.blockPos
            entity.remove(Entity.RemovalReason.DISCARDED)
            val allay = AllayEntity(EntityType.ALLAY, serverWorld)
            allay.setPosition(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5)
            serverWorld.spawnEntity(allay)
        }
    }
}