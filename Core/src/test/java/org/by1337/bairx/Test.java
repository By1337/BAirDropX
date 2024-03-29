package org.by1337.bairx;

import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.minimessage.MiniMessage;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.TextComponent;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.by1337.blib.core.text.LegacyConvertorImpl;
import org.by1337.blib.nbt.NBT;
import org.by1337.blib.nbt.NBTParser;
import org.by1337.blib.text.LegacyConvertor;

public class Test {
    @org.junit.Test
    public void run(){
//
   //     Component componentV147 = new LegacyConvertorImpl().convert("Hello <<<rainbow>world</rainbow>, isn't <blue><u><click:open_url:'https://docs.advntr.dev/minimessage'>MiniMessage</click></u></blue> fun?");

    //    System.out.println(componentV147);

      //  final Component component = MiniMessage.miniMessage().deserialize("Hello <<<rainbow>world</rainbow>, isn't <blue><u><click:open_url:'https://docs.advntr.dev/minimessage'>MiniMessage</click></u></blue> fun?");
       // final Component component = MiniMessage.miniMessage().deserialize("<color:#0dfb00>HEX text </color><green>Green</green><red>red &#</red><color:#0dfb00></color>");


       // Component component = Component.text("123123");

      //  String s = GsonComponentSerializer.gson().serializer().toJson(component);
//        System.out.println(s);
//        NBT nbt = NBTParser.parseNBT(s);
//
     //   System.out.println(toString(component));


    }

    public static String toString(Component component){
        if (!(component instanceof TextComponent textComponent)) return "";
        StringBuilder sb = new StringBuilder();

        sb.append(textComponent.content());
        for (Component child : textComponent.children()) {
            sb.append(toString(child));
        }
        return sb.toString();
    }
}
