package ch.skyfy.homes.features

import ch.skyfy.homes.api.config.BonusEffect
import ch.skyfy.homes.api.events.PlayerTeleportationEvents
import ch.skyfy.homes.api.events.TeleportationCancelled
import ch.skyfy.homes.api.events.TeleportationDone
import ch.skyfy.homes.api.events.TeleportationStandStillStarted
import ch.skyfy.homes.api.utils.getBonus
import ch.skyfy.homes.api.utils.getRule
import ch.skyfy.homes.api.utils.getPlayer
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity

object BonusFeature {

    init {
        fun giveEffect(player: ServerPlayerEntity, bonusEffect: BonusEffect) {
            Registries.STATUS_EFFECT.keys.firstOrNull { it.value.toString() == bonusEffect.name }?.let {
                Registries.STATUS_EFFECT.get(it)?.let { statusEffect ->
                    player.addStatusEffect(StatusEffectInstance(statusEffect, bonusEffect.duration * 20, bonusEffect.amplifier))
                }
            }
        }

        PlayerTeleportationEvents.TELEPORTATION_DONE.register(TeleportationDone { player, rule ->
            getBonus(rule)?.map?.get("TELEPORTATION_DONE")?.forEach { bonusEffect ->
                giveEffect(player, bonusEffect)
            }
        })

        PlayerTeleportationEvents.TELEPORTATION_STANDSTILL_STARTED.register(TeleportationStandStillStarted { player, rule ->
            getBonus(rule)?.map?.get("TELEPORTATION_STANDSTILL_STARTED")?.forEach { bonusEffect ->
                giveEffect(player, bonusEffect)
            }
        })

        PlayerTeleportationEvents.TELEPORTATION_CANCELLED.register(TeleportationCancelled { spe ->
            getPlayer(spe)?.let {
                getRule(it)?.let { rule ->
                    getBonus(rule)?.map?.get("TELEPORTATION_CANCELLED")?.forEach { bonusEffect ->
                        giveEffect(spe, bonusEffect)
                    }
                }
            }
        })
    }

}