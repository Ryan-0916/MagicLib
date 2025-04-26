package com.magicrealms.magiclib.bukkit.command.annotations;

import com.magicrealms.magiclib.bukkit.command.enums.CommandRule;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan-0916
 * @Desc 申明方法一条可执行的 Minecraft 指令
 * 必须在类注解 @CommandListener 中才生效
 * @date 2023-10-01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SuppressWarnings("unused")
public @interface Command {

    /**
     * 判断指令准确性的方式，参数如下
     * REGEX : 正则表达式 (严格区分大小写)
     * CASE_INSENSITIVE_REGEX : 正则表达式 (不区分大小写)
     */
    CommandRule rule() default CommandRule.CASE_INSENSITIVE_REGEX;

    /**
     * 玩家权限，Minecraft 本身角色的身份，参数如下
     * ALL : 全部角色 （包括 玩家、管理员、控制台）
     * PLAYER : 玩家 （包括 玩家、管理员）
     * PERMISSION : 权限节点，当为权限节点时则会按照 permission 参数中的权限文本进行校验
     * ADMIN : 仅管理员
     * CONSOLE : 仅控制台
     * OP : OP (包括 管理员、控制台)
     */
    PermissionType permissionType() default PermissionType.ALL;

    /* 匹配文本正则表达式 */
    String text();

    /* Minecraft 权限组，当玩家的权限必须满足时才可能进入指令处理 */
    String permission() default "";

    /* 用于控制 Minecraft 插件标签，例如 /mc 指令 mc 则是插件标签 */
    String label() default "";

}
