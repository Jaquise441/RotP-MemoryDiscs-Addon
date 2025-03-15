package com.hello_there.rotp_mds;


import com.hello_there.rotp_mds.init.InitItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RotpMemoryDiscsAddon.MOD_ID)
public class RotpMemoryDiscsAddon {
    public static final String MOD_ID = "rotp_mds";
    public static final Logger LOGGER = LogManager.getLogger();

    public RotpMemoryDiscsAddon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        InitItems.ITEMS.register(modEventBus);
    }
    public static Logger getLogger() {
        return LOGGER;
    }
}
