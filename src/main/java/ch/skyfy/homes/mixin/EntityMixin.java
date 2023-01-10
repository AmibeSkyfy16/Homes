package ch.skyfy.homes.mixin;

import ch.skyfy.homes.callbacks.EntityMoveCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void onMove(MovementType movementType, Vec3d movement, CallbackInfo callbackInfo) {
        if (EntityMoveCallback.EVENT.invoker().onMove((Entity) (Object) this, movementType, movement) == ActionResult.FAIL) callbackInfo.cancel();
    }

}
