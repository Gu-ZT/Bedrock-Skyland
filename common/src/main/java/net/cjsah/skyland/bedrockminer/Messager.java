package net.cjsah.skyland.bedrockminer;


import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class Messager {
    public static void actionBar(String message){
        Minecraft minecraftClient = Minecraft.getInstance();
        minecraftClient.gui.setOverlayMessage(Component.translatable(message),false);
    }
    public static void rawactionBar(String message){
        Minecraft minecraftClient = Minecraft.getInstance();
        Component text = Component.literal(message);
        minecraftClient.gui.setOverlayMessage(text,false);
    }

    public static void chat(String message){
        Minecraft minecraftClient = Minecraft.getInstance();
        minecraftClient.gui.getChat().addMessage(Component.translatable(message));
    }
}

