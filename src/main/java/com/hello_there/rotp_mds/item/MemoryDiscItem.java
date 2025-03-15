package com.hello_there.rotp_mds.item;

import com.github.standobyte.jojo.init.power.JojoCustomRegistries;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MemoryDiscItem extends Item {
    private static final String POWER_TAG = "NonStandPower";

    public MemoryDiscItem(Properties properties) {
        super(properties);

        DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
            protected ItemStack execute(IBlockSource blockSource, ItemStack stack) {
                if (hasPower(stack)) {
                    NonStandPowerType<?> powerType = getPowerFromStack(stack);
                    if (powerType != null) {
                        if (MCUtil.dispenseOnNearbyEntity(blockSource, stack, entity -> {
                            INonStandPower power = INonStandPower.getNonStandPowerOptional(entity).orElse(null);
                            if (power != null) {
                                if (power.hasPower()) {
                                    power.clear();
                                }
                                return power.givePower(powerType);
                            }
                            return false;
                        }, true)) {
                            return stack;
                        }
                    }
                }
                return super.execute(blockSource, stack);
            }
        });
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        INonStandPower power = INonStandPower.getPlayerNonStandPower(player);

        if (!world.isClientSide()) {
            boolean hadExistingPower = power.hasPower();
            NonStandPowerType<?> existingPowerType = hadExistingPower ? power.getType() : null;
            NonStandPowerType<?> newPowerType = getPowerFromStack(stack);

            if (hadExistingPower) {
                if (!player.abilities.instabuild) {
                    ItemStack prevDisc = withPower(new ItemStack(this), existingPowerType);
                    ItemEntity discEntity = player.drop(prevDisc, false);
                    if (discEntity != null) {
                        discEntity.setPickUpDelay(5);
                        discEntity.setOwner(player.getUUID());
                    }
                }
                power.clear();
            }

            if (newPowerType != null) {
                if (power.givePower(newPowerType)) {
                    if (player.abilities.instabuild) {
                        power.setEnergy(power.getMaxEnergy());
                        if (power.getTypeSpecificData(null).isPresent()) {
                            power.getTypeSpecificData(null).get().onPowerGiven(newPowerType, null);
                        }
                    }

                    if (!player.abilities.instabuild) {
                        stack.shrink(1);
                    }
                    return ActionResult.success(stack);
                }
            }
            else {
                if (hadExistingPower && !player.abilities.instabuild) {
                    stack.shrink(1);
                }
                return ActionResult.success(stack);
            }
        }
        return ActionResult.fail(stack);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(new ItemStack(this));

            for (NonStandPowerType<?> powerType : JojoCustomRegistries.NON_STAND_POWERS.getRegistry()) {
                items.add(withPower(new ItemStack(this), powerType));
            }
        }
    }

    public static ItemStack withPower(ItemStack discStack, NonStandPowerType<?> powerType) {
        discStack.getOrCreateTag().putString(POWER_TAG, powerType.getRegistryName().toString());
        return discStack;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        NonStandPowerType<?> powerType = getPowerFromStack(stack);
        if (powerType != null) {
            return new TranslationTextComponent("item.rotp_mds.memory_disc", powerType.getName());
        }
        return new TranslationTextComponent("item.rotp_mds.empty_memory_disc");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        NonStandPowerType<?> powerType = getPowerFromStack(stack);
        if (powerType != null) {
            tooltip.add(powerType.getName().withStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(new TranslationTextComponent("item.rotp_mds.empty_memory_disc.tooltip")
                    .withStyle(TextFormatting.GRAY));
        }
        tooltip.add(new TranslationTextComponent("item.jojo.creative_only_tooltip").withStyle(TextFormatting.DARK_GRAY));

    }

    @Nullable
    public static NonStandPowerType<?> getPowerFromStack(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(POWER_TAG)) {
            String powerId = stack.getTag().getString(POWER_TAG);
            return JojoCustomRegistries.NON_STAND_POWERS.getRegistry().getValue(new ResourceLocation(powerId));
        }
        return null;
    }

    public static boolean hasPower(ItemStack stack) {
        return getPowerFromStack(stack) != null;
    }
}