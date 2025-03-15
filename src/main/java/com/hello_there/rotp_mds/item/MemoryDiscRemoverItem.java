package com.hello_there.rotp_mds.item;

import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.hello_there.rotp_mds.init.InitItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MemoryDiscRemoverItem extends Item {
    private final boolean oneTimeUse;
    private final Mode mode;

    public MemoryDiscRemoverItem(Properties properties, Mode mode, boolean oneTimeUse) {
        super(properties);
        this.mode = mode;
        this.oneTimeUse = oneTimeUse;

        DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
            protected ItemStack execute(IBlockSource blockSource, ItemStack stack) {
                if (MCUtil.dispenseOnNearbyEntity(blockSource, stack, entity -> {
                    INonStandPower power = INonStandPower.getNonStandPowerOptional(entity).orElse(null);
                    return power != null && useOn(entity, power);
                }, oneTimeUse)) {
                    return stack;
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
            if (useOn(player, power)) {
                if (oneTimeUse && !player.abilities.instabuild) {
                    stack.shrink(1);
                }
                return ActionResult.success(stack);
            }
            return ActionResult.fail(stack);
        }
        return ActionResult.pass(stack);
    }

    private boolean useOn(LivingEntity entity, INonStandPower power) {
        if (power.hasPower()) {
            switch (mode) {
                case REMOVE:
                    power.clear();
                    break;
                case EJECT:
                    Optional<NonStandPowerType<?>> previousPower = power.hasPower() ?
                            Optional.of(power.getType()) : Optional.empty();
                    power.clear();
                    previousPower.ifPresent(prevPower -> {
                        ItemStack discStack = MemoryDiscItem.withPower(new ItemStack(InitItems.MEMORY_DISC.get()), prevPower);
                        MCUtil.giveItemTo(entity, discStack, true);
                    });
                    break;
                case FULL_CLEAR:
                    power.clear();
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (mode == Mode.FULL_CLEAR) {
            tooltip.add(new TranslationTextComponent("item.rotp_mds.memory_disc.full_clear.hint").withStyle(TextFormatting.GRAY));
        }
        tooltip.add(new TranslationTextComponent("item.jojo.creative_only_tooltip").withStyle(TextFormatting.DARK_GRAY));
    }

    public enum Mode {
        REMOVE,
        EJECT,
        FULL_CLEAR
    }
}