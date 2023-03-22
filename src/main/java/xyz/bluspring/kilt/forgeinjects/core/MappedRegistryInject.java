package xyz.bluspring.kilt.forgeinjects.core;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.kilt.injections.core.MappedRegistryInjection;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(MappedRegistry.class)
public class MappedRegistryInject<T> implements MappedRegistryInjection {
    @Shadow public boolean frozen;

    @Shadow @Final @Nullable private Function<T, Holder.Reference<T>> customHolderProvider;

    @Shadow @Nullable private Map<T, Holder.Reference<T>> intrusiveHolderCache;

    @Inject(method = "registerMapping(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder;", at = @At("HEAD"))
    public void kilt$markRegistryAsKnown(int i, ResourceKey<T> resourceKey, T object, Lifecycle lifecycle, CallbackInfoReturnable<Holder<T>> cir) {
        markKnown();
    }

    @Override
    public void markKnown() {
        MappedRegistryInjection.getKnownRegistries().add(((Registry) (Object) this).key().location());
    }

    @Override
    public void unfreeze() {
        this.frozen = false;
        if (this.customHolderProvider != null && this.intrusiveHolderCache == null)
            this.intrusiveHolderCache = new IdentityHashMap<>();
    }
}
