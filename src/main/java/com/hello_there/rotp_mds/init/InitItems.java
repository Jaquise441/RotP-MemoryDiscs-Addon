package com.hello_there.rotp_mds.init;
import static com.github.standobyte.jojo.init.ModItems.MAIN_TAB;

import com.hello_there.rotp_mds.RotpMemoryDiscsAddon;
import com.hello_there.rotp_mds.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RotpMemoryDiscsAddon.MOD_ID);

    public static final RegistryObject<MemoryDiscItem> MEMORY_DISC = ITEMS.register("memory_disc",
            () -> new MemoryDiscItem(new Item.Properties().tab(MAIN_TAB).stacksTo(1)));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_REMOVER = ITEMS.register("memory_remover",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(1),
                    MemoryDiscRemoverItem.Mode.REMOVE, false));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_REMOVER_ONE_TIME = ITEMS.register("memory_remover_one_time",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(64),
                    MemoryDiscRemoverItem.Mode.REMOVE, true));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_EJECT = ITEMS.register("memory_eject",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(1),
                    MemoryDiscRemoverItem.Mode.EJECT, false));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_EJECT_ONE_TIME = ITEMS.register("memory_eject_one_time",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(64),
                    MemoryDiscRemoverItem.Mode.EJECT, true));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_FULL_CLEAR = ITEMS.register("memory_full_clear",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(1),
                    MemoryDiscRemoverItem.Mode.FULL_CLEAR, false));

    public static final RegistryObject<MemoryDiscRemoverItem> MEMORY_FULL_CLEAR_ONE_TIME = ITEMS.register("memory_full_clear_one_time",
            () -> new MemoryDiscRemoverItem(new Item.Properties().tab(MAIN_TAB).stacksTo(64),
                    MemoryDiscRemoverItem.Mode.FULL_CLEAR, true));

    public static void register(IEventBus modEventbus) {
        ITEMS.register(modEventbus);
    }
}
