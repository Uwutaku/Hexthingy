package me.uwutaku.hexthingy

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld


class Smite : SpellAction {
    override val argc = 2

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0)
        val dmg = args.getDouble(1)
        env.assertEntityInRange(target)

        val cost = dmg * MediaConstants.SHARD_UNIT * 1.5
        return SpellAction.Result(
            Spell(target, dmg, env.castingEntity!!),
            cost.toLong(),
            listOf(ParticleSpray.cloud(target.pos.add(0.0, target.eyeY / 2.0, 0.0), 1.0))
        )
    }

    private class Spell(val target: LivingEntity, val dmg: Double, val caster: LivingEntity) :
        RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (target.world is ServerWorld) {
                val casterMediaAmount = Hexthingy.getTotalMediaFromEntity(caster)
                val targetMediaAmount = Hexthingy.getTotalMediaFromEntity(target)
                val totalMedia = casterMediaAmount + targetMediaAmount
                var casterDamage = (targetMediaAmount / totalMedia) * dmg.toFloat()
                var targetDamage = (casterMediaAmount / totalMedia) * dmg.toFloat()
                caster.damage(DamageTypes.of(caster.world, DamageTypes.smitecaster_damage), casterDamage)
                target.damage(DamageTypes.of(target.world, DamageTypes.smiterecipient_damage), targetDamage)
            }
        }
    }
}