package com.magicrealms.magiclib.paper.controller;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.Listener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import org.bukkit.command.CommandSender;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2024-07-16
 */
@Listener
@SuppressWarnings("unused")
public class TestController {


    @Command(text = "^hello$")
    public void hello(CommandSender sender, String[] args) {
        System.out.println("hello");

    }


    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
//        Player player = ((Player) sender);


//        player.getWorld().spawnEntity(player.getLocation(), EntityType.values());


//        new InputMenu.Builder().plugin(MagicLib.getInstance())
//                .player((Player) sender)
//                .customValidator(v -> {
//                    InputValidatorResult result = new InputValidatorResult();
//                    if (StringUtils.isBlank(v)) {
//                        result.setMessage("您为啥输入空啊");
//                        return result;
//                    }
//                    if (v.equals("炼奶")) {
//                        result.setMessage("你凭啥输入自己的名字啊？");
//                        return result;
//                    }
//                    if (v.equals("宁睿")) {
//                        result.setMessage("你凭啥输入我的名字啊？");
//                        return result;
//                    }
//                    result.setMessage("你输入的文字符合");
//                    result.setValidator(true);
//                    return result;
//                })
//                .resultSlot(1)
//                .itemStack(new ItemStack(Material.STONE))
//                .cancelTask(() -> {})
//                .confirmConsumer(e -> MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(),
//                        sender, e))
//                .open();
    }

}
